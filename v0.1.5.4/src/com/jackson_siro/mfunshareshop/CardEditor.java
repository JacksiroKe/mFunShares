package com.jackson_siro.mfunshareshop;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CardEditor extends AppCompatActivity {
    private ImageView mImageView;
    private Button mInsertImageButton;
    private Button mSaveImageButton;
    private Button mWriteOnImage;
    private TextView mTextOnImage;
    private final int RESULT_LOAD_IMAGE = 20;
    private String userInputValue = "";
    private String imagePath = "";
    private Uri pickedImage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mImageView = (ImageView)findViewById(R.id.main_background);
        mInsertImageButton = (Button)findViewById(R.id.insert_image);
        mSaveImageButton = (Button)findViewById(R.id.save_image);
        mWriteOnImage = (Button)findViewById(R.id.write_on_image);
        mTextOnImage = (TextView)findViewById(R.id.text_on_image);
        mInsertImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        mWriteOnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTextBox();
            }
        });
        mSaveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInputValue.equals("") || userInputValue.isEmpty()) {
                    return;
                }
                /*if(imagePath.isEmpty() || imagePath.equals("")){
                    return;
                }*/
                //Bitmap bm = writeTextOnDrawable(userInputValue).getBitmap();
                Bitmap bm = ProcessingBitmap(userInputValue);
                String pathLink = Environment.getExternalStorageDirectory() + File.separator + "testing.jpg";
                storeImage(bm, pathLink);
                //mImageView.setImageBitmap(bm);
                Toast.makeText(CardEditor.this, userInputValue, Toast.LENGTH_LONG).show();
            }
        });
    }
    @SuppressLint("NewApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            mImageView.setBackground(convertImagePathToDrawable(imagePath));
        }
    }
    private Drawable convertImagePathToDrawable(String imagePath){
        Drawable d = Drawable.createFromPath(imagePath);
        return d;
    }
    private void displayTextBox(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.card_editor_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText textContent = (EditText) dialogView.findViewById(R.id.add_text_on_image);
        dialogBuilder.setTitle("");
        dialogBuilder.setMessage("");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userInputValue = textContent.getText().toString();
                if(!userInputValue.equals("") || !userInputValue.isEmpty()){
                    // assign the content to the TextView object
                    mTextOnImage.setText(userInputValue);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    
    private Bitmap ProcessingBitmap(String captionString) {
    Bitmap bm1 = null;
    Bitmap newBitmap = null;
    try {
        Toast.makeText(CardEditor.this, pickedImage.getPath(), Toast.LENGTH_LONG).show();
        bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(pickedImage));
        Bitmap.Config config = bm1.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bm1, 0, 0, null);
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(50);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);
        Rect textRect = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), textRect);
        if(textRect.width() >= (canvas.getWidth() - 4))
            //paintText.setTextSize(convertToPixels(7));
        	paintText.setTextSize(10);
        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight() / 2) - ((paintText.descent() + paintText.ascent()) / 2)) ;
        canvas.drawText(captionString, xPos, yPos, paintText);
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return newBitmap;
}

private void storeImage(Bitmap mBitmap, String path) {
    OutputStream fOut = null;
    File file = new File(path);
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
package com.jackson_siro.mfunshareshop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.jackson_siro.mfunshareshop.tools.*;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BdCardMaker extends ActionBarActivity {


	public static final String VSB_SETTINGS = "Card_Settings";
	public static final String FONT_SIZE = "font_size";
	
	TextView mCardText;
	MfssDatabase db;
	ImageView mCardView;
	Bitmap bitmap;
	
	//private View mCardView;
	@SuppressLint("SdCardPath")
	private final String IMG_PATH = "/sdcard/AppSmata/mFunShares/cards/";
	private final String IMG_PATHi = "/AppSmata/mFunShares/cards/";
	private final String SENT_PATH = "/sdcard/AppSmata/mFunShares/sent/";
	private final String SENT_PATHi = "/AppSmata/mFunShares/sent/";

	public String CardImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_maker);

		ScrollView ScrollCard = (ScrollView) findViewById(R.id.ScrollCard);
		getLayoutInflater().inflate(R.layout.card_maker_scr, ScrollCard);

		//mCardText = (TextView) findViewById(R.id.mytext);
		mCardView = (ImageView)findViewById(R.id.myimage);

		Intent intent = getIntent();
		CardImage = intent.getStringExtra("mfss_card_img");
		
		String imagePath = Environment.getExternalStorageDirectory().toString() + IMG_PATHi + CardImage;
		mCardView.setImageDrawable(Drawable.createFromPath(imagePath));	
	}

	public void ShareImage(){
		
	}
	
	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.card_maker, menu);
	       
	       return true;
	   }
    
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       switch (item.getItemId()) {
	       		case android.R.id.home:
	       			onBackPressed();
	       			return true; 
	            	            
		        case R.id.menu_share:
		        	ShareImage();
	               return true;
	               
		        case R.id.menu_refresh:
		        					
	               return true;
	       
	           default:
	               return false;
	       }
	   }  
	   

	    public void saveChart(Bitmap getbitmap, float height, float width){
	        
			File dir = new File(Environment.getExternalStorageDirectory().toString() + SENT_PATHi);
			dir.mkdirs();

			File file = new File(dir, "funshare_" + System.currentTimeMillis() + ".png");
			
			/* if ( !file.exists() )
	        {
	            try {
	                success = file.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }*/

	        FileOutputStream ostream = null;
	        try
	        {
	            ostream = new FileOutputStream(file);

	            System.out.println(ostream);

	            Bitmap well = getbitmap;
	            Bitmap save = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
	            Paint paint = new Paint();
	            paint.setColor(Color.WHITE);
	            Canvas now = new Canvas(save);
	            now.drawRect(new Rect(0,0,(int) width, (int) height), paint);
	            now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,(int) width, (int) height), null);


	            if(save == null) {
	                System.out.println("NULL bitmap save\n");
	            }
	            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

	        }catch (NullPointerException e)
	        {
	            e.printStackTrace();
	            //Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
	        }

	        catch (FileNotFoundException e)
	        {
	            e.printStackTrace();
	            // Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
	        }

	        catch (IOException e)
	        {
	            e.printStackTrace();
	            // Toast.makeText(getApplicationContext(), "IO error", Toast.LENGTH_SHORT).show();
	        }
	    }

	    public Bitmap getBitmap(LinearLayout layout)
	    {

	        layout.setDrawingCacheEnabled(true);
	        layout.buildDrawingCache();
	        Bitmap bmp = Bitmap.createBitmap(layout.getDrawingCache());
	        layout.setDrawingCacheEnabled(false);

	        return bmp;
	    }
}
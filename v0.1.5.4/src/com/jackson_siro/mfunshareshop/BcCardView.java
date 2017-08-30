package com.jackson_siro.mfunshareshop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BcCardView extends ActionBarActivity {


	public static final String VSB_SETTINGS = "Card_Settings";
	public static final String FONT_SIZE = "font_size";
	
	TextView Title, Content;
	MfCard selectedCard;
	MfssDatabase db;
	ImageView mCardView;
	Bitmap bitmap;
	
	public String CardImage;
	
	private View mProgressView;
	//private View mCardView;
	@SuppressLint("SdCardPath")
	private final String IMG_PATH = "/sdcard/AppSmata/mFunShares/cards/";
	private final String IMG_PATHi = "/AppSmata/mFunShares/cards/";
	private final String SENT_PATH = "/sdcard/AppSmata/mFunShares/sent/";
	private final String SENT_PATHi = "/AppSmata/mFunShares/sent/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_view);

		Title = (TextView) findViewById(R.id.title);
		Content = (TextView) findViewById(R.id.description);
		mCardView = (ImageView)findViewById(R.id.card_image);

		mProgressView = findViewById(R.id.loading_progress);
		
		Intent intent = getIntent();
		int cardid = intent.getIntExtra("mfss_card", -1);

		db = new MfssDatabase(getApplicationContext());
		selectedCard = db.readCard(cardid);
		
		setTitle(selectedCard.getCardtitle());
		Title.setText(selectedCard.getCardtitle());
		Content.setText(selectedCard.getCardcontent());
		
		new DownloadFileFromURL().execute(selectedCard.getCardimage());
		
	}

	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}

		@Override
		protected String doInBackground(String... f_url) {
			int count;
	        try {
	            URL url = new URL(f_url[0]);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();

	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	            String[] imagefile = TextUtils.split(selectedCard.getCardimage(), "js_media/");
	    		OutputStream output = new FileOutputStream(IMG_PATH + imagefile[1]);

	            byte data[] = new byte[1024];
	            long total = 0;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                //publishProgress(""+(int)((total*100)/lenghtOfFile));
	                output.write(data, 0, count);
	            }
	            output.flush();
	            output.close();
	            input.close();
	            
	        } catch (Exception e) {
	        	Log.e("Error: ", e.getMessage());
	        }
	        
	        return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			showProgress(false);
			String[] imagefile = TextUtils.split(selectedCard.getCardimage(), "js_media/");    		
			String imagePath = Environment.getExternalStorageDirectory().toString() + IMG_PATHi + imagefile[1];
			mCardView.setImageDrawable(Drawable.createFromPath(imagePath));	
			CardImage = imagefile[1];
		}		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mCardView.setVisibility(show ? View.GONE : View.VISIBLE);
			mCardView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCardView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});
		} else {
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mCardView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public final boolean isInternetOn() {
    	
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		
			if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
			     connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
			   
				//DownloadFromUrl(IMG_PATH);	
			    return true;
			    
			} else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
				NotConnected();				
			    return false;
			}
		  return false;
		}

	protected void NotConnected() {
		
	}
	
	public void ShareImage(){

		Bitmap bitmap;
		OutputStream output;
		
		String[] imagefile = TextUtils.split(selectedCard.getCardimage(), "js_media/");    		
		String imagePath = Environment.getExternalStorageDirectory().toString() + IMG_PATHi + imagefile[1];
		
		bitmap = BitmapFactory.decodeFile(imagePath);
		String filePath = Environment.getExternalStorageDirectory().toString() + SENT_PATHi;
		
		File dir = new File(filePath);
		dir.mkdirs();

		File file = new File(dir, "funshare_" + System.currentTimeMillis() + ".png");

		try {
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			output = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
			output.flush();
			output.close();

			share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			startActivity(Intent.createChooser(share, "FunShare This"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.card_view, menu);
	       
	       return true;
	   }

	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       switch (item.getItemId()) {
	       
		        case R.id.menu_share:
		        	if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_logged_in_user", false)) {
		        		startActivity(new Intent(this, SignIn.class));
		      		} else {
		      			ShareImage();  			
		      		}
	               return true;
	               
		        case R.id.menu_edit:
		        	if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_logged_in_user", false)) {
		        		startActivity(new Intent(this, SignIn.class));
		      		} else {
		      			Intent intent = new Intent(BcCardView.this, BdCardMaker.class);
		    			intent.putExtra("mfss_card_img", CardImage);
		    			startActivityForResult(intent, 1);
		      		}				
	               return true;
	       
	           default:
	               return false;
	       }
	   } 
	   
}
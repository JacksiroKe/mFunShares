package com.jackson_siro.mfunshareshop;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AaSplash extends Activity {

	private TextView mytext1;
	private ImageView myimage;
	
	private long ms=0;
	private long splashTime=5000;
	private boolean splashActive = true;
	private boolean paused=false;
	RelativeLayout MyNote;
	private static final String LOG_TAG = "Splash";

	   @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aa_splash);
		SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
	    
	    isExternalStoragepresent();
		createDirIfNotExits("AppSmata/mFunShares/cards");
		createDirIfNotExits("AppSmata/mFunShares/sent");
		
		mytext1 = (TextView) findViewById(R.id.text1);
		myimage = (ImageView) findViewById(R.id.image);

		Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
		myimage.startAnimation(animation1);

		Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade2);
		mytext1.startAnimation(animation3);

		setFirstUse();
		
		//localEditor.putString("mfss_siteurl", "http://192.168.43.72/funshare/");	    
	    localEditor.putString("mfss_siteurl", "http://shop.funshare.co.ke/");	    
	    localEditor.commit();
	    
        Thread mythread = new Thread() {
			public void run() {
				try {
					while (splashActive && ms < splashTime) {
						if(!paused)
							ms=ms+100;
						sleep(100);
					}
				} catch(Exception e) {}
				finally {
					startActivity(new Intent(AaSplash.this, BaCategoryList.class));
					
				}
			}
		};
		mythread.start();
	
	}

	public void setFirstUse() {
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_first_use", false)) {
			Toast.makeText(this, R.string.welcome_txt, Toast.LENGTH_SHORT).show();
		    SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		    localEditor.putBoolean("mfss_first_use", true);
		    localEditor.putLong("mfss_first_data", System.currentTimeMillis());
		    localEditor.commit();
		}
		
	  }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result cat_title 100
		if (resultCode == 100) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	private boolean isExternalStoragepresent(){
		
		boolean mExternalStorageAvailable= false;
		boolean mExternalStorageWritable= false;
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)){
			mExternalStorageAvailable = mExternalStorageWritable = true; 
			
		} else {
			if(!((mExternalStorageAvailable) && (mExternalStorageWritable))){
				//Toast.makeText(getBaseContext(), "SD card not present", Toast.LENGTH_LONG).show();
			} return (mExternalStorageAvailable) && (mExternalStorageWritable);
		}
		return mExternalStorageWritable;
		
	}
		
	public static boolean createDirIfNotExits (String path){
		
		boolean ret =true;
		File file = new File(Environment.getExternalStorageDirectory(),path);
		if (!file.exists()){
			if (!file.mkdirs()){
				Log.e("AppSmata::", "Problem Creating AppSmata Folder");
				ret = false;
			}
		}
		return ret;
	}

}
package com.jackson_siro.mfunshareshop;

import com.jackson_siro.mfunshareshop.tools.*;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends ActionBarActivity {

	MfssDatabase db = new MfssDatabase(this);

	TextView mBalance, mFullName, mUserName, mUserSince, mUserEmail, mUserMobile;
	ImageView mUserIcon;
	Bitmap bitmap;	
	private View mProgressView;
	private final String IMG_PATH = "/AppSmata/mFunShares/cards/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
		mBalance = (TextView) findViewById(R.id.user_balance);
		mFullName = (TextView) findViewById(R.id.full_name);
		mUserName = (TextView) findViewById(R.id.user_name);
		mUserSince = (TextView) findViewById(R.id.user_since);
		mUserEmail = (TextView) findViewById(R.id.user_email);
		mUserMobile = (TextView) findViewById(R.id.user_mobile);
		
		mProgressView = findViewById(R.id.loading_progress);
		
		if (db.getOption("mfss_userid") == "null"){
    		Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show();
			localEditor.putBoolean("mfss_logged_in_user", false);
		    localEditor.commit();
		    startActivity(new Intent(this, SignIn.class));
		    finish();
		} else {
    		localEditor.putBoolean("mfss_logged_in_user", true);
		    localEditor.commit();
		    LoadUserDetails();
		}
	}

	public void LoadUserDetails(){
		mFullName.setText(db.getOption("mfss_user_fname") + db.getOption("mfss_user_surname"));
		mUserName.setText("@" + db.getOption("mfss_user_name"));
		mUserSince.setText(db.getOption("mfss_user_joined"));
		mUserEmail.setText(db.getOption("mfss_user_email"));
		mUserMobile.setText(db.getOption("mfss_user_mobile"));
		
	}
	

	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.user_profile, menu);
	       
	       return true;
	   }

	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
		   final SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
  			
		   switch (item.getItemId()) {
	       	
	       case android.R.id.home:
	       		onBackPressed();
	       		return true;
	       	
	       case R.id.payment:
	        	startActivity(new Intent(this, UserPayment.class));
              return true;
                           
       		case R.id.signout:
	        	AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
				builder.setTitle(R.string.just_amin);
				builder.setMessage(R.string.user_is_sure);
				
				builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						db.updateOption("mfss_userid", "null"); 
						db.updateOption("mfss_user_name", "null"); 
						db.updateOption("mfss_user_fname", "null"); 
						db.updateOption("mfss_user_surname", "null"); 
						db.updateOption("mfss_user_email", "null"); 
						db.updateOption("mfss_user_level", "null"); 
						db.updateOption("mfss_user_mobile", "null"); 
						db.updateOption("mfss_user_joined", "null");
						
						localEditor.putBoolean("mfss_logged_in_user", false);
					    localEditor.commit();
					    Toast.makeText(getApplicationContext(), R.string.user_logged_out, Toast.LENGTH_LONG).show();
						finish();
					}
				});
				
				builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//Toast.makeText(getApplicationContext(), R.string.user_is_sure, Toast.LENGTH_LONG).show();
						
					}
				});

				builder.show(); 
                return true;
	       
	           default:
	               return false;
	       }
	   } 
	
}

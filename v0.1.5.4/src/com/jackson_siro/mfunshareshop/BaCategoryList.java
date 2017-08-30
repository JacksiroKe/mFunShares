package com.jackson_siro.mfunshareshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jackson_siro.mfunshareshop.adaptor.*;
import com.jackson_siro.mfunshareshop.tools.*;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BaCategoryList extends ActionBarActivity {
	
	RelativeLayout MyCard;
	MfssDatabase db = new MfssDatabase(this);
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> categoriesList;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_CATID = "catid";
	private static final String TAG_TITLE = "cat_title";
	private static final String TAG_CONTENT = "cat_content";
	private static final String TAG_ICON = "cat_icon";

	JSONArray categories = null;
	
	public static final String SETTING_INFOS = "SETTING_Infos";
	public static final String USERNAME = "USERNAME";
	
	List<MfCategory> mylist;
	ArrayAdapter<String> myAdapter;
	
	ArrayList<HashMap<String, String>> myCards;
	ArrayList<HashMap<String, String>> cardsList;
	
	JSONArray cards = null;
	MyListAdapter adapter;
	
	ListView list;	
	private String[] My_Text;
	private String[] My_Texti;
	private String[] My_Textii;
	private String[] My_Icon;
	private View mProgressView;
	private View mCategoryList;
	
	   @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mfss_main);
		mProgressView = findViewById(R.id.loading_progress);
		mCategoryList = findViewById(R.id.mfss_list);
		
		SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
	    
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_first_time", false)) {
			
			db.createOption(new MfOption("mfss_userid", "null")); 
			db.createOption(new MfOption("mfss_user_name", "null")); 
			db.createOption(new MfOption("mfss_user_fname", "null")); 
			db.createOption(new MfOption("mfss_user_surname", "null")); 
			db.createOption(new MfOption("mfss_user_email", "null")); 
			db.createOption(new MfOption("mfss_user_level", "null")); 
			db.createOption(new MfOption("mfss_user_mobile", "null")); 
			db.createOption(new MfOption("mfss_user_joined", "null"));  
			db.createOption(new MfOption("mfss_user_balance", "null")); 
			
			
			localEditor.putBoolean("mfss_first_time", true);
		    localEditor.commit();
		}
		
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_logged_in_user", false)) {
    		localEditor.putBoolean("mfss_logged_in_user", false);
		    localEditor.commit();
		    LoginDialog();
		    
  		}
		
		isInternetOn();
		//categoriesList = new ArrayList<HashMap<String, String>>();
		//new LoadAllcategories().execute();
	}

	public final boolean isInternetOn() {
    	
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		
			if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
			     connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
			   
				categoriesList = new ArrayList<HashMap<String, String>>();
				new LoadAllcategories().execute();
				
			    return true;
			    
			} else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
			  
				NotConnected();
				
			    return false;
			}
		  return false;
		}

	protected void NotConnected() {
		FillWithData();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mCategoryList.setVisibility(show ? View.GONE : View.VISIBLE);
			mCategoryList.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCategoryList.setVisibility(show ? View.GONE : View.VISIBLE);
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
			mCategoryList.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	class LoadAllcategories extends AsyncTask<String, String, String> {
		
		SharedPreferences vSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());		
		String siteurl = vSettings.getString("mfss_siteurl", "NA") + "andy/categories.php";
		String imageurl =vSettings.getString("mfss_siteurl", "NA") + "js_media/";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			showProgress(true);
		}
		
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();			
			JSONObject json = jParser.makeHttpRequest(siteurl, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All categories: ", json.toString());
			db.deleteCategories();
			
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					categories = json.getJSONArray(TAG_CATEGORIES);
					for (int i = 0; i < categories.length(); i++) {
						JSONObject c = categories.getJSONObject(i);

						String catid = c.getString(TAG_CATID);
						String cat_title = c.getString(TAG_TITLE);
						String cat_content = c.getString(TAG_CONTENT);
						String cat_icon = c.getString(TAG_ICON);
						
						db.createCategory(new MfCategory(catid, cat_title, cat_content, imageurl + cat_icon)); 
						
					}
				} 
			} catch (JSONException e) {
				e.printStackTrace();
				//pDialog.dismiss();

				showProgress(false);
				FillWithData();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			//pDialog.dismiss();

			showProgress(false);
			FillWithData();
		}

	}
	
	private void FillWithData(){
		mylist = db.getAllCategories();
		List<String> listCatid = new ArrayList<String>();
		List<String> listCattitle = new ArrayList<String>();
		List<String> listCatcontent = new ArrayList<String>();
		List<String> listCaticon = new ArrayList<String>();
		
		for (int i = 0; i < mylist.size(); i++) {
			listCatid.add(i, mylist.get(i).getCatid());
			listCattitle.add(i, mylist.get(i).getCattitle());
			listCatcontent.add(i, mylist.get(i).getCatcontent());
			listCaticon.add(i, mylist.get(i).getCaticon());
		}
		
		My_Text = listCatid.toArray(new String[listCatid.size()]);		
		for (String string : My_Text) {	System.out.println(string);	}
		
		My_Texti = listCattitle.toArray(new String[listCattitle.size()]);		
		for (String stringi : My_Texti) {	System.out.println(stringi);	}

		My_Textii = listCatcontent.toArray(new String[listCatcontent.size()]);		
		for (String stringii : My_Textii) {	System.out.println(stringii);	}
		
		My_Icon = listCaticon.toArray(new String[listCaticon.size()]);		
		for (String stringiii : My_Text) {	System.out.println(stringiii);	}
		
        list = (ListView)findViewById(R.id.mfss_list);
        adapter = new MyListAdapter(this, My_Icon, My_Text, My_Texti, My_Textii);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
    			Intent intent = new Intent(BaCategoryList.this, BbCategoryView.class);
    			intent.putExtra("mfss_category", Integer.parseInt(My_Text[+ position]));
    			startActivityForResult(intent, 1);
        		
            }
        });	
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result card_title 100
		if (resultCode == 100) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}
	
	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.cat_list, menu);
	       
	       return true;
	   }

	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       switch (item.getItemId()) {
	       
		        case R.id.menu_refresh:
		        	Toast.makeText(this, R.string.refre_shing, Toast.LENGTH_SHORT).show();
		        	startActivity(new Intent(this, AaSplash.class));
					finish();
	               return true;
	               
		        case R.id.menu_settings:
		        	if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mfss_logged_in_user", false)) {
		        		startActivity(new Intent(this, SignIn.class));
		      		} else {
		      			startActivity(new Intent(this, UserProfile.class));
		      		}
		        						
	               return true;
	       
	           default:
	               return false;
	       }
	   } 
	   
	   public void LoginDialog() {
		   AlertDialog.Builder builder = new AlertDialog.Builder(BaCategoryList.this);
			builder.setTitle(R.string.just_amin);
			builder.setMessage("You are using mFunShare Shop in Guest Mode, Please Sign in or Sign up to access more fun. Your information is private and will not be shared any where!");
			
			builder.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					SignInActivity();
				}
			});
			
			builder.setNegativeButton("Sign Up", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					SignUpActivity();
				}
			});
			
			builder.setNeutralButton("Later", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
				}
			});
			
			builder.show(); 
	   }
	   
	   public void SignInActivity()
	   {
		   startActivity(new Intent(this, SignIn.class));
	   }
	   

	   public void SignUpActivity()
	   {
		   startActivity(new Intent(this, SignUp.class));
	   }
  }


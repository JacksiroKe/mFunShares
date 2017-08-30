package com.jackson_siro.mfunshareshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jackson_siro.mfunshareshop.tools.*;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jackson_siro.mfunshareshop.adaptor.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BbCategoryView extends ActionBarActivity {
	

	MfCategory selectedCategory;
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> cardsList;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_CARDS = "cards";
	private static final String TAG_CAT = "card_cat";
	private static final String TAG_TITLE = "card_title";
	private static final String TAG_CONTENT = "card_content";
	private static final String TAG_IMAGE = "card_image";

	// cards JSONArray
	JSONArray cards = null;
	
	ListView list;	
	private String[] My_Text;
	private String[] My_Texti;
	private String[] My_Textii;
	private String[] My_Image;
	
	MfssDatabase db = new MfssDatabase(this);
	List<MfCard> mylist;
	List<MfCategory> catlist;
	ArrayAdapter<String> myAdapter;
    MyListAdapter adapter;
	private View mProgressView;
	private View mCategoryView;
	
	   @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mfss_main);
		mProgressView = findViewById(R.id.loading_progress);
		mCategoryView = findViewById(R.id.mfss_list);
		Intent intent = getIntent();
		int qid = intent.getIntExtra("mfss_category", -1);

		db = new MfssDatabase(getApplicationContext());
		selectedCategory = db.readCategory(qid);		 
	    setTitle(selectedCategory.getCattitle());
		
	    isInternetOn();
		//cardsList = new ArrayList<HashMap<String, String>>();
		//new LoadAllcards().execute();
	}

	public final boolean isInternetOn() {
    	
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		
			if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
			     connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
			     connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
			   
				cardsList = new ArrayList<HashMap<String, String>>();
				new LoadAllcards().execute();
				
			    return true;
			    
			} else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
			  
				NotConnected();
				
			    return false;
			}
		  return false;
		}

	protected void NotConnected() {
		FillWithData(selectedCategory.getCatid());
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mCategoryView.setVisibility(show ? View.GONE : View.VISIBLE);
			mCategoryView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCategoryView.setVisibility(show ? View.GONE : View.VISIBLE);
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
			mCategoryView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	class LoadAllcards extends AsyncTask<String, String, String> {
		
		SharedPreferences vSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());		
		String siteurl = vSettings.getString("mfss_siteurl", "NA") + "andy/cards_all.php";
		String imageurl =vSettings.getString("mfss_siteurl", "NA") + "js_media/";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("catid", selectedCategory.getCatid()));			
			JSONObject json = jParser.makeHttpRequest(siteurl, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All cards: ", json.toString());
			db.deleteCards();
			
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// cards found
					// Getting Array of cards
					cards = json.getJSONArray(TAG_CARDS);

					// looping through All cards
					for (int i = 0; i < cards.length(); i++) {
						JSONObject c = cards.getJSONObject(i);
						String card_cat = c.getString(TAG_CAT);
						String card_title = c.getString(TAG_TITLE);
						String card_content = c.getString(TAG_CONTENT);
						String card_image = c.getString(TAG_IMAGE);
						
						db.createCard(new MfCard(card_cat, card_title, card_content, imageurl + card_image)); 
					}
				} 
			} catch (JSONException e) {
				e.printStackTrace();
				showProgress(false);
				FillWithData(selectedCategory.getCatid());
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			showProgress(false);
			FillWithData(selectedCategory.getCatid());
		}

	}
	

	private void FillWithData(String catid){
		mylist = db.getAllCardsByCat(catid);
		List<String> listCardid = new ArrayList<String>();
		List<String> listCardtitle = new ArrayList<String>();
		List<String> listCardcontent = new ArrayList<String>();
		List<String> listCardimage = new ArrayList<String>();
		
		for (int i = 0; i < mylist.size(); i++) {
			listCardid.add(i, Integer.toString(mylist.get(i).getId()));
			listCardtitle.add(i, mylist.get(i).getCardtitle());
			listCardcontent.add(i, mylist.get(i).getCardcontent());
			listCardimage.add(i, mylist.get(i).getCardimage());
		}
		
		My_Text = listCardid.toArray(new String[listCardid.size()]);		
		for (String string : My_Text) {	System.out.println(string);	}
		
		My_Texti = listCardtitle.toArray(new String[listCardtitle.size()]);		
		for (String stringi : My_Texti) {	System.out.println(stringi);	}

		My_Textii = listCardcontent.toArray(new String[listCardcontent.size()]);		
		for (String stringii : My_Textii) {	System.out.println(stringii);	}
		
		My_Image = listCardimage.toArray(new String[listCardimage.size()]);		
		for (String stringiii : My_Image) {	System.out.println(stringiii);	}
		
		list=(ListView)findViewById(R.id.mfss_list);
		adapter = new MyListAdapter(this, My_Image, My_Text, My_Texti, My_Textii);
        list.setAdapter(adapter);
		
		//registerForCardcontentextMenu(list);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
    			Intent intent = new Intent(BbCategoryView.this, BcCardView.class);
    			intent.putExtra("mfss_card", Integer.parseInt(My_Text[+ position]));
    			startActivityForResult(intent, 1);
        		
            }
        });	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            onBackPressed();
	            return true;
	            
	    }

	    return(super.onOptionsItemSelected(item));
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
}


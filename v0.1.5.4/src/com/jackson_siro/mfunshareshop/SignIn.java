package com.jackson_siro.mfunshareshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jackson_siro.mfunshareshop.tools.JSONParser;
import com.jackson_siro.mfunshareshop.tools.MfssDatabase;
import com.jackson_siro.mfunshareshop.tools.MfOption;

public class SignIn extends ActionBarActivity implements LoaderCallbacks<Cursor> {

	private AutoCompleteTextView mLoginNameView;
	private EditText mLoginKeyView;
	private View mProgressView;
	private View mLoginFormView;

	JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";	
	private static final String TAG_USER = "user";	
	private static final String TAG_USERID = "userid";
	private static final String TAG_USERNAME = "user_name";
	private static final String TAG_FNAME = "user_fname";
	private static final String TAG_SURNAME = "user_surname";
	private static final String TAG_EMAIL = "user_email";
	private static final String TAG_LEVEL = "user_level";
	private static final String TAG_MOBILE = "user_mobile";
	private static final String TAG_JOINED = "user_joined";
	private static final String TAG_BALANCE = "user_balance";

	MfssDatabase db = new MfssDatabase(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin);

		mLoginNameView = (AutoCompleteTextView) findViewById(R.id.loginname);
		populateAutoComplete();

		mLoginKeyView = (EditText) findViewById(R.id.loginkey);
		mLoginKeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		
		Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
		mSignUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), SignUp.class);
    			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    			finish();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	private void populateAutoComplete() {
		if (VERSION.SDK_INT >= 14) {
			getLoaderManager().initLoader(0, null, this);
		} else if (VERSION.SDK_INT >= 8) {
			new Setuplogin_nameAutoCompleteTask().execute(null, null);
		}
	}

	public void attemptLogin() {
		
		mLoginNameView.setError(null);
		mLoginKeyView.setError(null);

		String login_name = mLoginNameView.getText().toString();
		String login_key = mLoginKeyView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(login_key) && !islogin_keyValid(login_key)) {
			mLoginKeyView.setError(getString(R.string.error_invalid_login_key));
			focusView = mLoginKeyView;
			cancel = true;
		}

		if (TextUtils.isEmpty(login_name)) {
			mLoginNameView.setError(getString(R.string.error_field_required));
			focusView = mLoginNameView;
			cancel = true;
		} else if (!islogin_nameValid(login_name)) {
			mLoginNameView.setError(getString(R.string.error_invalid_loginname));
			focusView = mLoginNameView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			new UserSigninTask().execute();
		}
	}

	private boolean islogin_nameValid(String login_name) {
		// TODO: Replace this with your own logic
		return login_name.length() > 2;
	}

	private boolean islogin_keyValid(String login_key) {
		// TODO: Replace this with your own logic
		return login_key.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> login_names = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			login_names.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addlogin_namesToAutoComplete(login_names);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}

	class Setuplogin_nameAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... voids) {
			ArrayList<String> login_nameAddressCollection = new ArrayList<String>();
			ContentResolver cr = getContentResolver();
			Cursor login_nameCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
			while (login_nameCur.moveToNext()) {
				String login_name = login_nameCur.getString(login_nameCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				login_nameAddressCollection.add(login_name);
			}
			login_nameCur.close();

			return login_nameAddressCollection;
		}

		@Override
		protected void onPostExecute(List<String> login_nameAddressCollection) {
			addlogin_namesToAutoComplete(login_nameAddressCollection);
		}
	}

	private void addlogin_namesToAutoComplete(List<String> login_nameAddressCollection) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignIn.this,
				android.R.layout.simple_dropdown_item_1line, login_nameAddressCollection);

		mLoginNameView.setAdapter(adapter);
	}
	
	class UserSigninTask extends AsyncTask<String, String, String> {
		SharedPreferences vSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());		
		String siteurl = vSettings.getString("mfss_siteurl", "NA") + "andy/users_signin.php";
		
		String mLoginName = mLoginNameView.getText().toString();
		String mLoginKey = mLoginKeyView.getText().toString();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("login_name", mLoginName));
			params.add(new BasicNameValuePair("login_key", mLoginKey));

			JSONObject json = jsonParser.makeHttpRequest(siteurl, "POST", params);
			Log.d("Signing in", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					JSONArray userObj = json.getJSONArray(TAG_USER); 
					JSONObject user = userObj.getJSONObject(0);
										
					db.updateOption("mfss_userid", user.getString(TAG_USERID)); 
					db.updateOption("mfss_user_name", user.getString(TAG_USERNAME));
					db.updateOption("mfss_user_fname", user.getString(TAG_FNAME)); 
					db.updateOption("mfss_user_surname", user.getString(TAG_SURNAME)); 
					db.updateOption("mfss_user_email", user.getString(TAG_EMAIL)); 
					db.updateOption("mfss_user_level", user.getString(TAG_LEVEL)); 
					db.updateOption("mfss_user_mobile", user.getString(TAG_MOBILE)); 
					db.updateOption("mfss_user_joined", user.getString(TAG_JOINED)); 
					setLoggedIn();
					
				} else {
					//Toast.makeText(getApplicationContext(), "Invalid username/password combination!", Toast.LENGTH_LONG).show();
					
					mLoginNameView.setError(null);
					mLoginKeyView.setError(null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showProgress(false);
				mLoginNameView.setError(getString(R.string.error_invalid_login_name));
				mLoginKeyView.setError(getString(R.string.error_incorrect_login_key));
				mLoginNameView.requestFocus();

			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}
	
	private void setLoggedIn(){
		SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		localEditor.putBoolean("mfss_logged_in_user", true);
	    localEditor.commit();
	    //startActivity(new Intent(getApplicationContext(), UserProfile.class));					
		finish();
	}
}

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

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.jackson_siro.mfunshareshop.SignIn.UserSigninTask;
import com.jackson_siro.mfunshareshop.tools.JSONParser;
import com.jackson_siro.mfunshareshop.tools.MfssDatabase;

public class SignUp extends ActionBarActivity implements LoaderCallbacks<Cursor> {

	private static final String[] DUMMY_CREDENTIALS = new String[] { "foo@example.com:hello", "bar@example.com:world" };

	MfssDatabase db = new MfssDatabase(this);
	private AutoCompleteTextView mFnameView;
	private AutoCompleteTextView mSnameView;
	private AutoCompleteTextView mUnameView;
	private AutoCompleteTextView mMobileView;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private EditText mPassconView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		// Set up the login form.
		mFnameView = (AutoCompleteTextView) findViewById(R.id.fname);
		mSnameView = (AutoCompleteTextView) findViewById(R.id.sname);
		mUnameView = (AutoCompleteTextView) findViewById(R.id.username);
		mMobileView = (AutoCompleteTextView) findViewById(R.id.mobile);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
		mPassconView = (EditText) findViewById(R.id.passwordcon);
		
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptRegister();
					return true;
				}
				return false;
			}
		});

		Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
		mEmailSignUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptRegister();
			}
		});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), SignIn.class);
    			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}

	public void attemptRegister() {
		
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String fname = mFnameView.getText().toString();
		String sname = mSnameView.getText().toString();
		String username = mUnameView.getText().toString();
		String mobile = mMobileView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		String passwordcon = mPassconView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_login_key));
			focusView = mPasswordView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(fname)) {
			mFnameView.setError(getString(R.string.error_invalid_fname));
			focusView = mFnameView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(sname)) {
			mSnameView.setError(getString(R.string.error_invalid_sname));
			focusView = mSnameView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(username)) {
			mUnameView.setError(getString(R.string.error_invalid_username));
			focusView = mUnameView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mobile)) {
			mMobileView.setError(getString(R.string.error_invalid_mobile));
			focusView = mMobileView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_login_name));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			new UserSignupTask().execute();
		}
	}

	private boolean isEmailValid(String email) {
		// TODO: Replace this with your own logic
		return email.contains("@");
	}

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 4;
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
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
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

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
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

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		// Create adapter to tell the AutoCompleteTextView what to show in its
		// dropdown list.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignUp.this,
				android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}


	class UserSignupTask extends AsyncTask<String, String, String> {
		SharedPreferences vSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());		
		String siteurl = vSettings.getString("mfss_siteurl", "NA") + "andy/users_signup.php";
		
		String firstname = mFnameView.getText().toString();
		String surname = mSnameView.getText().toString();
		String username = mUnameView.getText().toString();
		String mobile = mMobileView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		String passcon = mPassconView.getText().toString();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_fname", firstname));
			params.add(new BasicNameValuePair("user_surname", surname));
			params.add(new BasicNameValuePair("user_name", username));
			params.add(new BasicNameValuePair("user_mobile", mobile));
			params.add(new BasicNameValuePair("user_email", email));
			params.add(new BasicNameValuePair("user_password", passcon));

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
					mFnameView.setError(null);
					mSnameView.setError(null);
					mUnameView.setError(null);
					mMobileView.setError(null);
					mEmailView.setError(null);
					mPasswordView.setError(null);
					mPassconView.setError(null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showProgress(false);
				mFnameView.setError(null);
				mSnameView.setError(null);
				mUnameView.setError(null);
				mMobileView.setError(null);
				mEmailView.setError(null);
				mPasswordView.setError(null);
				mPassconView.setError(null);
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

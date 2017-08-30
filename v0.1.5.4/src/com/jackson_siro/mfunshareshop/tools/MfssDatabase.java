package com.jackson_siro.mfunshareshop.tools;

import java.util.LinkedList;
import java.util.List;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class MfssDatabase extends SQLiteOpenHelper {

	// database version
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "MFunShareShop";
	
	//Categories Variables
	private static final String CAT_TABLE = "Mfss_Category", CAT_ID = "catid", CAT_CATID = "categoryid", CAT_TITLE = "cat_title", CAT_CONTENT = "cat_content", CAT_ICON = "cat_icon";
	private static final String[] CAT_COLUMNS = { CAT_ID, CAT_CATID, CAT_TITLE, CAT_CONTENT, CAT_ICON };
	
	//Cards Variables
	private static final String CARD_TABLE = "Mfss_Cards", CARD_ID = "id", CARD_CAT = "card_cat", CARD_TITLE = "card_title", CARD_CONTENT = "card_content", CARD_IMAGE = "card_image";
	private static final String[] COLUMNS_CARD = { CARD_ID, CARD_CAT, CARD_TITLE, CARD_CONTENT, CARD_IMAGE };
	
	//Options Variables
	private static final String OPT_TABLE = "Mfss_Options", OPT_ID = "optionid", OPT_TITLE = "option_title", OPT_CONTENT = "option_content", OPT_UPDATED = "option_updated";
	private static final String[] COLUMNS_OPTION = { OPT_ID, OPT_TITLE, OPT_CONTENT };
	
	//Options Variables
		private static final String PAY_TABLE = "Mfss_Payment", PAY_ID = "id", PAY_AGENT = "payment_agent", PAY_CODE = "payment_code", PAY_TIME = "payment_time", PAY_AMOUNT = "payment_amount";
		private static final String[] PAY_COLUMNS = { PAY_ID, PAY_AGENT, PAY_CODE, PAY_TIME, PAY_AMOUNT };

	public MfssDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL("CREATE TABLE " + CAT_TABLE + "(" + CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CAT_CATID + " VARCHAR, " + CAT_TITLE + " VARCHAR, " + CAT_CONTENT + " VARCHAR, " + CAT_ICON + " VARCHAR)");
		db.execSQL("CREATE TABLE " + CARD_TABLE + "(" + CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CARD_CAT + " VARCHAR, " + CARD_TITLE + " VARCHAR, " + CARD_CONTENT + " VARCHAR, " + CARD_IMAGE + " VARCHAR)");
		db.execSQL("CREATE TABLE " + OPT_TABLE + "(" + OPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + OPT_TITLE + " VARCHAR, " + OPT_CONTENT + " VARCHAR, " + OPT_UPDATED + " VARCHAR)");
		db.execSQL("CREATE TABLE " + PAY_TABLE + "(" + PAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PAY_AGENT + " VARCHAR, " + PAY_CODE + " VARCHAR, " + PAY_AMOUNT + " VARCHAR)");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CAT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + OPT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PAY_TABLE);
		
		this.onCreate(db);
	}

	//CATEGORIES MANAGEMENT	
	public void createCategory(MfCategory category) {
		// get reference of the CategoryDB database
		SQLiteDatabase db = this.getWritableDatabase();

		// make values to be inserted
		ContentValues values = new ContentValues();
		values.put(CAT_CATID, category.getCatid());
		values.put(CAT_TITLE, category.getCattitle());
		values.put(CAT_CONTENT, category.getCatcontent());
		values.put(CAT_ICON, category.getCaticon());

		// insert category
		db.insert(CAT_TABLE, null, values);
		db.close();
	}

	public MfCategory readCategory(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CAT_TABLE, // a. table
				CAT_COLUMNS, CAT_CATID + " = ?", 
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		MfCategory category = new MfCategory();
		category.setId(Integer.parseInt(cursor.getString(0)));
		category.setCatid(cursor.getString(1));
		category.setCattitle(cursor.getString(2));
		category.setCatcontent(cursor.getString(3));
		category.setCaticon(cursor.getString(4));
		
		return category;
	}

	public List<MfCategory> getAllCategories() {
		List<MfCategory> Category = new LinkedList<MfCategory>();
		String query = "SELECT  * FROM " + CAT_TABLE + " ORDER BY " + 
				CAT_TITLE + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		MfCategory category = null;
		if (cursor.moveToFirst()) {
			do {
				category = new MfCategory();
				category.setId(Integer.parseInt(cursor.getString(0)));
				category.setCatid(cursor.getString(1));
				category.setCattitle(cursor.getString(2));
				category.setCatcontent(cursor.getString(3));
				category.setCaticon(cursor.getString(4));
				
				Category.add(category);
			} while (cursor.moveToNext());
		}
		return Category;
	}
	
	
	public int updateCategory(MfCategory category) {

		// get reference of the CategoryDB database
		SQLiteDatabase db = this.getWritableDatabase();

		// make values to be inserted
		ContentValues values = new ContentValues();
		values.put("categoryid", category.getCatid()); 
		values.put("cat_title", category.getCattitle());
		values.put("cat_content", category.getCatcontent());
		values.put("cat_icon", category.getCaticon());
		
		// upcat_title
		int i = db.update(CAT_TABLE, values, CAT_ID + " = ?", new String[] { String.valueOf(category.getId()) });

		db.close();
		return i;
	}

	// Deleting single category
	public void deleteCategory(MfCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		// delete category
		db.delete(CAT_TABLE, CAT_ID + " = ?", new String[] { String.valueOf(category.getId()) });
		db.close();
	}
	
	public void deleteCategories(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(CAT_TABLE, null, null);
		db.close();
	}
	
	//CARDS MANAGEMENT
	public void createCard(MfCard mfCard) {
		// get reference of the CardDB database
		SQLiteDatabase db = this.getWritableDatabase();

		// make values to be inserted
		ContentValues values = new ContentValues();
		values.put(CARD_CAT, mfCard.getCardcat());
		values.put(CARD_TITLE, mfCard.getCardtitle());
		values.put(CARD_CONTENT, mfCard.getCardcontent());
		values.put(CARD_IMAGE, mfCard.getCardimage());

		// insert card
		db.insert(CARD_TABLE, null, values);

		// close database transaction
		db.close();
	}

	public MfCard readCard(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CARD_TABLE, // a. table
				COLUMNS_CARD, CARD_ID + " = ?", 
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		MfCard mfCard = new MfCard();
		mfCard.setId(Integer.parseInt(cursor.getString(0)));
		mfCard.setCardcat(cursor.getString(1));
		mfCard.setCardtitle(cursor.getString(2));
		mfCard.setCardcontent(cursor.getString(3));
		mfCard.setCardimage(cursor.getString(4));
		
		return mfCard;
	}

	public List<MfCard> getAllCards() {
		List<MfCard> MfCard = new LinkedList<MfCard>();
		String query = "SELECT  * FROM " + CARD_TABLE + " ORDER BY " + 
				CARD_TITLE + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		MfCard mfCard = null;
		if (cursor.moveToFirst()) {
			do {
				mfCard = new MfCard();
				mfCard.setId(Integer.parseInt(cursor.getString(0)));
				mfCard.setCardcat(cursor.getString(1));
				mfCard.setCardtitle(cursor.getString(2));
				mfCard.setCardcontent(cursor.getString(3));
				mfCard.setCardimage(cursor.getString(4));
								
				MfCard.add(mfCard);
			} while (cursor.moveToNext());
		}
		return MfCard;
	}

	

	public List<MfCard> getAllCardsByCat(String card_cat) {
		List<MfCard> MfCard = new LinkedList<MfCard>();
		String query = "SELECT  * FROM " + CARD_TABLE + " WHERE " + CARD_CAT + 
				" = " + card_cat + " ORDER BY " + CARD_TITLE + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		MfCard mfCard = null;
		if (cursor.moveToFirst()) {
			do {
				mfCard = new MfCard();
				mfCard.setId(Integer.parseInt(cursor.getString(0)));
				mfCard.setCardcat(cursor.getString(1));
				mfCard.setCardtitle(cursor.getString(2));
				mfCard.setCardcontent(cursor.getString(3));
				mfCard.setCardimage(cursor.getString(4));
				
				MfCard.add(mfCard);
			} while (cursor.moveToNext());
		}
		return MfCard;
	}
	
	public int updateCard(MfCard mfCard) {

		// get reference of the CardDB database
		SQLiteDatabase db = this.getWritableDatabase();

		// make values to be inserted
		ContentValues values = new ContentValues();
		values.put("card_cat", mfCard.getCardcat()); 
		values.put("card_title", mfCard.getCardtitle());
		values.put("card_content", mfCard.getCardcontent());
		values.put("card_image", mfCard.getCardimage());
		
		// upcard_title
		int i = db.update(CARD_TABLE, values, CARD_ID + " = ?", new String[] { String.valueOf(mfCard.getId()) });

		db.close();
		return i;
	}

	// Deleting single card
	public void deleteCard(MfCard mfCard) {

		SQLiteDatabase db = this.getWritableDatabase();
		// delete card
		db.delete(CARD_TABLE, CARD_ID + " = ?", new String[] { String.valueOf(mfCard.getId()) });
		db.close();
	}
	
	public void deleteCards(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(CARD_TABLE, null, null);
		db.close();
	}
	
	//OPTIONS MANAGEMENT
	public void createOption(MfOption option) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(OPT_TITLE, option.getOptiontitle());
		values.put(OPT_CONTENT, option.getOptioncontent());
		db.insert(OPT_TABLE, null, values);
		db.close();
	}

	public MfOption readOption(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(OPT_TABLE, // a. table
				COLUMNS_OPTION, OPT_ID + " = ?", 
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		MfOption option = new MfOption();
		option.setId(Integer.parseInt(cursor.getString(0)));
		option.setOptiontitle(cursor.getString(1));
		option.setOptioncontent(cursor.getString(2));
		
		return option;
	}

	public String getOption(String title) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(OPT_TABLE, // a. table
				COLUMNS_OPTION, OPT_TITLE + " = ?", 
				new String[] { title }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		MfOption option = new MfOption();
		option.setId(Integer.parseInt(cursor.getString(0)));
		option.setOptiontitle(cursor.getString(1));
		option.setOptioncontent(cursor.getString(2));
		
		return option.getOptioncontent();
	}

	public void updateOption(String opt_title, String opt_value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(OPT_TITLE, opt_title);
		values.put(OPT_CONTENT, opt_value);
		db.update(OPT_TABLE, values, OPT_TITLE + " = ?", new String[] { opt_title });
		db.close();
	}

	//PAYMENTS MANAGEMENT
	public void createPayment(MfPayment payment) {
		// get reference of the PaymentDB database
		SQLiteDatabase db = this.getWritableDatabase();

		// make values to be inserted
		ContentValues values = new ContentValues();
		values.put(PAY_AGENT, payment.getPaymentAgent());
		values.put(PAY_CODE, payment.getPaymentCode());
		values.put(PAY_TIME, payment.getPaymentTime());
		values.put(PAY_AMOUNT, payment.getPaymentAmount());

		// insert card
		db.insert(PAY_TABLE, null, values);

		// close database transaction
		db.close();
	}

	public List<MfPayment> getAllPayments() {
		List<MfPayment> Payment = new LinkedList<MfPayment>();
		String query = "SELECT  * FROM " + PAY_TABLE + " ORDER BY " + 
				PAY_ID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		MfPayment payment = null;
		if (cursor.moveToFirst()) {
			do {
				payment = new MfPayment();
				payment.setId(Integer.parseInt(cursor.getString(0)));				
				payment.setPaymentAgent(cursor.getString(1));
				payment.setPaymentCode(cursor.getString(2));
				payment.setPaymentTime(cursor.getString(3));
				payment.setPaymentAmount(cursor.getString(4));
								
				Payment.add(payment);
			} while (cursor.moveToNext());
		}
		return Payment;
	}

}

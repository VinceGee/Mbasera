/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.vince.empire.mbasera.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vince.empire.mbasera.navhistory.adapter.model.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "mbasera";

	// Login table name
	private static final String TABLE_USER = "user";

    // order table name
    private static final String TABLE_ORDER = "order_table";

	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_UID = "uid";
	private static final String KEY_CREATED_AT = "created_at";

	// Login Table Columns names
	private static final String ID = "col_id";
	private static final String KEY_ORDER_ID = "order_id";
	private static final String KEY_DATE = "date_";
	private static final String KEY_DESC = "desc";
	private static final String KEY_TOTAL = "total";

	public static ArrayList<Order> mSaveOrderList = new ArrayList<Order>();

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
				+ KEY_CREATED_AT + " TEXT" + ")";

        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + ID + " INTEGER PRIMARY KEY,"
                + KEY_ORDER_ID + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_TOTAL + " TEXT"+ ")";


		db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE_USER IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE_ORDER IF EXISTS " + TABLE_ORDER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String email, String uid, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name); // Name
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_UID, uid); // Email
		values.put(KEY_CREATED_AT, created_at); // Created At

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

    /**
     * Storing order details in database
     * */
    public void addOrder(String order_id, String date, String desc, String total){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORDER_ID, order_id); // Email
        values.put(KEY_DATE, date); // Name
        values.put(KEY_DESC, desc); // Email
        values.put(KEY_TOTAL, total); // Email

        // Inserting Row
        long idy = db.insert(TABLE_ORDER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New order inserted into sqlite: " + idy);
    }

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
			user.put("email", cursor.getString(2));
			user.put("uid", cursor.getString(3));
			user.put("created_at", cursor.getString(4));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

    /**
     * Getting order data from database
     * */
    public ArrayList<Order> getOrderDetails() {
        String selectQuery = "SELECT  * FROM " + TABLE_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {


                mSaveOrderList.add(new Order(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));

                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mSaveOrderList;
    }

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
        db.delete(TABLE_ORDER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}

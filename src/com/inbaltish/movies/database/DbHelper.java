package com.inbaltish.movies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{

	// c'tor
	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// creating a DB table
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(DbConstants.LOG_TAG, "Creating the DB tables");
		String CREATE_TABLE = "CREATE TABLE " + DbConstants.TABLE_NAME + "(" 
		+ DbConstants.MOVIE_ID + " INTEGER PRIMARY KEY,"
		+ DbConstants.MOVIE_TITLE + " TEXT," 
		+ DbConstants.MOVIE_DESCRIPTION + " TEXT," 
		+ DbConstants.MOVIE_URL + " TEXT,"
		+ DbConstants.MOVIE_WATCHED + " INTEGER,"
		+ DbConstants.MOVIE_RATE + " INTEGER"
		+ ")";
		
		try{
			db.execSQL(CREATE_TABLE);
		}catch (SQLiteException ex){
			Log.e(DbConstants.LOG_TAG, "Create table exception: " + ex.getMessage());
		}
	}

	// upgrading DB table by dropping the old and calling onCreate() to create a new one
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbConstants.LOG_TAG, "Upgrading database from version: " + oldVersion + " to " + newVersion + ", which will destroy all old table");
		db.execSQL("DROP TABLE IF EXISTS" + DbConstants.TABLE_NAME);
		onCreate(db);
	}
	
	

}

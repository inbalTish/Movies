package com.inbaltish.movies.database;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.inbaltish.movies.core.Movie;

public class DbHandler {
	
	static Movie movie;
	
	private DbHelper helper;
	private SQLiteDatabase db;
	
	// c'tor - creating a DbHelper object
	public DbHandler(Context context){
		helper = new DbHelper(context, DbConstants.DATABASE_NAME, null, DbConstants.DATABASE_VERSION);
		}
	
	// add a raw to the DB (represents one movie object)
	public void addRaw(Movie movie){
		Log.d(DbConstants.LOG_TAG, "adding a raw to DB");
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_TITLE, movie.getTitle()); // (put(key(Column name), value))
		values.put(DbConstants.MOVIE_DESCRIPTION, movie.getDescription());
		values.put(DbConstants.MOVIE_URL, movie.getUrl());
//		values.put(DbConstants.MOVIE_WATCHED, movie.getWatched());
//		values.put(DbConstants.MOVIE_RATE, movie.getWatched());
		try{
			db.insertOrThrow(DbConstants.TABLE_NAME, null, values); // (returns the id of the new raw created)
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; // why we need to throw another exception here???
		}finally{
			db.close();
		}
	}
	
	// update a raw in the DB (all movie's parameters except for id)
	public void updateRaw(Movie movie){
		Log.d(DbConstants.LOG_TAG, "updating a raw in the DB");
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_TITLE, movie.getTitle());
		values.put(DbConstants.MOVIE_DESCRIPTION, movie.getDescription());
		values.put(DbConstants.MOVIE_URL, movie.getUrl());
		values.put(DbConstants.MOVIE_WATCHED, movie.getWatched());
		values.put(DbConstants.MOVIE_RATE, movie.getWatched());
		try{
			db.update(DbConstants.TABLE_NAME, values, DbConstants.MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; 
		}finally{
			db.close();
		}
	}
	
	// update rate parameter in DB raw by raw id
	public void updateRate(Movie movie){
		Log.d(DbConstants.LOG_TAG, "updating rate in a DB raw");
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_RATE, movie.getRate());
		try{
			db.update(DbConstants.TABLE_NAME, values, DbConstants.MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; 
		}finally{
			db.close();
		}
	}
	
	// update watched parameter in DB raw by raw id
	public void updateWatched(Movie movie){
		Log.d(DbConstants.LOG_TAG, "updating watched in a DB raw");
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_WATCHED, movie.getWatched());
		try{
			db.update(DbConstants.TABLE_NAME, values, DbConstants.MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; 
		}finally{
			db.close();
		}
	}
		
	// delete a raw from BD
	public void deleteRaw(Movie movie){
		Log.d(DbConstants.LOG_TAG, "deleting a raw from DB");
		db = helper.getWritableDatabase();
		try{
			db.delete(DbConstants.TABLE_NAME, DbConstants.MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; 
		}finally{
			db.close();
		}
	}
	
	// delete all raws from DB (not deleting the table itself!)
	public void deleteAllRaws(){
		Log.d(DbConstants.LOG_TAG, "deleting all raws from DB");
		db = helper.getWritableDatabase();
		try{
			db.delete(DbConstants.TABLE_NAME, null, null);
		}catch(SQLException ex){
			Log.e(DbConstants.LOG_TAG, ex.getMessage());
			throw ex; 
		}finally{
			db.close();
		}
	}
	
	// get a cursor object that holds all raws (entire table)
	public Cursor getAllRaws(){
		Log.d(DbConstants.LOG_TAG, "getting all raws from DB - Cursor");
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DbConstants.TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	// get an ArrayList<Movie> that holds all raws (entire table)
	public ArrayList<Movie> getAllRawsAsArray(){
		Log.d(DbConstants.LOG_TAG, "getting all raws from DB - ArrayList");
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DbConstants.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<Movie> DBRawsArray = new ArrayList<Movie>();
		while(cursor.moveToNext()){
				DBRawsArray.add(movie = new Movie(cursor.getInt(0), 
						cursor.getString(1), 
						cursor.getString(2), 
						cursor.getString(3), 
						cursor.getInt(4),
						cursor.getInt(5))); 
		}
		return DBRawsArray;
	}
	
	// get a cursor object (by id) that holds one raw from DB
	public Cursor getRaw(int id){
		Log.d(DbConstants.LOG_TAG, "getting a raw from DB - Cursor");
		db = helper.getReadableDatabase();
		movie = null;
		Cursor cursor = db.query(DbConstants.TABLE_NAME, null, DbConstants.MOVIE_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		return cursor;
	}
	
	// get a movie object (by id) that holds one raw from DB
	public Movie getRawAsMovie(int id){
		Log.d(DbConstants.LOG_TAG, "getting a raw from DB - Movie");
		db = helper.getReadableDatabase();
		movie = null;
		Cursor cursor = db.query(DbConstants.TABLE_NAME, null, DbConstants.MOVIE_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		// check if the movie was found and initializing a movie object with data from cursor
		if(cursor.moveToFirst()){
			movie = new Movie(cursor.getInt(0), 
					cursor.getString(1), 
					cursor.getString(2), 
					cursor.getString(3), 
					cursor.getInt(4),
					cursor.getInt(5)); 
		}
		return movie;
	}
}
package com.inbaltish.movies;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.inbaltish.movies.core.*;
import com.inbaltish.movies.database.DbHandler;

public class MainActivity extends Activity implements OnClickListener {
	
	ImageButton buttonSettings, buttonAdd;
	ListView myMovies;
	Intent intent;
	ArrayList<Movie> movies;
	MoviesArrayAdapter adapter;
	DbHandler handler;
	
	Movie movieItem;
	int menuId;
	
	//--------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		// creating ArrayList<Movie>
		movies = new ArrayList<Movie>();
		// creating DBHandler instance and DB file (if non-existing)
		handler = new DbHandler(this);
		
		// registering views for clicks
		buttonAdd = (ImageButton)findViewById(R.id.imageButtonAddMovie);
		buttonAdd.setOnClickListener(this);
		
		buttonSettings = (ImageButton)findViewById(R.id.imageButtonSettingsMain);
		buttonSettings.setOnClickListener(this);
		
		myMovies = (ListView)findViewById(R.id.listMyMovies);
		myMovies.setEmptyView(findViewById(android.R.id.empty));
		
	}//---------- end of OnCreat()
	
	@Override
	protected void onResume() {
		// initiating ArrayList<Movie> with records from DB 
		movies = handler.getAllRawsAsArray();
		// custom adapter MoviesArrayAdapter to display all saved movies in ListView
		adapter = new MoviesArrayAdapter(this, R.layout.list_item_main_activity, movies); // by calling the adapter here am I re-saving data?
		myMovies.setAdapter(adapter);
		super.onResume();
	}

	@Override
	public void onClick(View b) {
		switch(b.getId()){
		case R.id.imageButtonAddMovie:
			// open alertDialog with 3 buttons
			setAlerteDialogHow2Add();
			break;
		case R.id.imageButtonSettingsMain:
			// open option menu
			openOptionsMenu();
			break;
		}
	}
	
	// setting AlertDialog for add button
	private void setAlerteDialogHow2Add() {
		final AlertDialog how2AddDialog = new AlertDialog.Builder(this).create();
		how2AddDialog.setTitle(R.string.how2Add_dialog);
		how2AddDialog.setMessage("Please choose if you want to add your movie manually or by searching the internet");
		// setting AlertDialog's buttons
		how2AddDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Manually", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// open EditMovieActivity
				Intent intent = new Intent(getBaseContext(), EditMovieActivity.class);
				intent.putExtra("requestCode", Constants.ADD_MANUALLY);
				startActivity(intent);	
			}
		});
		how2AddDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Internet", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// open SearchMovieActivity
				Intent intent = new Intent(getBaseContext(), SearchMovieActivity.class);
				startActivity(intent);	
			}
		});
		how2AddDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// closing alertDialog and back to MainActivity
				how2AddDialog.cancel();
			}
		});
		how2AddDialog.show();
	}

	// creating optionMenu for settings button
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.option_menu_exit:
			// back to mainActivity (should it be exit from app? finish();)
			closeOptionsMenu();
			return true;
		case R.id.option_menu_clearList:
			// deleting all the records from DB and arrayList
			handler.deleteAllRaws();
			movies.clear();
			adapter.notifyDataSetChanged();
			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}
	
	// ---------- Methods for handling events of ListView's items
	
	// for Spinner
	public void updateItemRate(Movie movie){
		handler.updateRate(movie);
	}
	// for CheckBox
	public void updateItemWatched(Movie movie){
		handler.updateWatched(movie);
	}
	// for TextView
	public void sendItemToEdit(int DBid){
		// passing the item's DBid to EditMovieActivity for Editing
		Intent intent = new Intent (this, EditMovieActivity.class);
		intent.putExtra("requestCode", Constants.EDIT_MOVIE);
		intent.putExtra("DBid", DBid);
		startActivity(intent);
	}
	
	// context menu for long click on adapter's txtView
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_context_menu, menu);
		// getting the specific movie object and its id by view's tag
		movieItem = movies.get(Integer.parseInt(view.getTag() + ""));
		menuId = movieItem.getId();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		switch(item.getItemId()){
		case R.id.contextMenu_edit:
			// open EditMovieActivity for editing movie's details
			Intent intent = new Intent (this, EditMovieActivity.class);
			intent.putExtra("requestCode", Constants.EDIT_MOVIE);
			intent.putExtra("DBid", menuId);
			startActivity(intent);
			return true;
		case R.id.contextMenu_delete:
			// delete movie from DB and arrayList
			handler.deleteRaw(movieItem); 
			movies.remove(movieItem);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.contextMenu_cancel:
			closeContextMenu();
			return true;
		default: 
			return super.onContextItemSelected(item);
		}
	}
		
}//---------- End of Activity

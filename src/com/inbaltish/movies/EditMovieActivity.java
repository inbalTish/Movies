package com.inbaltish.movies;


import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inbaltish.movies.core.*;
import com.inbaltish.movies.database.DbHandler;

public class EditMovieActivity extends Activity implements OnClickListener {
	
	Bundle bundle;
	int requestCode;
	
	private DownloadTask downloadImageTask;
	private SearchMovieTask searchTask;
	
	Movie savedMovie;
	DbHandler handler;
	
	EditText editTitle, editDescription, editUrl;
	ImageView showPicture;
	
//--------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_movie_activity);
		
		// setting the activity's views
		Button btnShow = (Button)findViewById(R.id.buttonShowPicture);
		btnShow.setOnClickListener(this);
		Button btnSave = (Button)findViewById(R.id.buttonSaveEditMovie);
		btnSave.setOnClickListener(this);
		Button btnDelete = (Button)findViewById(R.id.buttonDeleteMovie);
		btnDelete.setOnClickListener(this);
		Button btnCancel = (Button)findViewById(R.id.buttonCancelEditMovie);
		btnCancel.setOnClickListener(this);
		editTitle = (EditText)findViewById(R.id.editTextMovieTitle);
		editDescription = (EditText)findViewById(R.id.editTextMovieDescription);
		editUrl = (EditText)findViewById(R.id.editTextPictureUrl);
		showPicture = (ImageView)findViewById(R.id.imageViewMoviePicture);
		
		// creating DBHandler instance
		handler = new DbHandler(this);
		
		// preparing to receive intents
		bundle = getIntent().getExtras();
		requestCode = bundle.getInt("requestCode");
		
		// setting view's contents according to intent's origin
		switch(requestCode){
		case Constants.ADD_MANUALLY:
			// no need to show anything in views (new movie)
			break;
		case Constants.ADD_INTERNET:
			// getting the movie link from SearchActivity's intent
			String searchLink = bundle.getString("searchLink");
			// setting view's contents by calling SearchMovieTask (movie's details mode)
			if(searchTask != null){ // checking if task already running
				if(searchTask.getStatus() != AsyncTask.Status.FINISHED){
					Log.d(Constants.getHttpTag(), "no need to start a new task");
					return;
				}
			}
			searchTask = new SearchMovieTask(EditMovieActivity.this, Constants.SEARCH_MOVIE_DETAILS);
			searchTask.execute(searchLink + Constants.getApiKey()); // passing the movie's link as an url for the search task
			break;
		case Constants.EDIT_MOVIE:
			// getting the movie id from MainActivity's intent
			int DBid = bundle.getInt("DBid");
			// getting row from DB (by id)
			savedMovie = handler.getRawAsMovie(DBid);
			// displaying movie's parameters in views
			String t = savedMovie.getTitle();
			editTitle.setText(t);
			editDescription.setText(savedMovie.getDescription());
			editUrl.setText(savedMovie.getUrl());
			break;
		}
	}//---------- End of onCreate()

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		// displaying the picture from url by calling DownloadTask
		case R.id.buttonShowPicture:
			TextView errorMsg = (TextView)findViewById(R.id.textViewErrorMsg);
			errorMsg.setText(null);
			String imageUrl = editUrl.getText().toString();
			if(editUrl.length() != 0){
				if(validateImageUrl(imageUrl) == true){ // validating image url
					if(downloadImageTask != null){ // checking if download started successfully
						if(downloadImageTask.getStatus() != AsyncTask.Status.FINISHED){ // checking if the task is not already running
							Log.d("DownloadImageTask", "No need to start a new task");
							return;
						}
					}
					// adding "http://" to url
					if(imageUrl.toLowerCase(Locale.US).startsWith("www.")){
						imageUrl = "http://" + imageUrl;
					}
					downloadImageTask = new DownloadTask(this);
					// getting the url from view's display for DownloadImageTask (in case the user edit it)
					downloadImageTask.execute(imageUrl);
				}else{
					errorMsg.setText("Url address is invalid");
				}
			}else{
				editUrl.setHintTextColor(getResources().getColor(R.color.red));
				editUrl.setHint("url address is empty");
			}
			break;
		case R.id.buttonSaveEditMovie:
			// setting the data to local variables
			String currTitle;
			String currDescription;
			String currUrl;
			if(editTitle.length() != 0){ // running the same test in the setTitle(), but here I can set massage for the user...
				editTitle.setHintTextColor(getResources().getColor(R.color.red));
				currTitle = editTitle.getText().toString();
				currDescription = editDescription.getText().toString();
				// notifying the user that his url was invalid and was not saved
				currUrl = editUrl.getText().toString();
				if(editUrl.getText().length() != 0){
					if(validateImageUrl(currUrl) == false){
						Toast.makeText(this, "invalid Url. was not saved!", Toast.LENGTH_LONG).show();
					}
				}
				// saving the movie object to DB according to intent's origin
				Movie newMovie;
				switch(requestCode){
				case(Constants.ADD_MANUALLY):
					newMovie = new Movie(currTitle, currDescription, currUrl);
					handler.addRaw(newMovie);
					finish();
					break;
				case(Constants.ADD_INTERNET): // same code but as preparation for future changes, and for the sake of good order...
					newMovie = new Movie(currTitle, currDescription, currUrl);
					handler.addRaw(newMovie);
					finish();
					break;
				case(Constants.EDIT_MOVIE):
					savedMovie.setTitle(currTitle);
					savedMovie.setDescription(currDescription);
					savedMovie.setUrl(currUrl);
					handler.updateRaw(savedMovie);
					finish();
					break;
				}
			}else{
				editTitle.setHintTextColor(getResources().getColor(R.color.red)); 
				editTitle.setHint("Must enter movie title");
			}
			break;
		case R.id.buttonDeleteMovie:
			switch(requestCode){
			case (Constants.ADD_MANUALLY):
				// delete views' contents
				editTitle.setText("");
				editDescription.setText("");
				editUrl.setText("");
				break;
			case (Constants.ADD_INTERNET):
				// delete views' contents
				editTitle.setText("");
				editDescription.setText("");
				editUrl.setText("");
				showPicture.setImageResource(R.drawable.tv_black_white);
				break;
			case (Constants.EDIT_MOVIE):
				setDeleteMovieDialog();
				break;
			}
			break;
		case R.id.buttonCancelEditMovie:
			if(searchTask != null){
				if(searchTask.getStatus() != AsyncTask.Status.FINISHED){
					searchTask.cancel(true);
				}
			}
			if(downloadImageTask != null){
				if(downloadImageTask.getStatus() != AsyncTask.Status.FINISHED){
					downloadImageTask.cancel(true);
				}
			}
			finish();
			break;
		}
	}
	
	// setting AlertDialog for the user to confirm delete 
	private void setDeleteMovieDialog() {
		final AlertDialog deleteMovieDialog = new AlertDialog.Builder(this).create();
		deleteMovieDialog.setTitle(R.string.deleteMovie_dialog);
		deleteMovieDialog.setMessage("Are you sure you want to delete this movie?");
		deleteMovieDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// deleting movie from DB
				handler.deleteRaw(savedMovie);
				// back to MainActivity
				finish();
			}
		});
		deleteMovieDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// back to EditMovieActivity
				deleteMovieDialog.cancel();
			}
		});
		deleteMovieDialog.show();
	}
	
	public static boolean validateImageUrl(String url){
		if(!url.toLowerCase(Locale.US).startsWith("http:")){
			if(!url.toLowerCase(Locale.US).startsWith("www.")){
				return false;
			}
		}
		if(url.toLowerCase(Locale.US).endsWith(".gif")){
			return true;
		}else if(url.toLowerCase(Locale.US).endsWith(".png")){
			return true;
		}else if(url.toLowerCase(Locale.US).endsWith(".bmp")){
			return true;
		}else if(url.toLowerCase(Locale.US).endsWith(".jpg")){
			return true;
		}else if(url.toLowerCase(Locale.US).endsWith(".jpeg")){
			return true;
		}else{
			return false;
		}
	}

}//---------- End of Activity

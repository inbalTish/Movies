package com.inbaltish.movies;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inbaltish.movies.core.*;

public class SearchMovieActivity extends Activity implements OnItemClickListener, OnClickListener {
	
	private EditText editSearch;
	private Button btnFind, btnCancel;
	private ListView listResultes;
	
	private SearchMovieTask searchTask;

	static ArrayList<String> linksArray;
	private Intent intent;
	
// ----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_movie_activity);
		
		// setting the Activity's views
		editSearch = (EditText)findViewById(R.id.editTextSearchMovie);
		listResultes = (ListView)findViewById(R.id.listSearchReasults);
		listResultes.setOnItemClickListener(this);
		btnFind = (Button)findViewById(R.id.buttonFindMovie);
		btnFind.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.buttonCancelFindMovie);
		btnCancel.setOnClickListener(this);
		
	}//---------- End of onCreate()

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// getting the movie's link from the SearchTask array (by getSreachResults())
		String searchLink = linksArray.get(arg2);
		// passing the item's id to EditMovieActivity
		intent = new Intent (SearchMovieActivity.this, EditMovieActivity.class);
		intent.putExtra("requestCode", Constants.ADD_INTERNET);
		intent.putExtra("searchLink", searchLink);
		startActivity(intent);
	}

	@Override
	public void onClick(View b) {
		switch(b.getId()){
		case R.id.buttonFindMovie:
			// Starting search task
			if(searchTask != null){ // checking if a task is already in process
				if(searchTask.getStatus() != AsyncTask.Status.FINISHED){
					Log.d(Constants.getHttpTag(), "no need to start a new task");
					editSearch.setHintTextColor(getResources().getColor(R.color.red));
					editSearch.setHint("Please wait while searching...");
					return;
				}
			}
			if(editSearch.length() != 0){ // checking if there is a text to search
				String searchString = editSearch.getText().toString();
				searchTask = new SearchMovieTask(SearchMovieActivity.this, Constants.SEARCH_MOVIES);
				searchTask.execute(searchString);	
			}else{
				editSearch.setHintTextColor(getResources().getColor(R.color.red));
				editSearch.setHint("Search field is empty");
			}
			break;
		case R.id.buttonCancelFindMovie:
			if(searchTask != null){ 
				if(searchTask.getStatus() != AsyncTask.Status.FINISHED){
					searchTask.cancel(true); // (true means to interrupt in task process)			
				}else{
					finish();
				}
			}else{
				finish();
			}
			break;
		}
	}
	
	// getting array of likes from seachTask (to use in onItemClick)
	public static void getSreachResults(ArrayList<String> itemsLinks) {
		linksArray = itemsLinks;
	}

}// ---------- End of Activity

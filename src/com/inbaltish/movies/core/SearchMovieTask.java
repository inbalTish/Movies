package com.inbaltish.movies.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inbaltish.movies.R;
import com.inbaltish.movies.SearchMovieActivity;

public class SearchMovieTask extends AsyncTask<String, Integer, String>{

	private Activity searchContext;
	private int taskCode;
	private ProgressBar proBar;
	
	// SearchMovieActivity's views
	EditText editSearch;

	// EditMovieActivity's views
	EditText editTitle, editDescription, editUrl;

	ArrayList<String> itemsLinks;

	// c'tor:
	public SearchMovieTask(Activity activity, int taskCode){
		searchContext = activity;
		this.taskCode = taskCode;
	}

	@Override
	protected void onPreExecute() {
		// reseting progress Bar
		proBar = (ProgressBar)searchContext.findViewById(R.id.progressBarSearch);
		switch(taskCode){
		case Constants.SEARCH_MOVIES:
			editSearch = (EditText)searchContext.findViewById(R.id.editTextSearchMovie);
			editSearch.setText(null);
			editSearch.setHint(null);
			proBar.setVisibility(0);
			break;
		case Constants.SEARCH_MOVIE_DETAILS:
			editTitle = (EditText)searchContext.findViewById(R.id.editTextMovieTitle);
			editDescription = (EditText)searchContext.findViewById(R.id.editTextMovieDescription);
			editUrl = (EditText)searchContext.findViewById(R.id.editTextPictureUrl);
			editTitle.setText(null);
			editDescription.setText(null);
			editUrl.setText(null);
			break;
		}
	}

	@Override
	protected String doInBackground(String... searchString) {
		Log.d(Constants.getHttpTag(), "doInBackground- Starting search task");
		String result = null;
		switch(taskCode){
		case Constants.SEARCH_MOVIES:
			result = sendSearchRequest(searchString[0]); // String[0] since passing only 1 string
			break;
		case Constants.SEARCH_MOVIE_DETAILS:
			result = sendSearchRequest2(searchString[0]); 
			break;
		}
		return result;
	}

	// 1--- search request for list of movies
	private String sendSearchRequest(String searchString){
		Log.d(Constants.getHttpTag(), "sending search request");
		BufferedReader input = null;
		HttpURLConnection httpCon = null;
		StringBuilder response = new StringBuilder();

		try {
			String query = URLEncoder.encode(searchString, "utf-8");
			URL url = new URL(Constants.getSearchUrl() + query); // url for movies list search
			httpCon = (HttpURLConnection) url.openConnection();
			Log.d(Constants.getHttpTag(), "try open httpCon");
			
			// off-line mode - getting stuck here up to connection timeout error (for about 1min.)
			httpCon.setConnectTimeout(20000);
			httpCon.setReadTimeout(25000);
			Log.d(Constants.getHttpTag(), "connection timeout");

			// Check the status of the connection
			if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(Constants.getHttpTag(), "Cannot connect to: " + url.toString());
				return null;
			}

			// Use BufferReader to read the data into a string
			input = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			Log.d(Constants.getHttpTag(), "getting buffered");

			// Creating the long string using stringBuilder & setting loop for progressDialog
			String line;

			while ((line = input.readLine()) != null) {
				response.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}
		return response.toString(); // changing the stringBuilder response to return string to DoInBackground()
	}

	// 2--- search request for movie details
	private String sendSearchRequest2(String searchString) {
		Log.d(Constants.getHttpTag(), "sending search request");
		BufferedReader input = null;
		HttpURLConnection httpCon = null;
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(searchString); // url for movie's details search (searchLink)
			httpCon = (HttpURLConnection) url.openConnection();
			Log.d(Constants.getHttpTag(), "try open httpCon");

			// Check the status of the connection
			if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(Constants.getHttpTag(), "Cannot connect to: " + url.toString());
				return null;
			}

			// Use BufferReader to read the data into a string
			input = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			Log.d(Constants.getHttpTag(), "getting buffered");
			
			// Creating the long string using stringBuilder & setting loop for progressDialog
			String line;
			while ((line = input.readLine()) != null) {
				response.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}
		return response.toString(); // changing the stringBuilder response to return string to DoInBackground()		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	}

	@Override
	protected void onPostExecute(String result) {
		if(result != null && !result.equals("")){
			processResult(result);
		}else{
			Log.d(Constants.getHttpTag(), "failed to send httpCon");
			editSearch.setHintTextColor(searchContext.getResources().getColor(R.color.red));
			editSearch.setHint("Problem downloading. Try again later");
		}
		if(taskCode == Constants.SEARCH_MOVIES){
			proBar.setVisibility(8);
		}
	}

	private void processResult(String result) {
		switch(taskCode){
		case Constants.SEARCH_MOVIES:
			try{
				JSONObject resultObject = new JSONObject(result);
				JSONArray resultsArray = resultObject.getJSONArray("movies");
				Log.d(Constants.getHttpTag(), "Number of results: " + resultsArray.length());
				if(resultsArray.length() == 0){
					editSearch.setHintTextColor(searchContext.getResources().getColor(R.color.red));
					editSearch.setHint("No results were found");
					return;
				}

				ArrayList<String> listItems = new ArrayList<String>(); // arrayList to hold the items for display in listVivw
				itemsLinks = new ArrayList<String>(); // arraList to hold the self link for the item's details search

				for (int i = 0; i < resultsArray.length(); i++) {
					JSONObject movieJSON = resultsArray.getJSONObject(i);
//					String movieId = movieJSON.getString("id"); taking the movieLink instead
					String movieLink = movieJSON.getJSONObject("links").getString("self");
					String movieTitle = movieJSON.getString("title");
					String movieYear = movieJSON.getString("year");

					listItems.add(movieTitle + " (" + movieYear + ")");
					itemsLinks.add(movieLink);
				}
				SearchMovieActivity.getSreachResults(itemsLinks); // sending the movie's link to the SearchActivity

				ListView listResultsJSON = (ListView)searchContext.findViewById(R.id.listSearchReasults);
				ArrayAdapter<String> adapterJSON = new ArrayAdapter<String>(searchContext, android.R.layout.simple_list_item_1, listItems);
				listResultsJSON.setAdapter(adapterJSON);

			}catch(JSONException je){
				je.printStackTrace();
			}
			break;

		case Constants.SEARCH_MOVIE_DETAILS:
			try{
				JSONObject resultObject = new JSONObject(result);
				if(resultObject.length() == 0){
					Log.d(Constants.getHttpTag(), "Problem prossesing request for details");
					return;
				}

				// get the data from JSONObject into string parameters
				String movieTitle = resultObject.getString("title");
				String movieDescription = resultObject.getString("synopsis");
				String moviePicture = resultObject.getJSONObject("posters").getString("thumbnail");
				// set the details in EditActivity's views
				editTitle.setText(movieTitle);
				editDescription.setText(movieDescription);
				editUrl.setText(moviePicture);

			}catch(JSONException je){
				je.printStackTrace();
			}
			break;
		}
	}

	@Override
	protected void onCancelled() {
		Toast.makeText(searchContext, "Search is Cancelled", Toast.LENGTH_SHORT).show();
		super.onCancelled();
	}

}

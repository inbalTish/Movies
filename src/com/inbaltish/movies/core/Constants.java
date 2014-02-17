package com.inbaltish.movies.core;

public class Constants {
	
	// requestCodes
	public static final int ADD_MANUALLY = 11;
	public static final int ADD_INTERNET = 22;
	public static final int EDIT_MOVIE = 33;
	public static final int SAVE_MOVIE = 44;
	
	// taskCodes
	public static final int SEARCH_MOVIES = 222;
	public static final int SEARCH_MOVIE_DETAILS = 333;

	// tags
	private static final String HTTP_TAG = "HttpGetTask";
	private static final String TAG = "MovieParameters";
	
	// urls
	private static final String SEARCH_URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=tmzf5889e8wqnsdssqx3n3tz&q=";
	private static final String API_KEY = "?apikey=tmzf5889e8wqnsdssqx3n3tz";

	// ---------- getters:

	public static String getHttpTag() {
		return HTTP_TAG;
	}

	public static String getTag() {
		return TAG;
	}
	
	public static String getSearchUrl() {
		return SEARCH_URL;
	}

	public static String getApiKey() {
		return API_KEY;
	}
	
}

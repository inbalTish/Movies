package com.inbaltish.movies.core;

import com.inbaltish.movies.EditMovieActivity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Movie implements Parcelable{
	
	private int id;
	private String title;
	private String description;
	private String url;
	private int watched;
	private int rate;
	
	// CREATOR property
	public class CREATOR implements Parcelable.Creator<Movie> { // if creator is "static final" need to open "CREATOR = new Parcelable.Creator<Movie>")
		public Movie createFromParcel(Parcel source) {
			return new Movie(source);
		}
		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	}
	
	// ---------- getters & setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		if (id >= 0){
			this.id = id;
		}
		else{
		Log.d(Constants.getTag(), "ID is not valid");
		return;
		}
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if (title != null && !title.equals("")){ // I ran the text in the btnSave in EditActivity too
			this.title = title;
		}else{
			Log.d(Constants.getTag(), "Title is empty");
			return;
		}
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		if(url != null && !url.equals("")){
			if(EditMovieActivity.validateImageUrl(url) == true){
				if(url.toLowerCase().startsWith("www.")){
					this.url = "http://" + url;
				}else{
					this.url = url;
				}
			}else{
				Log.d(Constants.getTag(), "invalid url adress");
			}
		}else{
			this.url = null;
		}
	}
	
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public int getWatched() {
			return watched;
	}
	public void setWatched(int watched) {
		if(watched == 1 || watched == 0){
			this.watched = watched;
		}
	}
	
	// ---------- c'tors
	
	public Movie(Parcel source){ // for reconstruction from Parcel
		 Log.v("class Movie", "Movie(Parcel source): time to marshal parcel data");
		 id = source.readInt();
		 title = source.readString();
		 description = source.readString();
		 url = source.readString();
		 watched = source.readInt();
		 rate = source.readInt();
	}

	public Movie(String title) {
		setTitle(title);		
	}
	public Movie(int id, String title, String description, String url) {
		setId(id);
		setTitle(title);
		setDescription(description);
		setUrl(url);
	}
	public Movie(String title, String description, String url) {
		setTitle(title);
		setDescription(description);
		setUrl(url);
	}
	public Movie(int id, String title, String description, String url, int watched, int rate) {
		setId(id);
		setTitle(title);
		setDescription(description);
		setUrl(url);
		setWatched(watched);
		setRate(rate);
	}
	public Movie(String title, String description, String url, int watched, int rate) {
		setTitle(title);
		setDescription(description);
		setUrl(url);
		setWatched(watched);
	}
	
	// ---------- methods for parcel

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel destination, int flags) {
		Log.v("class Movie", "writeToParcel..."+ flags);
		destination.writeInt(id);
		destination.writeString(title);
		destination.writeString(description);
		destination.writeString(url);
		destination.writeInt(watched);
		destination.writeInt(rate);
	}
	
	public void readFromParcel(Parcel source) {
		id = source.readInt();
		title = source.readString();
		description = source.readString();
		url = source.readString();
		watched = source.readInt();
		rate = source.readInt();
	}
}

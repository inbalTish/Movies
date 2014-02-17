package com.inbaltish.movies.core;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.inbaltish.movies.MainActivity;
import com.inbaltish.movies.R;

public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

	List<Movie> items;
	Activity activity;
	int layout;
	
	Movie currentMovie;
	
	CheckBox checkBox;
	TextView txtView;
	Spinner spinner;
	
	// c'tor
	public MoviesArrayAdapter(Context context, int textViewResourceId, List<Movie> objects) {
		super(context, textViewResourceId, objects);
		items = objects;
		activity = (Activity)context;
		layout = textViewResourceId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// setting the spinner's list and adapter
		String[] spinnerList = {"Rate","1","2","3","4","5","6","7","8","9","10"};
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, spinnerList);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // specify the drop-down menu layout

		// inflating the views as defined in the list-item layout xml
		View row = convertView;
		if(row == null){
			LayoutInflater inflater = activity.getLayoutInflater();
			row = inflater.inflate(layout, parent, false);
		}

		// getting the object by its position in List
		currentMovie = items.get(position); 

		// getting each movie's parameters to display in ListView's views 
		checkBox = ((CheckBox)row.findViewById(R.id.CheckBoxWatched));
		txtView = ((TextView)row.findViewById(R.id.TextViewTitle));
		spinner = ((Spinner)row.findViewById(R.id.listItemSpinner));
		checkBox.setChecked(currentMovie.getWatched() == 1);
		txtView.setText(currentMovie.getTitle());
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(currentMovie.getRate());
		// tagging the views with adapter's position
		spinner.setTag(position);
		checkBox.setTag(position);
		txtView.setTag(position);
		
		// setting list item views with event listeners
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   // getting the clicked item by the view's tag
				   currentMovie = items.get(Integer.parseInt(buttonView.getTag() + ""));
				   // setting user's choice for DB update (from MainActivity)
				   if(buttonView.isChecked()) {
						currentMovie.setWatched(1);
					}else{
						currentMovie.setWatched(0);
					}
					((MainActivity) activity).updateItemWatched(currentMovie);
			   }
			});

		txtView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// getting the item's DBid by object's position in List
				int DBid = items.get(position).getId();
				// passing the item's DBid to MainActivity (to forward to EditMovieActivity)
				((MainActivity) activity).sendItemToEdit(DBid);			
			}
		});
		
		txtView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.registerForContextMenu(v);
				return false;
			}
		});
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// getting the clicked item by the view's tag
				currentMovie = items.get(Integer.parseInt(arg0.getTag()+""));
				// setting user's choice for DB update (from MainActivity)
				int rate = arg2;
				currentMovie.setRate(rate);
				((MainActivity) activity).updateItemRate(currentMovie);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// no need to do anything here
			}	
		});
		return row;
	}

}//---------- End of getView()

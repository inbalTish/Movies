package com.inbaltish.movies.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.inbaltish.movies.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Integer, Bitmap> {

	private Activity downloadImage;
	private ProgressDialog downloadDialog;
	
	TextView errorMsg;
	ImageView moviePicture;
	
	// c'tor
	public DownloadTask (Activity activity){ 
		downloadImage = activity;
		downloadDialog = new ProgressDialog(downloadImage); // opening a progressDialog object
	}
	
	@Override
	protected void onPreExecute() {
		// setting the ImageView to display image
		moviePicture = (ImageView)downloadImage.findViewById(R.id.imageViewMoviePicture);
//		moviePicture.setImageBitmap(null); // want to display app default photo
		
		// setting progressDialog
		downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		downloadDialog.setCancelable(true);
		downloadDialog.setMessage("Loading...");
		downloadDialog.setProgress(0);
		downloadDialog.show();
		// setting the TextView for the error massage
		errorMsg = (TextView)downloadImage.findViewById(R.id.textViewErrorMsg);
		errorMsg.setVisibility(View.INVISIBLE);
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		Log.d(Constants.getHttpTag(), "doInBackfround- Starting image download");
		Bitmap image = downloadImage(urls[0]);
		return image;
	}

	private Bitmap downloadImage(String urlString) {
		URL url;
		try{
			url = new URL(urlString);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			Log.d(Constants.getHttpTag(), "try open httpCon");

			InputStream is = httpCon.getInputStream();
			int fileLength = httpCon.getContentLength();
			
			// reading from stream to byte array
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Log.d(Constants.getHttpTag(), "getting buffered");

			int nRead, totalBytesRead = 0;
			byte[] data = new byte[2048];
			downloadDialog.setMax(fileLength);

			// read the image bytes in chunks of 2048 bytes
			while((nRead = is.read(data, 0, data.length)) != -1){
				buffer.write(data, 0, nRead);
				totalBytesRead += nRead;
				publishProgress(totalBytesRead);
			}
			buffer.flush();
			byte[] image = buffer.toByteArray();
			// turning bytes to bitmap
			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
			return bitmap;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		downloadDialog.show();
		downloadDialog.setProgress(progress[0]);
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if(result != null){ // result.getByteCount() != 0
			moviePicture.setImageBitmap(result);
		}else{
			Log.d(Constants.getHttpTag(), "failed to send httpCon");
			TextView errorMsg = (TextView)downloadImage.findViewById(R.id.textViewErrorMsg); 
			errorMsg.setVisibility(View.VISIBLE);
			errorMsg.setText("Problem downloading. Check internet connection or url");
		}
		downloadDialog.dismiss(); // close progressDialog
	}

	@Override
	protected void onCancelled() {
		Toast.makeText(downloadImage, "Download is Cancelled", Toast.LENGTH_SHORT).show();
		super.onCancelled();
	}

}

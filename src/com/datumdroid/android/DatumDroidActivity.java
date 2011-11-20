package com.datumdroid.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class DatumDroidActivity extends Activity {
	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/DatumDroid/";

	private static final String TAG = "DatumDroid";

	private static final String REQUEST_BY = "requestBy";
	private static final String ALL = "all";

	private static final String SEARCH_TERM = "searchTerm";	

	protected Button ocrButton;
	protected EditText searchTextBox;
	protected String ocrPath = DATA_PATH + "/ocr.jpg";
	protected boolean ocrTaken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	protected static final String SEARCH_QUERY = "search_query";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG, "onCreate");

	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart");

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		boolean try_copy = !(new File(DATA_PATH + "eng.traineddata")).exists();

		for (String t : paths) {
			File dir = new File(t);
			if (!dir.exists()) {
				Log.v(TAG, "creating directory [" + t + "]");
				if (!dir.mkdirs()) {
					Log.v(TAG, "error: create dir " + t + "on failed");
					try_copy = false;
					// We can still do without OCR
				}
			}
		}

		if (try_copy) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied eng traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy eng traineddata " + e.toString());
			}
		}

		try {
			boolean isWifi = checkWifiConnection();
			boolean is3G = check3gConnection();

			if ((isWifi || is3G) == false) {
				Toast toast = Toast.makeText(this, R.string.no_network,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				searchTextBox = (EditText) findViewById(R.id.searchTextBox);

				Button bsearch = (Button) findViewById(R.id.searchButton);
				bsearch.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.d(TAG, "onClick: starting service");
						Log.d(TAG, "search term sent:"
								+ searchTextBox.getText().toString());
						if (searchTextBox.getText().toString().length() == 0) {
							Toast.makeText(DatumDroidActivity.this,
									R.string.null_search_term, Toast.LENGTH_SHORT);
							Log.i(TAG, "SEARCH TERM IS EMPTY");
						} else {
							new MyTask(DatumDroidActivity.this, searchTextBox
									.getText().toString()).execute();
						}
					}
				});

				ocrButton = (Button) findViewById(R.id.ocrButton);
				ocrButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.v(TAG, "Starting OCR");
						startCameraActivity();
					}
				});

			}

		} catch (Exception e) {
			Log.e(TAG, "error is = " + e.toString());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");

	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();

	}

	protected void startCameraActivity() {
		File file = new File(ocrPath);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "OCR resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.i(TAG, "User cancelled OCR Save");
		}
	}

	protected void onPhotoTaken() {
		ocrTaken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(ocrPath, options);

		try {
			ExifInterface exif = new ExifInterface(ocrPath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "OCR Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);

				// Convert to ARGB_8888, required by tess
				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			}

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation for OCR: " + e.toString());
		}

		TessBaseAPI baseApi = new TessBaseAPI();
		// baseApi.setDebug(true);

		// Only English supported for the time being
		baseApi.init(DATA_PATH, "eng");
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text()
				.replaceAll("[^a-zA-Z0-9]+", " ").trim();

		Log.v(TAG, recognizedText);
		searchTextBox.setText(searchTextBox.getText() + " " + recognizedText);
		baseApi.end();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(PHOTO_TAKEN, ocrTaken);
		outState.putString(SEARCH_QUERY, searchTextBox.getText().toString());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(DatumDroidActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
		
		searchTextBox.setText(savedInstanceState.getString(SEARCH_QUERY));
	}

	public class MyTask extends AsyncTask<Void, Integer, Void> {
		ProgressDialog progress;
		String search;
		Context c;
		protected ServiceConnection serviceConnection;

		public MyTask(Context ctx, String s) {
			// this.progress = p;
			search = s;
			c = ctx;
			progress = new ProgressDialog(DatumDroidActivity.this); // create
																	// the
																	// progress
																	// dialog
																	// here
		}

		public void onPreExecute() {
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			progress.setCancelable(false);
			progress.show();
		}

		public Void doInBackground(Void... unused) {

			for (int i = 0; i < 1000; i++) {
				publishProgress(i);
			}

			final Intent intent = new Intent(DatumDroidActivity.this,
					HttpService.class);
			intent.putExtra(SEARCH_TERM, search);
			intent.putExtra(REQUEST_BY, ALL);
			startService(intent);
			return null;

		}

		protected void onProgressUpdate(Integer... progress) {
			setProgress(progress[0]);
		}

		public void onPostExecute(Void unused) {
			if (progress.isShowing()) {
				progress.dismiss();
			}
			stopService(new Intent(DatumDroidActivity.this, HttpService.class));
			Log.d(TAG, "Service stopped");

		}
	}

	// function to check for wifi connectivity
	public boolean checkWifiConnection() {

		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
			return true;
		} else {
			Log.d(TAG, "Wifi connection not present");
			return false;
		}
	}

	// function to check 3G connection
	public boolean check3gConnection() {
		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conman.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnected()) {
			return true;
		} else {
			Log.d(TAG, "3G connection not present");
			return false;
		}
	}

}

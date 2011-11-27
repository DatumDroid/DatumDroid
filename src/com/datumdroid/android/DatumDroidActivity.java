package com.datumdroid.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.googlecode.tesseract.android.TessBaseAPI;

public class DatumDroidActivity extends Activity {
	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/DatumDroid/";

	private static final String TAG = "DatumDroid";
	private static final String ALL = "all";
	private static String EMPTY = "empty...";
	
	private String searchedText = "";
	private int repeatedSearchPos = -1;
	private final int MAX_RECENT_SEARCH_TERMS = 5;
	String[] test = new String[MAX_RECENT_SEARCH_TERMS];
	SharedPreferences recentSearchTerms;
    SharedPreferences.Editor RSTeditor;

	protected Button ocrButton;
	protected EditText searchTextBox;
	protected String ocrPath = DATA_PATH + "/ocr.jpg";
	protected boolean ocrTaken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	protected static final String SEARCH_QUERY = "search_query";
	private static final String RECENT_SEARCH_TERMS = "Recent Search Terms";
	private static final String CONTENT_SOURCES = "Content Sources";
	private boolean insert_search = false;
	private boolean isWifi ;
	private boolean is3G;
	private boolean insert_content = false;
	private Map<String,Object> content_sources_select;
	private Map<String,String> content_sources;
	SharedPreferences contentSources;
	SharedPreferences.Editor CSeditor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		recentSearchTerms = getSharedPreferences(RECENT_SEARCH_TERMS, MODE_PRIVATE);
		RSTeditor = recentSearchTerms.edit();
		contentSources = getSharedPreferences(CONTENT_SOURCES, MODE_PRIVATE);
		CSeditor = contentSources.edit();
		CSeditor.putString("pref1", "youtube");
		CSeditor.putString("pref2", "gimages");
		CSeditor.putString("pref3", "guardian");
		CSeditor.putString("pref4", "twitter");
		CSeditor.putString("pref5", "feedzilla");
		CSeditor.commit();
		Log.i(TAG, "onCreate");

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		isWifi = checkWifiConnection();
		is3G = check3gConnection();
		
		Log.i(TAG, "onStart");
		
		if(insert_search && !(test[0].equalsIgnoreCase(searchedText))) {
			Log.w(TAG, "IN INSERT");
			for(int i = 0; i<MAX_RECENT_SEARCH_TERMS; i++) {
				if(test[i].equalsIgnoreCase(searchedText)) {
					repeatedSearchPos = i+1; // since in array it is 0-4 and in shared 
								//preferences it is 1-5
					break;
				}
			}
			if(repeatedSearchPos != -1) {
				
				for(int i = repeatedSearchPos; i>=1; i--) {
					RSTeditor.putString(Integer.toString(i),recentSearchTerms.getString(Integer.toString(i-1), EMPTY));
				}
				RSTeditor.putString(Integer.toString(1), searchedText);
				RSTeditor.commit();			
				repeatedSearchPos = -1;
			}else {
				for(int i = 5; i>=2; i--) {
					RSTeditor.putString(Integer.toString(i),recentSearchTerms.getString(Integer.toString(i-1), EMPTY));
				}
				RSTeditor.putString(Integer.toString(1), searchedText);
				RSTeditor.commit();		
			}
			insert_search = false;
		}
		for(int i = 1; i<= MAX_RECENT_SEARCH_TERMS; i++) {
//			Log.w(TAG, Integer.toString(i));
//			Log.w(TAG, recentSearchTerms.getString(Integer.toString(i), EMPTY));
			test[i-1] = recentSearchTerms.getString(Integer.toString(i), EMPTY);	        
		}
				
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.recent_search_list_item, test);
        final ListView recentSearchList = (ListView) findViewById(R.id.recentSearchList);
        recentSearchList.setAdapter(adapter);

        recentSearchList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if(recentSearchList.getItemAtPosition(position).toString().equalsIgnoreCase(EMPTY)
						== false && (isWifi || is3G) == true) {
					insert_search = true;
					searchedText = recentSearchList.getItemAtPosition(position).toString();
//					new FeedAsyncTask(DatumDroidActivity.this, 
//							searchedText, ALL).execute();
				}
				}
		});   

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
			Log.i(TAG, "onStart");
			
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
							
							searchedText = searchTextBox.getText().toString().trim();
							insert_search = true;
//							new FeedAsyncTask(DatumDroidActivity.this, searchedText,
//									ALL).execute();
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		try{
			
			if(insert_content) {
				SharedPreferences content_prefs = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor CPeditor = content_prefs.edit();
				String temp = content_prefs.getString("editTextPref", "http://");
	
				if(temp.equalsIgnoreCase("http://")== false) {
					CPeditor.putString("editTextPref", "http://");
					CPeditor.commit();
					//TODO: case where the url is bad
					
					
					//case where url is good
					CSeditor.putString("pref"+Integer.toString(content_sources_select.size()),
							temp.replace(" ", ""));
					CSeditor.commit();
					CPeditor.putBoolean("pref"+Integer.toString(content_sources_select.size()), true);
					CPeditor.commit();
					
					Log.i(TAG, "HELLO");
					insert_content = false;
				}
				//in content_sources_select everything is a boolean
				//except editTextPref(which is String)
				content_sources_select = (Map<String, Object>) content_prefs.getAll();
				content_sources = (Map<String, String>) contentSources.getAll();
				Log.i(TAG, "CONTENT PREFERENCES are = " + content_sources_select.toString());
				Log.i(TAG, "CONTENT SOURCES are = " + content_sources.toString());
			}
		
		}catch(Exception e) {
			Log.e(TAG, "error is = " + e.toString());
		}		

	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_options, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		
	    switch (item.getItemId()) {
	    case R.id.content:
	    	Log.i(TAG, "content pressed");
	    	insert_content = true;
	    	Intent myIntent = new Intent(this,SetContentPrefs.class);
            startActivityForResult(myIntent, 0);
	        return true;
	    case R.id.about:
	    	Log.i(TAG, "about pressed");
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
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

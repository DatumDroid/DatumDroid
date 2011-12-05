package com.datumdroid.android;

import java.net.URL;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.datumdroid.android.ocr.CaptureActivity;

public class DatumDroidActivity extends Activity {
	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/DatumDroid/";

	private static final String TAG = "DatumDroid";
	private static final String ALL = "all";
	
	public static final String SEARCH_QUERY_EXTRA = "search_query";
	
	private String searchedText;
	private int temp = -1;
	private final int MAX_RECENT_SEARCH_TERMS = 5;
	String[] test = new String[MAX_RECENT_SEARCH_TERMS];
//	SharedPreferences recentSearchTerms;
//    SharedPreferences.Editor RSTeditor;

	protected Button ocrButton;
	protected EditText searchTextBox;
	protected String ocrPath = DATA_PATH + "/ocr.jpg";
	protected boolean ocrTaken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	protected static final String SEARCH_QUERY = "search_query";
//	private static final String RECENT_SEARCH_TERMS = "Recent Search Terms";
	private static final String CONTENT_SOURCES = "Content Sources";
//	private boolean insert_search = false;
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
//		recentSearchTerms = getSharedPreferences(RECENT_SEARCH_TERMS, MODE_PRIVATE);
//		RSTeditor = recentSearchTerms.edit();
		contentSources = getSharedPreferences(CONTENT_SOURCES, MODE_PRIVATE);
		CSeditor = contentSources.edit();
		CSeditor.putString("pref6", "youtube.com");
		CSeditor.putString("pref7", "gimages.com");
		CSeditor.putString("pref8", "guardian.com");
		CSeditor.putString("pref9", "twitter.com");
		CSeditor.putString("pref10", "feedzilla.com");
		CSeditor.commit();
		Log.i(TAG, "onCreate");

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
				Log.i(TAG, "temp is "+temp);
				if(temp.equalsIgnoreCase("http://")== false) {
					CPeditor.putString("editTextPref", "http://");
					CPeditor.commit();
					//TODO: case where the url is bad
					Log.i(TAG, "temp is "+temp);
					URL url = new URL(temp.replace(" ", ""));
					Log.i(TAG, "temp is "+temp);
					content_sources_select = (Map<String, Object>) content_prefs.getAll();
//					content_sources = (Map<String, String>) contentSources.getAll();
							
					//case where url is good
					CSeditor.putString("pref"+Integer.toString(content_sources_select.size()),
					url.toString().replaceAll(("http://|https://"), ""));
					
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

	/*	if(insert && !(test[0].equalsIgnoreCase(searchedText))) {
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
			Log.w(TAG, Integer.toString(i));
			Log.w(TAG, recentSearchTerms.getString(Integer.toString(i), EMPTY));
			test[i-1] = recentSearchTerms.getString(Integer.toString(i), EMPTY);	        
		}
		Log.i(TAG, "Loading recentSearchList.");
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.recent_search_list_item, test);
		Log.i(TAG, "1");
        final ListView recentSearchList = (ListView) findViewById(R.id.recentSearchList);
        Log.i(TAG, findViewById(R.id.recentSearchList).toString());
        Log.i(TAG, "2");
        recentSearchList.setAdapter(adapter);
        Log.i(TAG, "OIC");
        recentSearchList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if(recentSearchList.getItemAtPosition(position).toString().equalsIgnoreCase(EMPTY)
						== false && (isWifi || is3G) == true) {
					insert_search = true;
					searchedText = recentSearchList.getItemAtPosition(position).toString();
<<<<<<< HEAD
					Intent i = new Intent(getBaseContext(), MainViewPageActivity.class);
					startActivity(i);
=======
//					new FeedAsyncTask(DatumDroidActivity.this, 
//							searchedText, ALL).execute();
				}
>>>>>>> 0a30feeeedd0fe38d4ce935fecee5833439db558
				}
			}
		});   */
	
	@Override
	protected void onStart() {
		super.onStart();
       
		Log.i(TAG, "Checking for connection.");
		try {
			isWifi = checkWifiConnection();
			is3G = check3gConnection();
			
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
							finish();
							Intent i = new Intent(getBaseContext(), MainViewPageActivity.class);
							i.putExtra(SEARCH_QUERY_EXTRA, searchedText);
							startActivity(i);
						}
					}
				});
				
				ocrButton = (Button) findViewById(R.id.ocrButton);
				ocrButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.v(TAG, "Starting OCR");
						
						// Set orientation to landscape
						// If we do not do this, the returning of the ocr result would fail
						// And force close the application, we revert this in onActivityResult()
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						Intent intent = new Intent(DatumDroidActivity.this, CaptureActivity.class);
						startActivityForResult(intent, 0);
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
	    	Intent myIntent2 = new Intent(this,AboutUs.class);
	    	startActivity(myIntent2);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			String ocrResult = data.getStringExtra("ocrResult").replaceAll("[^a-zA-Z0-9]+", " ").trim();
			if (searchTextBox.getText().length() != 0) {
				searchTextBox.append(" " + ocrResult);
			} else {
				searchTextBox.setText(ocrResult);
			}
			
			// Reset to user-pref orientation
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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

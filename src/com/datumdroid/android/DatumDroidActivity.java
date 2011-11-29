package com.datumdroid.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.datumdroid.android.ocr.CaptureActivity;

public class DatumDroidActivity extends Activity {
	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/DatumDroid/";

	private static final String TAG = "DatumDroid";
	private static final String ALL = "all";
	private static String EMPTY = "empty...";
	
	private String searchedText;
	private int temp = -1;
	private final int MAX_RECENT_SEARCH_TERMS = 5;
	String[] test = new String[MAX_RECENT_SEARCH_TERMS];
	SharedPreferences recentSearchTerms;
    SharedPreferences.Editor editor;

	protected Button ocrButton;
	protected EditText searchTextBox;
	protected static final String SEARCH_QUERY = "search_query";
	private static final String RECENT_SEARCH_TERMS = "Recent Search Terms";
	private boolean insert = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		recentSearchTerms = getSharedPreferences(RECENT_SEARCH_TERMS, MODE_PRIVATE);//getPreferences(MODE_PRIVATE);
		editor = recentSearchTerms.edit();
		Log.i(TAG, "onCreate");

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if(insert && !(test[0].equalsIgnoreCase(searchedText))) {
			Log.w(TAG, "IN INSERT");
			for(int i = 0; i<MAX_RECENT_SEARCH_TERMS; i++) {
				if(test[i].equalsIgnoreCase(searchedText)) {
					temp = i+1; // since in array it is 0-4 and in shared 
								//preferences it is 1-5
					break;
				}
			}
			if(temp != -1) {
				
				for(int i = temp; i>=1; i--) {
					editor.putString(Integer.toString(i),recentSearchTerms.getString(Integer.toString(i-1), EMPTY));
				}
				editor.putString(Integer.toString(1), searchedText);
				editor.commit();			
				temp = -1;
			}else {
				for(int i = 5; i>=2; i--) {
					editor.putString(Integer.toString(i),recentSearchTerms.getString(Integer.toString(i-1), EMPTY));
				}
				editor.putString(Integer.toString(1), searchedText);
				editor.commit();		
			}
	        insert = false;
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
						== false) {
					insert = true;
					searchedText = recentSearchList.getItemAtPosition(position).toString();
					new MyAsyncTask(DatumDroidActivity.this, 
							searchedText, ALL).execute();
				}
				}
		});   

		try {
			boolean isWifi = checkWifiConnection();
			boolean is3G = check3gConnection();
			
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
							insert = true;
							new MyAsyncTask(DatumDroidActivity.this, searchedText,
									ALL).execute();
						}
					}
				});
				
				ocrButton = (Button) findViewById(R.id.ocrButton);
				ocrButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.v(TAG, "Starting OCR");
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				searchTextBox.setText(data.getDataString());
			}
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

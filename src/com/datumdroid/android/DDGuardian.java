package com.datumdroid.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DDGuardian {

	private static final String TAG = "DatumDroid";
	private static final String TITLE = "webTitle";
	private static final String URL = "webUrl";

	public String[] titles;
	public String[] urls;

	private Cache<String> vc;

	public DDGuardian() {
		vc = new Cache<String>();
	}

	public void parseJSONData(String response) {
		try {
			JSONArray jArray = new JSONArray(response);
			Log.i(TAG, "DDGuardian:parseJSONData");

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject json_data = jArray.getJSONObject(i);

				vc.put(TITLE + Integer.toString(i), json_data.getString(TITLE));
				vc.put(URL + Integer.toString(i), json_data.getString(URL));

				// Log.i(TAG,"id: "+Integer.toString(i+1)+
				//
				// ", title: "+json_data.getString(TITLE)+
				// ", url: "+json_data.getString(URL) );

			}
		} catch (JSONException e) {

			Log.e(TAG, "Error parsing data " + e.toString());

		}
	}

	public void createAdapterStrings() {
		int size = vc.count() / 2;// since 2 values stored for each result
		titles = new String[size];
		urls = new String[size];

		for (int i = 0; i < size; i++) {
			titles[i] = vc.get(TITLE + Integer.toString(i));
			urls[i] = vc.get(URL + Integer.toString(i));
		}
		if ((titles.length != 0) && (titles.length == urls.length)) {

			Log.i(TAG,
					"DDGuardian:createAdapterStrings():strings successfully made");
		}
	}

}

package com.datumdroid.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.util.Log;

public class DDVideos extends ListActivity {

	private static final String TAG = "DatumDroid";
	private static final String TITLE = "title";
	private static final String WATCH_PAGE = "watch_page";
	private static final String THUMBNAIL = "thumbnail";

	public String[] titles;
	public String[] watchPages;
	public String[] thumbnails;
	private Cache<String> vc;

	public DDVideos() {
		vc = new Cache<String>();
	}

	public void parseJSONData(String response) {
		try {
			JSONArray jArray = new JSONArray(response);
			Log.i(TAG, "DDVideos2: parseJSONData");

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject json_data = jArray.getJSONObject(i);

				vc.put(TITLE + Integer.toString(i), json_data.getString(TITLE));
				vc.put(WATCH_PAGE + Integer.toString(i),
						json_data.getString(WATCH_PAGE));
				vc.put(THUMBNAIL + Integer.toString(i),
						json_data.getString(THUMBNAIL));
				;

				// Log.i(TAG,"id: "+Integer.toString(i+1)+
				//
				// ", title: "+json_data.getString(TITLE)+
				// ", watch_page: "+json_data.getString(WATCH_PAGE)+
				//
				// ", thumbnail: "+json_data.getString(THUMBNAIL));

			}
		} catch (JSONException e) {

			Log.e(TAG, "Error parsing data " + e.toString());
			e.printStackTrace();

		}
	}

	public void createAdapterStrings() {
		int size = vc.count() / 3;// since 3 values stored for each result
		titles = new String[size];
		watchPages = new String[size];
		thumbnails = new String[size];

		for (int i = 0; i < size; i++) {
			titles[i] = vc.get(TITLE + Integer.toString(i));
			watchPages[i] = vc.get(WATCH_PAGE + Integer.toString(i));
			thumbnails[i] = vc.get(THUMBNAIL + Integer.toString(i));
		}
		if ((titles.length != 0) && (titles.length == watchPages.length)
				&& (titles.length == thumbnails.length)) {

			Log.i(TAG,
					"DDVideos2:createAdapterStrings():strings successfully made");
		}
	}

}

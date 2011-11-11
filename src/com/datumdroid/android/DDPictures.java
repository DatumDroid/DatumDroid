package com.datumdroid.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DDPictures {
	private static final String TAG = "DatumDroid";
	private static final String URL = "imgurl";
	private static final String THUMBNAIL = "thumb";
	private static final String THUMB_SRC = "src";

	public String[] image_urls;
	public String[] thumb_srcs;

	private Cache<String> vc;

	public DDPictures() {
		vc = new Cache<String>();
	}

	public void parseJSONData(String response) {
		try {
			JSONArray jArray = new JSONArray(response);
			Log.i(TAG, "DDPictures: parseJSONData");

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject json_data = jArray.getJSONObject(i);

				vc.put(URL + Integer.toString(i), json_data.getString(URL));
				vc.put(THUMB_SRC + Integer.toString(i), json_data
						.getJSONObject(THUMBNAIL).getString(THUMB_SRC));

				// Log.i(TAG,"id: "+Integer.toString(i+1)+
				//
				// ", image_url: "+json_data.getString(URL)+
				// ", thumb_srcs: "+json_data.getString(URL));

			}
		} catch (JSONException e) {

			Log.e(TAG, "Error parsing data " + e.toString());
			e.printStackTrace();

		}
	}

	public void createAdapterStrings() {
		int size = vc.count() / 2;// since 2 values stored for each result
		image_urls = new String[size];
		thumb_srcs = new String[size];

		for (int i = 0; i < size; i++) {
			image_urls[i] = vc.get(URL + Integer.toString(i));
			thumb_srcs[i] = vc.get(THUMB_SRC + Integer.toString(i));
		}
		if ((image_urls.length != 0)
				&& (thumb_srcs.length == image_urls.length)) {

			Log.i(TAG,
					"DDPictures:createAdapterStrings():strings successfully made");
		}
	}
}

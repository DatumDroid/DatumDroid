package com.datumdroid.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DDTweets {
	private static final String TAG = "DatumDroid";
	private static final String TEXT = "text";
	private static final String PROFILE_IMAGE_URL = "profile_image_url";

	public String[] texts;
	public String[] profileImages;
	private Cache<String> vc;

	public DDTweets() {
		vc = new Cache<String>();
	}

	public void parseJSONData(String response) {
		try {
			JSONArray jArray = new JSONArray(response);
			Log.i(TAG, "DDTweets:parseJSONData");

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject json_data = jArray.getJSONObject(i);

				vc.put(TEXT + Integer.toString(i), json_data.getString(TEXT));
				vc.put(PROFILE_IMAGE_URL + Integer.toString(i),
						json_data.getString(PROFILE_IMAGE_URL));

				// Log.i(TAG,"id: "+Integer.toString(i+1)+
				//
				// ", text: "+json_data.getString(TEXT)+
				//
				// ", profile_image: "+json_data.getString(PROFILE_IMAGE_URL));
			}
		} catch (JSONException e) {

			Log.e(TAG, "Error parsing data " + e.toString());
			e.printStackTrace();

		}
	}

	public void createAdapterStrings() {
		int size = vc.count() / 2; // since 2 values stored for each result
		texts = new String[size];
		profileImages = new String[size];

		for (int i = 0; i < size; i++) {
			texts[i] = vc.get(TEXT + Integer.toString(i));
			profileImages[i] = vc.get(PROFILE_IMAGE_URL + Integer.toString(i));
		}
		if ((texts.length != 0) && (texts.length == profileImages.length)) {

			Log.i(TAG,
					"DDTweets2:createAdapterStrings():strings successfully made");
		}
	}

}

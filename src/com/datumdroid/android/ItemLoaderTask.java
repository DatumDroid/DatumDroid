package com.datumdroid.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
;

public class ItemLoaderTask extends AsyncTask<APIListAdapter.ApiParams, Void, Vector<ApiItem>> {
	private static final String TAG = ItemLoaderTask.class.getCanonicalName();
	
	public static final String API_URI = "http://www.datumdroid.appspot.com/get";
	private APIListAdapter mAPIListAdapter;
	
	public ItemLoaderTask(APIListAdapter apiListAdapter) {
		mAPIListAdapter = apiListAdapter;
	}
	
	@Override
	protected Vector<ApiItem> doInBackground(APIListAdapter.ApiParams... params) {
		Log.i(TAG, "doinBackground");
		Log.i(TAG, params[0].toString());
		
		return sendRequest(params[0]);
	}

	@Override
	protected void onPostExecute(Vector<ApiItem> result) {
		super.onPostExecute(result);
		
		for(ApiItem item : result) {
			mAPIListAdapter.addItem(item);
		}
	}
	
	/** Sends a request to the API for additional items.
	 * @param itemsPerRequest Items to load per request.
	 * @param page Page number to load.
	 */
	private Vector<ApiItem> sendRequest(APIListAdapter.ApiParams params) {
		Log.i(TAG, "sendRequest");
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Utils.createRequestString(API_URI, params.toHashSet()));
		HttpResponse response = null;
		
		try {
			 response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();
		} catch (IOException e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();
		}
		
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			InputStream is = null;
			
			try {
				is = entity.getContent();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
			} catch (IOException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
			}
			
			String responseString = Utils.convertStreamToString(is);
			Log.i(TAG, responseString);
			try {
				return parseJSONResponse(responseString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private Vector<ApiItem> parseJSONResponse(String jsonString) throws JSONException, URISyntaxException {
		JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
		JSONArray jsonArray = jsonObject.getJSONArray("articles");
		Vector<ApiItem> items = new Vector<ApiItem>();
		
		for(int i=0; i < jsonArray.length(); i++) {
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			ApiItem item = new ApiItem();
			item.title = jsonItem.getString(ApiItem.TITLE);
			item.desc = jsonItem.getString(ApiItem.DESC);
			item.link = Uri.parse(jsonItem.getString(ApiItem.LINK));
			items.add(item);
		}
		
		return items;
	}
}

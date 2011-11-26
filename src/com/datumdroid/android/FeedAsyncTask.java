package com.datumdroid.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class FeedAsyncTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "DatumDroid";
	private static final String SERVER_URL = "http://api.datumdroid.com/1.0/request.php";

	private static final String ALL = "all";

	private static final String YOUTUBE = "youtube";
	private static final String VTITLES = "vtitles";
	private static final String VTHUMBNAILS = "vthumbnails";
	private static final String VURLS = "vurls";

	private static final String TWITTER = "twitter";
	private static final String TTEXTS = "ttexts";
	private static final String TTHUMBNAILS = "tthumbnails";

	private static final String GUARDIAN = "guardian";
	private static final String GTITLES = "gtitles";
	private static final String GURLS = "gurls";

	private static final String FEEDZILLA = "feedzilla";
	private static final String FTITLES = "ftitles";
	private static final String FURLS = "furls";

	private static final String GIMAGES = "gimages";
	private static final String PURLS = "purls";
	private static final String PTHUMBNAILS = "pthumbnails";

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	
	private DDVideos video = new DDVideos();
	private DDTweets tweet = new DDTweets();
	private DDGuardian guardian = new DDGuardian();
	private DDFeedzilla feedzilla = new DDFeedzilla();
	private DDPictures picture = new DDPictures();
	private int version;

	
	ProgressDialog progress;
	String searchTerm;
	String category;
	Bundle sendBundle;
	Context ctx;

	private String url;

	private int responseCode;
	private String message;

	private String response;

	public enum RequestMethod {
		GET, POST
	}

	public String getResponse() {
		return response;
	}
	public int getVersion() {
		return version;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void SetURL(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}


	/*-------------------------------------------*/
	public FeedAsyncTask(Context c, String s, String cat) {
		ctx = c;
		searchTerm = s;
		category = cat;
		progress = new ProgressDialog(ctx); // create the progress dialog here
	}
	
	public void onPreExecute() {
		//TODO: change color of the progress dialog
		//to match the background
		Log.d(TAG, "Asynctask Started");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage("Loading...");
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();			
	}

	@Override
	protected Void doInBackground(Void... param) {
		try {
			sendBundle = new Bundle();
			SetURL(SERVER_URL);
			version = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionCode;


			if (searchTerm != null) {
				Log.i(TAG,
						"search term received:"
								+ searchTerm);
				String s = URLEncoder.encode(searchTerm,
						"UTF-8");
				Log.i(TAG, "search term AFTER:" + s);
				AddParam("q", s);
				
				//TODO : enable paging and selective adding of parameters
				// like done below for the response
				AddParam(YOUTUBE, "1");
				AddParam(TWITTER, "1");
				AddParam(GUARDIAN, "1");
				AddParam(FEEDZILLA, "1");
				AddParam(GIMAGES, "42");
				AddHeader("user", "DatumDroid/Android/" + 
						getVersion());
				
				Log.w(TAG, "BEFORE execute = " + 
						Long.toString(System.currentTimeMillis()));
				Execute(RequestMethod.GET);
				Log.w(TAG, "AFTER execute = " + 
						Long.toString(System.currentTimeMillis()));

				Log.i(TAG, "MYRESPONSE");
				Log.i(TAG, getResponse());
				Log.i(TAG, "DONE RESPONSE");
				
				/* IMPORTANT CODE */
				JSONObject jObject = (JSONObject) new JSONTokener(
						getResponse()).nextValue();
				
				
				if(category.equalsIgnoreCase(ALL) || category.equalsIgnoreCase(YOUTUBE)) {
					JSONArray jYoutube = jObject.getJSONArray(YOUTUBE);
					Log.i(TAG, "YOUTUBE:" + jYoutube.toString());
					video.parseJSONData(jYoutube.toString());
					video.createAdapterStrings();
					
					sendBundle.putStringArray(VTITLES, video.titles);
					sendBundle.putStringArray(VTHUMBNAILS, video.thumbnails);
					sendBundle.putStringArray(VURLS, video.watchPages);
					
				}
				if(category.equalsIgnoreCase(ALL) || category.equalsIgnoreCase(TWITTER)) {
					JSONArray jTwitter = jObject.getJSONArray(TWITTER);
					Log.i(TAG, "TWITTER" + jTwitter.toString());
					tweet.parseJSONData(jTwitter.toString());
					tweet.createAdapterStrings();
					
					sendBundle.putStringArray(TTEXTS, tweet.texts);
					sendBundle.putStringArray(TTHUMBNAILS, tweet.profileImages);
					
				}
				if(category.equalsIgnoreCase(ALL) || category.equalsIgnoreCase(GUARDIAN)) {
					JSONArray jGuardian = jObject.getJSONArray(GUARDIAN);
					Log.i(TAG, "GUARDIAN" + jGuardian.toString());
					guardian.parseJSONData(jGuardian.toString());
					guardian.createAdapterStrings();
					
					sendBundle.putStringArray(GTITLES, guardian.titles);
					sendBundle.putStringArray(GURLS, guardian.urls);
					
				}
				if(category.equalsIgnoreCase(ALL) || category.equalsIgnoreCase(FEEDZILLA)) {
					JSONArray jFeedzilla = jObject.getJSONArray(FEEDZILLA);
					Log.i(TAG, "FEEDZILLA" + jFeedzilla.toString());
					feedzilla.parseJSONData(jFeedzilla.toString());
					feedzilla.createAdapterStrings();
					
					sendBundle.putStringArray(FTITLES, feedzilla.titles);
					sendBundle.putStringArray(FURLS, feedzilla.urls);
					
				}
				if(category.equalsIgnoreCase(ALL) || category.equalsIgnoreCase(GIMAGES)) {
					JSONArray jGimages = jObject.getJSONArray(GIMAGES);
					Log.i(TAG, "GIMAGES" + jGimages.toString());
					picture.parseJSONData(jGimages.toString());
					picture.createAdapterStrings();

					sendBundle.putStringArray(PURLS, picture.image_urls);
					sendBundle.putStringArray(PTHUMBNAILS, picture.thumb_srcs);
					
				}
				
			}else {
				Log.i(TAG, "AsyncTask : SEARCH TERM IS NULL");
			}
		}catch (Exception e) {
			Log.e(TAG, "AsyncTask error is = " + e.toString());		
		}			
		return null;
	}
	

	public void onPostExecute(Void unused) {
		
		Intent intent1 = new Intent(ctx, IntentReceiver.class);
		intent1.putExtras(sendBundle);
		intent1.setAction("android.intent.action.PHONE_STATE");
		ctx.sendBroadcast(intent1, null);
		if (progress.isShowing()) {
			progress.dismiss();
		}
		Log.d(TAG, "Asynctask finished");

	}
	/*--------------------------------------------------*/

	public void Execute(RequestMethod post) throws Exception {
		switch (post) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "=" + p.getValue();
					if (combinedParams.length() > 1) // if more than "?" is
														// there, then this else
														// that
					{
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			Log.i(TAG, "URL:" + url + combinedParams);
			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			Log.w(TAG, "executeRequest START = " + 
					Long.toString(System.currentTimeMillis()));
			executeRequest(request, url);
			Log.w(TAG, "executeRequest FINISH = " + 
					Long.toString(System.currentTimeMillis()));
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			Log.i(TAG, "URL:" + url);
			executeRequest(request, url);
			break;
		}
		}
	}

	private void executeRequest(HttpUriRequest request, String url) {
		
		HttpClient client = new DefaultHttpClient();

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				Log.w(TAG, "convertStreamtoString START = " + 
						Long.toString(System.currentTimeMillis()));
				response = convertStreamToString(instream);
				Log.w(TAG, "convertStreamToString FINISH = " + 
						Long.toString(System.currentTimeMillis()));

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}

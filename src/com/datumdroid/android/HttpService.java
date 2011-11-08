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

import com.datumdroid.android.R;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class HttpService extends Service {

	private static final String TAG = "DatumDroid";
	private static final String SERVER_URL = "http://api.datumdroid.com/1.0/request.php";

	private static final String REQUEST_BY = "requestBy";
	private static final String SEARCH_TERM = "searchTerm";
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

	private static final String PURLS = "purls";
	private static final String PTHUMBNAILS = "pthumbnails";

	private static final String GIMAGES = "gimages";

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private DDVideos video = new DDVideos();
	private DDTweets tweet = new DDTweets();
	private DDGuardian guardian = new DDGuardian();
	private DDFeedzilla feedzilla = new DDFeedzilla();
	private DDPictures picture = new DDPictures();

	Bundle sendBundle;
	Bundle getBundle;

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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*-------------------------------------------*/
	@Override
	public void onCreate() {
		Log.d(TAG, "HttpService:onCreate");
		SetURL(SERVER_URL);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "HttpService:onStart");

		try {
			getBundle = intent.getExtras();

			if (getBundle.getString(SEARCH_TERM) != null) {
				Log.i(TAG,
						"search term received:"
								+ getBundle.getString(SEARCH_TERM));
				String s = URLEncoder.encode(getBundle.getString(SEARCH_TERM),
						"UTF-8");
				Log.i(TAG, "search term AFTER:" + s);
				AddParam("q", s);
				sendBundle = new Bundle();

				if (getBundle.getString(REQUEST_BY).equalsIgnoreCase(ALL)) {

					Log.i(TAG, "HTTPService:ALL case");
					AddParam(YOUTUBE, "1");
					AddParam(TWITTER, "1");
					AddParam(GUARDIAN, "1");
					AddParam(FEEDZILLA, "1");
					AddParam(GIMAGES, "42");
					AddHeader("user", "DatumDroid/Android/"
							+ R.string.application_version);
					Execute(RequestMethod.GET);

					/* IMPORTANT CODE */
					JSONObject jObject = (JSONObject) new JSONTokener(
							getResponse()).nextValue();

					JSONArray jYoutube = jObject.getJSONArray(YOUTUBE);
					Log.i(TAG, "YOUTUBE:" + jYoutube.toString());
					video.parseJSONData(jYoutube.toString());
					video.createAdapterStrings();

					JSONArray jTwitter = jObject.getJSONArray(TWITTER);
					Log.i(TAG, "TWITTER" + jTwitter.toString());
					tweet.parseJSONData(jTwitter.toString());
					tweet.createAdapterStrings();

					JSONArray jGuardian = jObject.getJSONArray(GUARDIAN);
					Log.i(TAG, "GUARDIAN" + jGuardian.toString());
					guardian.parseJSONData(jGuardian.toString());
					guardian.createAdapterStrings();

					JSONArray jFeedzilla = jObject.getJSONArray(FEEDZILLA);
					Log.i(TAG, "FEEDZILLA" + jFeedzilla.toString());
					feedzilla.parseJSONData(jFeedzilla.toString());
					feedzilla.createAdapterStrings();

					JSONArray jGimages = jObject.getJSONArray("gimages");
					Log.i(TAG, "GIMAGES" + jGimages.toString());
					picture.parseJSONData(jGimages.toString());
					picture.createAdapterStrings();

					Log.i(TAG, "MYRESPONSE");
					Log.i(TAG, getResponse());
					Log.i(TAG, "DONE RESPONSE");

					sendBundle.putStringArray(VTITLES, video.titles);
					sendBundle.putStringArray(VTHUMBNAILS, video.thumbnails);
					sendBundle.putStringArray(VURLS, video.watchPages);

					sendBundle.putStringArray(PURLS, picture.image_urls);
					sendBundle.putStringArray(PTHUMBNAILS, picture.thumb_srcs);

					sendBundle.putStringArray(GTITLES, guardian.titles);
					sendBundle.putStringArray(GURLS, guardian.urls);

					sendBundle.putStringArray(TTEXTS, tweet.texts);
					sendBundle.putStringArray(TTHUMBNAILS, tweet.profileImages);

					sendBundle.putStringArray(FTITLES, feedzilla.titles);
					sendBundle.putStringArray(FURLS, feedzilla.urls);

				}

				Intent intent1 = new Intent(this, IntentReceiver.class);
				intent1.putExtras(sendBundle);
				intent1.setAction("android.intent.action.PHONE_STATE");
				sendBroadcast(intent1, null);

			} else {
				Log.i(TAG, "HttpService:onStart(): SEARCH TERM IS NULL");
			}
		} catch (Exception e) {
			Log.e(TAG, "error1 is = " + e.toString());
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "HttpService:onDestroy");

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

			executeRequest(request, url);
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
				response = convertStreamToString(instream);

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

package com.datumdroid.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.util.Log;

public class Utils {
	private static final String TAG = "DatumDroid";
	
	private Utils () {
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception e) {
			Log.e(TAG, "error is =" + e.toString());
			e.printStackTrace();
		}
	}
	
	public static String createRequestString(String baseUri, HashMap<String, String> params) {
		StringBuilder sb = new StringBuilder(baseUri + "?");
		Log.i(TAG, params.toString());
		for(String key : params.keySet()) {
			Log.i(TAG, key);
			sb.append("&");
			sb.append(key);
			sb.append("=");
			try {
				sb.append(URLEncoder.encode(params.get(key), "UTF-8"));
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
		
		Log.i(TAG, sb.toString());
		return sb.toString();
	}
	
	public static String convertStreamToString(InputStream is) {
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

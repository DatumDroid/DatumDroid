package com.datumdroid.android;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.util.Log;

public class VideoCache {
	private static final String TAG = "DatumDroid";
	private HashMap<String, SoftReference<String>> cache = new HashMap<String, SoftReference<String>>();

	public String get(String id) {
		if (!cache.containsKey(id))
			return null;
		SoftReference<String> ref = cache.get(id);
		if (ref.get() == null) {
			Log.i(TAG, "ref.get() is null");
		}
		return ref.get();
	}

	public int count() {
		return cache.size();
	}

	public void put(String id, String s) {
		cache.put(id, new SoftReference<String>(s));
	}

	public void clear() {
		cache.clear();
	}
}

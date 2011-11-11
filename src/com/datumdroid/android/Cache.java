package com.datumdroid.android;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.util.Log;

public class Cache <T> {
	private static final String TAG = "DatumDroid";
	private HashMap<String, SoftReference<T>> cache = new HashMap<String, SoftReference<T>>();

	public T get(String id) {
		if (!cache.containsKey(id))
			return null;
		SoftReference<T> ref = cache.get(id);
		if (ref.get() == null) {
			Log.i(TAG, "ref.get() is null");
		}
		return ref.get();
	}

	public int count() {
		return cache.size();
	}

	public void put(String id, T s) {
		cache.put(id, new SoftReference<T>(s));
	}

	public void clear() {
		cache.clear();
	}
}

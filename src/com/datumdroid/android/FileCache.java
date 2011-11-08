package com.datumdroid.android;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCache {
	private static final String TAG = "DatumDroid";
	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		cacheDir = new File(Environment.getExternalStorageDirectory()
				.toString() + "/DatumDroid/" + "VideoThumbnails");
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// Images identified by hashcode. Not a perfect solution, good for the
		// demo.

		try {
			String filename = String.valueOf(url.hashCode());
			File f = new File(cacheDir, filename);
			return f;

		} catch (Exception e) {
			Log.e(TAG, "error in ImageLoader is =" + e.toString());
			return null;
		}

	}

	public void clear() {
		if (cacheDir.listFiles().length != 0) {

			File[] files = cacheDir.listFiles();
			for (File f : files)
				f.delete();
		}
	}

}

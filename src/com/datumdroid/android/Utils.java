package com.datumdroid.android;

import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class Utils {
	private static final String TAG = "DatumDroid";

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
}

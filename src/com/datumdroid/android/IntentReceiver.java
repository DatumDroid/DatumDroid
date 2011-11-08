package com.datumdroid.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver {

	private static final String TAG = "DatumDroid";

	Bundle getBundle;
	Bundle sendBundle;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "onReceive:receiver called");

		// get the data from the passed intent
		// this is the intent that was broadcasted by HttpService

		try {
			getBundle = intent.getExtras();
			sendBundle = new Bundle(getBundle);

			Intent i = new Intent(context, MainViewPageActivity.class);
			i.putExtras(sendBundle);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} catch (Exception e) {
			Log.e(TAG, "the error is = " + e.toString());
		}

	}

}

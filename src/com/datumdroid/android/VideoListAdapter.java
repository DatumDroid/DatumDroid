package com.datumdroid.android;

import com.datumdroid.android.R;

import android.app.Activity;
import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {

	private static final String TAG = "DatumDroid";
	private Activity activity;
	private String[] title;
	private String[] thumbnail;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public VideoListAdapter(Activity a, String[] t, String[] th) {
		try {
			activity = a;
			title = t;
			thumbnail = th;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(activity.getApplicationContext());
		} catch (Exception e) {
			Log.e(TAG, "error is = " + e.toString());
		}

	}

	public int getCount() {
		return title.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.item, null);
		}

		TextView text = (TextView) vi.findViewById(R.id.list_text);
		ImageView image = (ImageView) vi.findViewById(R.id.thumbnail);
		text.setAutoLinkMask(Linkify.WEB_URLS);
		text.setText(title[position]);
		imageLoader.DisplayImage(thumbnail[position], activity, image);

		return vi;
	}

}

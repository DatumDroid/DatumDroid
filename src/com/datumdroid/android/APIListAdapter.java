package com.datumdroid.android;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class APIListAdapter extends BaseAdapter {
	private static final String TAG = APIListAdapter.class.getCanonicalName();
	public static final int ITEMS_PER_REQUEST = 40;

	/* Number of items currently visible. */
	private int mCurrentItemCount = 0;
	private int mRequestedItemCount = 0;;
	private ItemLoaderTask mItemLoaderTask;
	private Vector<ApiItem> mItems = new Vector<ApiItem>();
	private Context mContext;
	private String mProvider;
	private String mQuery;
	private LayoutInflater mInflater;
	
	public class ApiParams {
		public String q = "";
		public String provider = "";
		public int itemsPerPage = ITEMS_PER_REQUEST;
		public int page = 0;
		
		public HashMap<String, String> toHashSet() {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("q", q);
			map.put("provider", provider);
			map.put("count", Integer.toString(itemsPerPage));
			map.put("page", Integer.toString(page));
			return map;
		}
	}
	
	public APIListAdapter (Context context, String provider, String query) {
		mItemLoaderTask = new ItemLoaderTask(this);
		mContext = context;
		mInflater = (LayoutInflater) 
					mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mProvider = provider;
		mQuery = query;
		loadMoreItems();
	}
	
	@Override
	public int getCount() {
		return mCurrentItemCount;
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item, null);
		}
		((TextView)convertView.findViewById(R.id.item_title)).setText(mItems.get(position).title);
		((TextView)convertView.findViewById(R.id.item_desc)).setText(mItems.get(position).desc);
		return convertView;
	}
	
	public void addItem(ApiItem item) {
		mItems.add(item);
		mCurrentItemCount++;
		
		notifyDataSetChanged();
	}
	
	public void loadMoreItems() {
		Log.i(TAG, "More items requested by " + mProvider);
		Log.i(TAG, mCurrentItemCount + " items loaded, " + mRequestedItemCount + " requested.");
		if(mCurrentItemCount >= mRequestedItemCount) {
			mRequestedItemCount += ITEMS_PER_REQUEST;
			ApiParams params = new ApiParams();
			params.itemsPerPage = ITEMS_PER_REQUEST;
			params.page = mRequestedItemCount/ITEMS_PER_REQUEST;
			params.provider = mProvider;
			params.q = mQuery;
			mItemLoaderTask = new ItemLoaderTask(this);
			mItemLoaderTask.execute(params);
		}
	}
}

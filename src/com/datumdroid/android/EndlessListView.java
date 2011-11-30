package com.datumdroid.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class EndlessListView extends ListView implements OnScrollListener {
	private static final String TAG = EndlessListView.class.getCanonicalName();
	private static View mFooterView;
	
	public EndlessListView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) 
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setOnScrollListener(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.i(TAG, "FVI = " + firstVisibleItem + ", VIC = " + visibleItemCount +
				", TIC = " + totalItemCount);
		if(firstVisibleItem + visibleItemCount >= totalItemCount) {
			/* We need to load more items, since we're at the end of the list */
			APIListAdapter ala = ((APIListAdapter)getAdapter());
			ala.loadMoreItems();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// We don't need to do anything here.
	}
}

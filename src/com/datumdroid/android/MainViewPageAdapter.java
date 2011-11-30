package com.datumdroid.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.datumdroid.android.R;
import com.viewpagerindicator.TitleProvider;

public class MainViewPageAdapter extends PagerAdapter implements TitleProvider {
	private static final String TAG = MainViewPageAdapter.class.getCanonicalName();
	private String mQuery;
	
	/* FIXME Really, really bad hack to meet 30th deadline. This should
	 * be properly selected by the user.
	 */
	private static final String [] SERVICE_NAMES = {
		"Feedzilla",
		"Twitter",
		"Guardian"
	};
	
	private static final String [] SERVICE_URIS = {
		"feedzilla.com",
		"twitter.com",
		"guardian.com"
	};
	
	private final Context mContext;

	public MainViewPageAdapter(Context context, String query) {
		mContext = context;
		mQuery = query;
	}

	public String getTitle(int position) {
		return SERVICE_NAMES[position];
	}

	@Override
	public int getCount() {
		return SERVICE_NAMES.length;
	}

	@Override
	public Object instantiateItem(View pager, final int position) {
		EndlessListView listView = new EndlessListView(mContext);
		listView.setAdapter(new APIListAdapter(mContext, SERVICE_URIS[position], mQuery));
		final Context context = mContext;
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Intent i = new Intent(Intent.ACTION_VIEW,
						((ApiItem)adapterView.getAdapter().getItem(position)).link);
				context.startActivity(i);
			}
		});
		
		((ViewPager) pager).addView(listView);
		return listView;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager) pager).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View view) {
	}

	@Override
	public void restoreState(Parcelable p, ClassLoader c) {
		// if ( p instanceof ScrollingState )
		// {
		// scrollPosition = ( (ScrollingState) p ).getScrollPos();
		// }
	}

	@Override
	public Parcelable saveState() {
		return null;
		// return new ScrollingState( scrollPosition );
	}

	@Override
	public void startUpdate(View view) {
	}

}
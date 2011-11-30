package com.datumdroid.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.datumdroid.android.R;
import com.viewpagerindicator.TitlePageIndicator;

public class MainViewPageActivity extends Activity {
	private static final String TAG = MainViewPageActivity.class.getCanonicalName();;
	
	private ViewPager pager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String query = getIntent().getStringExtra(DatumDroidActivity.SEARCH_QUERY_EXTRA);
		setContentView(R.layout.main_view_page);
		MainViewPageAdapter adapter = new MainViewPageAdapter(this, query);
		pager = (ViewPager) findViewById(R.id.mainviewpage);
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.title_indicator);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pager.setAdapter(null);
	}

}

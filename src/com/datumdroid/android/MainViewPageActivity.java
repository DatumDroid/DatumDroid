package com.datumdroid.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.TitlePageIndicator;

public class MainViewPageActivity extends Activity {

	private static final String TAG = "DatumDroid";

	private static final String VTITLES = "vtitles";
	private static final String VTHUMBNAILS = "vthumbnails";
	private static final String VURLS = "vurls";

	private static final String TTEXTS = "ttexts";
	private static final String TTHUMBNAILS = "tthumbnails";

	private static final String GTITLES = "gtitles";
	private static final String GURLS = "gurls";

	private static final String FTITLES = "ftitles";
	private static final String FURLS = "furls";

	private static final String PURLS = "purls";
	private static final String PTHUMBNAILS = "pthumbnails";

	private Bundle getBundle;
	private HelpStringData hsd;
	private ViewPager pager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view_page);

		Log.i(TAG, "MainViewPageActivity: onCreate");

	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			Log.i(TAG, "MainViewPageActivity: onStart");
			getBundle = this.getIntent().getExtras();

			hsd = new HelpStringData(getBundle.getStringArray(VTITLES),
					getBundle.getStringArray(VTHUMBNAILS),
					getBundle.getStringArray(VURLS),
					getBundle.getStringArray(PURLS),
					getBundle.getStringArray(PTHUMBNAILS),
					getBundle.getStringArray(GTITLES),
					getBundle.getStringArray(GURLS),
					getBundle.getStringArray(TTEXTS),
					getBundle.getStringArray(TTHUMBNAILS),
					getBundle.getStringArray(FTITLES),
					getBundle.getStringArray(FURLS));

			MainViewPageAdapter adapter = new MainViewPageAdapter(this, hsd);
			pager = (ViewPager) findViewById(R.id.mainviewpage);
			TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.title_indicator);
			pager.setAdapter(adapter);
			indicator.setViewPager(pager);

		} catch (Exception e) {
			Log.e(TAG, "Error1 is = " + e.toString());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "MainViewPageActivity: onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "MainViewPageActivity: onResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pager.setAdapter(null);

		Log.i(TAG, "MainViewPageActivity: onDestroy()");
	}

}

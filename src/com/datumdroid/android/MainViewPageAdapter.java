package com.datumdroid.android;

import android.app.Activity;
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
	private static final String TAG = "DatumDroid";

	private static String[] titles = new String[] { "Videos", "Pictures",
			"Guardian", "Tweets", "Feedzilla" };
	private static final String NORESULTSFOUND = "Sorry! No Donuts found!";

	private HelpStringData help_data;
	private VideoListAdapter vadapter;
	private PictureGridAdapter padapter;

	private final Context context;

	public MainViewPageAdapter(Context context, HelpStringData hsd) {
		this.context = context;
		help_data = new HelpStringData(hsd);
	}

	public String getTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object instantiateItem(View pager, final int position) {
		ListView lv1;
		GridView gv1;
		TextView tv1;
		LayoutInflater layoutInflater = ((Activity) this.context)
				.getLayoutInflater();
		lv1 = (ListView) layoutInflater.inflate(R.layout.videos, null);
		gv1 = (GridView) layoutInflater.inflate(R.layout.picture_grid, null);

		if (position == 0) {
			try {
				if (help_data.video_titles.length == 0) {
					tv1 = new TextView(context);
					tv1.setText(NORESULTSFOUND);
					((ViewPager) pager).addView(tv1, 0);
					return tv1;
				}
				vadapter = new VideoListAdapter((Activity) this.context,
						help_data.video_titles, help_data.video_thumbnails);
				lv1.setAdapter(vadapter);
			} catch (Exception e) {
				Log.e(TAG, "errorl is=" + e.toString());
			}

			lv1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long arg3) {

					((Activity) context).startActivity(new Intent(
							Intent.ACTION_VIEW, Uri
									.parse(help_data.video_urls[position]
											.toString())));

				}
			});

			((ViewPager) pager).addView(lv1, 0);
			return lv1;

		} else if (position == 1) {
			try {
				if (help_data.picture_thumbnails.length == 0) {
					tv1 = new TextView(context);
					tv1.setText(NORESULTSFOUND);
					((ViewPager) pager).addView(tv1, 0);
					return tv1;
				}
				padapter = new PictureGridAdapter((Activity) this.context,
						help_data.picture_thumbnails);
				gv1.setAdapter(padapter);
			} catch (Exception e) {
				Log.e(TAG, "errorl is=" + e.toString());
			}

			gv1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					((Activity) context).startActivity(new Intent(
							Intent.ACTION_VIEW, Uri
									.parse(help_data.picture_urls[position]
											.toString())));
				}
			});

			((ViewPager) pager).addView(gv1, 0);
			return gv1;

		} else if (position == 2) {
			try {
				if (help_data.guardian_titles.length == 0) {
					tv1 = new TextView(context);
					tv1.setText(NORESULTSFOUND);
					((ViewPager) pager).addView(tv1, 0);
					return tv1;
				}
				lv1.setAdapter(new ArrayAdapter<String>(((Activity) context),
						R.layout.text_item, help_data.guardian_titles));
			} catch (Exception e) {
				Log.e(TAG, "errort is=" + e.toString());
			}

			lv1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long arg3) {

					((Activity) context).startActivity(new Intent(
							Intent.ACTION_VIEW, Uri
									.parse(help_data.guardian_urls[position]
											.toString())));

				}
			});
			((ViewPager) pager).addView(lv1, 0);
			return lv1;

		} else if (position == 3) {
			try {
				if (help_data.twitter_texts.length == 0) {
					tv1 = new TextView(context);
					tv1.setText(NORESULTSFOUND);
					((ViewPager) pager).addView(tv1, 0);
					return tv1;
				}
				vadapter = new VideoListAdapter((Activity) this.context,
						help_data.twitter_texts, help_data.twitter_thumbnails);
				lv1.setAdapter(vadapter);

			} catch (Exception e) {
				Log.e(TAG, "errort is=" + e.toString());
			}
			((ViewPager) pager).addView(lv1, 0);
			return lv1;

		} else {

			try {
				if (help_data.feedzilla_titles.length == 0) {
					tv1 = new TextView(context);
					tv1.setText(NORESULTSFOUND);
					((ViewPager) pager).addView(tv1, 0);
					return tv1;
				}
				lv1.setAdapter(new ArrayAdapter<String>(((Activity) context),
						R.layout.text_item, help_data.feedzilla_titles));

			} catch (Exception e) {
				Log.e(TAG, "errort is=" + e.toString());
			}

			lv1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long arg3) {

					((Activity) context).startActivity(new Intent(
							Intent.ACTION_VIEW, Uri
									.parse(help_data.feedzilla_urls[position]
											.toString())));

				}
			});
			((ViewPager) pager).addView(lv1, 0);
			return lv1;
		}

	}

	@Override
	public void destroyItem(View pager, int position, Object view) {
		if (position == 0) {
			try {
				if (help_data.video_titles.length == 0) {
					Log.i(TAG, "TEXTVIEW:videos is cleared");
					((ViewPager) pager).removeView((TextView) view);
				} else {
					vadapter.imageLoader.stopThread();
					vadapter.imageLoader.clearCache();
					((ViewPager) pager).removeView((ListView) view);
					Log.i(TAG,
							"LISTVIEW:videos :cleared video cache and stopped the thread");
				}
			} catch (Exception e) {
				Log.e(TAG, "errord is=" + e.toString());
			}

		} else if (position == 1) {

			try {
				if (help_data.picture_thumbnails.length == 0) {
					Log.i(TAG, "TEXTVIEW:pictures is cleared");
					((ViewPager) pager).removeView((TextView) view);
				} else {
					padapter.imageLoader.stopThread();
					padapter.imageLoader.clearCache();
					Log.i(TAG,
							"GRIDVIEW: pictures:cleared video cache and stopped the thread");
					((ViewPager) pager).removeView((GridView) view);
				}

			} catch (Exception e) {
				Log.e(TAG, "errord is=" + e.toString());
			}

		} else if (position == 2) {
			try {
				if (help_data.guardian_titles.length == 0) {
					Log.i(TAG, "TEXTVIEW:guardian is cleared");
					((ViewPager) pager).removeView((TextView) view);
				} else {
					Log.i(TAG, "LISTVIEW:guardian is cleared");
					((ViewPager) pager).removeView((ListView) view);
				}
			} catch (Exception e) {
				Log.e(TAG, "errord is=" + e.toString());
			}

		} else if (position == 3) {
			try {
				if (help_data.twitter_texts.length == 0) {
					Log.i(TAG, "TEXTVIEW:tweets is cleared");
					((ViewPager) pager).removeView((TextView) view);
				} else {
					vadapter.imageLoader.stopThread();
					vadapter.imageLoader.clearCache();

					Log.i(TAG,
							"LISTVIEW:tweets :cleared video cache and stopped the thread");
					((ViewPager) pager).removeView((ListView) view);
				}

			} catch (Exception e) {
				Log.e(TAG, "errord is=" + e.toString());
			}

		} else {
			try {
				if (help_data.feedzilla_titles.length == 0) {
					Log.i(TAG, "TEXTVIEW:feedzilla is cleared");
					((ViewPager) pager).removeView((TextView) view);
				} else {
					Log.i(TAG, "LISTVIEW:feedzilla is cleared");
					((ViewPager) pager).removeView((ListView) view);
				}

			} catch (Exception e) {
				Log.e(TAG, "errord is=" + e.toString());
			}

		}
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
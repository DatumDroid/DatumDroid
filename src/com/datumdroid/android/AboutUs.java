package com.datumdroid.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutUs extends Activity{

	private static final String DESCRIPTION = 
		"DESCRIPTION:" +
		"\n\nWe believe that information should be available to anyone at any time " +
		"in any form. One should not be bounded by only one media or source of information " +
		"and should be able to explore other forms of the same information at that very instant. " +
		"In order to achieve this goal of ours we created DatumDroid. We use" +
		" OCR technology to recognize what you are reading and present the information to you " +
		"in various forms (visual, graphical, print etc.) from popular sources, so that you do not" +
		" miss what the world is thinking!" +
		"\nVISIT US AT- http://www.datumdroid.com"+
		"\n\nAUTHORS:" +
		"\n\nAviral Dasgupta, Grade 10, Loyola International School, Jharkhand, India \n" +
		"\nDevashish Sharma, Computer Engineering, University of Waterloo, Canada\n" +
		"\nGautam Gupta, Grade 10, Amity International School, Sector 46, Gurgaon, Haryana, India";;
	

		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		TextView description = (TextView) findViewById(R.id.description);
		description.setText(DESCRIPTION);
		description.setAutoLinkMask(Linkify.ALL);
	
	}
	

}

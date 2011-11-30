package com.datumdroid.android;

import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

public class SetContentPrefs extends PreferenceActivity{
	private static final String TAG = "DatumDroid";
	private static final String CONTENT_SOURCES = "Content Sources";
	CheckBoxPreference checkBoxPreference;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     try{
	    	 
	    	 addPreferencesFromResource(com.datumdroid.android.R.xml.content_preference);	    	 
	    	 //fetch the item where you wish to insert the CheckBoxPreference, in this case a PreferenceCategory with key "targetCategory"
		        PreferenceCategory targetCategory = (PreferenceCategory)findPreference("contentCategory");
		        
		        SharedPreferences temp = getSharedPreferences(CONTENT_SOURCES, MODE_PRIVATE);
		        Map<String, String> tempContent = (Map<String, String>) temp.getAll();
		        
		        for(int i = 6; i<=tempContent.size(); i++) { // as first 5 are already there
		        	//create one check box for each setting you need
			        checkBoxPreference = new CheckBoxPreference(this);
			        //make sure each key is unique  
			        checkBoxPreference.setKey("pref"+Integer.toString(i));
			        checkBoxPreference.setTitle(tempContent.get("pref"+Integer.toString(i)));
			        checkBoxPreference.setChecked(true);
			        targetCategory.addPreference(checkBoxPreference);
		        	
		        }
        
	     }catch(Exception e){
	    	 Log.e(TAG, "error is = " + e.toString());
	    	 
	     }
  
	 }

}

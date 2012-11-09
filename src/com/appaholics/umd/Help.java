package com.appaholics.umd;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Help extends Activity {

	ActionBar ab;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	boolean modPrimary;
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.help);
		ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		
		
		prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		editor = prefs.edit();
		
		modPrimary = prefs.getBoolean("editPrimary", false);
		
		WebView webView = (WebView) findViewById(R.id.helpWebView);
		
		webView.loadUrl("file:///android_asset/index.html");
		
		CheckBox box = (CheckBox) findViewById(R.id.primaryModBox);
		
		if(modPrimary)
		{
			box.setChecked(true);
		}
		
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					editor.putBoolean("editPrimary", true);
					editor.commit();
				}
				else
				{
					editor.putBoolean("editPrimary", false);
					editor.commit();
				}
				
			}
			
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}

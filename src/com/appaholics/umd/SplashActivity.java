package com.appaholics.umd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	      setContentView(R.layout.splash);
	      Toast toast = Toast.makeText(getBaseContext(), "Data may take some time to load. Please be patient.", Toast.LENGTH_LONG);
	      toast.show();
	      Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 3000) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	               finish();
	               Intent ide = new Intent();
	               ide.setClassName("com.appaholics.umd",
	                              "com.appaholics.umd.MainActivity");
	               startActivity(ide);
	            }
	         }
	      };
	      splashThread.start();
	   }
	   @Override
	   public void onBackPressed() {

	      return;
	   }
}
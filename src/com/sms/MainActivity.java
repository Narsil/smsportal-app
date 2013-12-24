package com.sms;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String TAG = "SMSService";


	
	private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SMSService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	protected void resetBtn(){
        Button btn = (Button) findViewById(R.id.btn);
        if (!isServiceRunning()){
		    btn.setOnClickListener(new OnClickListener() {
		
		           @Override
		           public void onClick(View v) {
		                 // TODO Auto-generated method stub
		                 startService(new Intent(getBaseContext(), SMSService.class));
		                 resetBtn();
		           }
		    });
		    btn.setText("Start");
        }else{
		   btn.setOnClickListener(new OnClickListener() {
		           @Override
		           public void onClick(View v) {
		                 // TODO Auto-generated method stub
		                 stopService(new Intent(getBaseContext(), SMSService.class));
		                 resetBtn();
		           }
		    });
		   btn.setText("Stop");
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Starting MainActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        resetBtn();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

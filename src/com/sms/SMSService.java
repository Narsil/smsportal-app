package com.sms;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {
	private static final String TAG = "SMSService";
	private int DELTA = 10;
	private static final int DELTA_MULTIPLIER = 3;
	private static final int START_DELTA = 10;
	private static final int MAX_DELTA = 3600;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Created", Toast.LENGTH_SHORT).show();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Running ", Toast.LENGTH_SHORT).show();
		new RetreiveMessageTask(this).execute(Defaults.URL);
		
		

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, DELTA);
    	Log.v(TAG, "Resetting next fetch in : " + DELTA + " seconds");
       
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        
		return super.onStartCommand(intent, flags, startId);
	}

	public void resetDelta() {
		DELTA = START_DELTA;		
	}

	public void increaseDelta() {
		DELTA *= DELTA_MULTIPLIER;
		if (DELTA > MAX_DELTA){
			DELTA = MAX_DELTA;
		}
		
	}

}

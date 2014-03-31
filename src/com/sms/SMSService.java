package com.sms;

import java.util.ArrayList;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {
	private static final String TAG = "SMSService";
	private static int DELTA = 10;
	private static final int DELTA_MULTIPLIER = 3;
	private static final int START_DELTA = 10;
	private static final int MAX_DELTA = 600;
	private static boolean isRunning = false;
	
	// Messages
	public static final int NEW_DELAY = 0;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    @SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case MSG_UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }
	
    private void sendMessageToUI(int delta) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                // Send data as an Integer
                mClients.get(i).send(Message.obtain(null, NEW_DELAY, delta, 0));

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Created", Toast.LENGTH_SHORT).show();
		isRunning = true;
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
		isRunning = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "SMSService Running ", Toast.LENGTH_SHORT).show();
		new RetreiveMessageTask(this).execute(Defaults.URL);
		
		

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, DELTA);
    	Log.v(TAG, "Resetting next fetch in : " + DELTA + " seconds");
    	sendMessageToUI(DELTA);
       
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        
		return super.onStartCommand(intent, flags, startId);
	}

	public static void resetDelta() {
		DELTA = START_DELTA;		
	}

	public void increaseDelta() {
		DELTA *= DELTA_MULTIPLIER;
		if (DELTA > MAX_DELTA){
			DELTA = MAX_DELTA;
		}
		
	}
	
    public static boolean isRunning()
    {
        return isRunning;
    }

}

package com.sms;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "SMSService";
	Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    @SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SMSService.NEW_DELAY:
            	TextView txt = (TextView) findViewById(R.id.delaytext);
                txt.setText("New Delay: " + msg.arg1);
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
    	public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, SMSService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }
        
		@Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
			mService = null;
        }
    };
    
    void doBindService() {
        bindService(new Intent(this, SMSService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SMSService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
	
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
		        	     startService(new Intent(MainActivity.this, SMSService.class));
		        	     doBindService();
		                 resetBtn();
		           }
		    });
		    btn.setText("Start");
        }else{
		   btn.setOnClickListener(new OnClickListener() {
		           @Override
		           public void onClick(View v) {
		                 // TODO Auto-generated method stub
		        	     doUnbindService();
		        	     stopService(new Intent(MainActivity.this, SMSService.class));
		                 
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

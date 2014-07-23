package com.sms;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.os.AsyncTask;
import android.util.Log;

class TouchTask extends AsyncTask<String, Void, Void>{
		private static final String TAG = "TouchTask";
	
		@Override
		protected Void doInBackground(String... urls) {
			// TODO Auto-generated method stub
	    	DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
	    	for (String url : urls){
		    	Log.v(TAG, "Touch URL: " + url);
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Content-type", "application/json");
				try {
					HttpResponse httpResponse = httpclient.execute(httppost);
					httpResponse.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	return null;
		}

	}
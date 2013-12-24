package com.sms;

import java.io.IOException;

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
	    	Log.v(TAG, "Touch URL: " + urls[0]);
			HttpPost httppost = new HttpPost(urls[0]);
			httppost.setHeader("Content-type", "application/json");
			try {
				httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}
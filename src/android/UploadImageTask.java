package com.checkroom.plugin.cameraattachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class UploadImageTask extends AsyncTask<String, Void, String> {

	private String mUrl;
	private UploadImageTaskCallback mCallback;
	private String imageBase64;
	private int httpStatusCode;

	public UploadImageTask(UploadImageTaskCallback callback, String url,
			String imageBase64) {
		mUrl = url;
		mCallback = callback;
		this.imageBase64 = imageBase64;
	}

	@Override
	protected String doInBackground(String... str) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("data", imageBase64));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(mUrl);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			httpStatusCode = response.getStatusLine().getStatusCode();

			if (httpStatusCode == 200) {
				return convertResponseToString(response);
			}
		} catch (Exception e) {
			if (e instanceof HttpHostConnectException) {
				httpStatusCode = 404;
				return "error";
			}
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
		return "error";
	}

	private String convertResponseToString(HttpResponse response) {
		String res = "";
		StringBuffer buffer = new StringBuffer();
		try {
			InputStream inputStream = response.getEntity().getContent();
			int contentLength = (int) response.getEntity().getContentLength();
			if (contentLength >= 0) {
				byte[] data = new byte[512];
				int len = 0;
				while (-1 != (len = inputStream.read(data))) {
					buffer.append(new String(data, 0, len));
				}
				inputStream.close();
				res = buffer.toString();
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result.equals("error")) {
			mCallback.onUploadCompleted(false, result, httpStatusCode);
		} else {
			mCallback.onUploadCompleted(true, result, 200);
		}
	}

	public interface UploadImageTaskCallback {
		public void onUploadCompleted(boolean success, String result,
				int httpStatusCode);
	}
}
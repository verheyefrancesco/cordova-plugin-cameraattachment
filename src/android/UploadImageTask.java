package com.checkroom.plugin.cameraattachment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class UploadImageTask extends AsyncTask<String, Void, String> {

	private InputStream inputStream;
	private String mUrl;

	private Bitmap mBitmap;

	private UploadImageTaskCallback mCallback;

	public UploadImageTask(UploadImageTaskCallback callback, String url,
			Bitmap bitmap) {
		mUrl = url;
		mBitmap = bitmap;
		mCallback = callback;
	}

	@Override
	protected String doInBackground(String... str) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);

		byte[] ba = bao.toByteArray();
		int flag = 0; // you can pass the default 0 = Base64.DEFAULT
		String ba1 = Base64.encodeToString(ba, flag);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("data", ba1));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(mUrl);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			// HttpEntity entity = response.getEntity();
			// InputStream is = entity.getContent();

			String result = convertResponseToString(response);
			return result;
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
		return null;
	}

	private String convertResponseToString(HttpResponse response)
			throws IllegalStateException, IOException {
		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength();
		if (contentLength < 0) {
		} else {
			byte[] data = new byte[512];
			int len = 0;
			try {
				while (-1 != (len = inputStream.read(data))) {
					buffer.append(new String(data, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			res = buffer.toString();
		}
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result == null) {
			mCallback.onUploadCompleted(false, null);
		} else {
			mCallback.onUploadCompleted(true, result);
		}
	}

	public interface UploadImageTaskCallback {
		public void onUploadCompleted(boolean success, String result);
	}
}
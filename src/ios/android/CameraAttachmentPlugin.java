package com.checkroom.plugin.cameraattachment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.checkroom.plugin.cameraattachment.CameraAttachmentDialog.CameraAttachmentCallback;

public class CameraAttachmentPlugin extends CordovaPlugin implements
		CameraAttachmentCallback {
	private final String pluginName = "CameraAttachmentPlugin";

	private static final String ARG_UPLOADURL = "uploadUrl";

	private String uploadUrl;

	private CallbackContext callbackContext;
	protected CameraAttachmentDialog cameraAttachmentDialog;

	@Override
	public boolean execute(final String action, final JSONArray data,
			final CallbackContext callbackContext) {
		Log.d(pluginName, pluginName + " called with options: " + data);
		boolean result = false;

		this.show(data, callbackContext);

		this.callbackContext = callbackContext;

		result = true;

		return result;
	}

	public synchronized void show(final JSONArray data,
			final CallbackContext callbackContext) {
		readParametersFromData(data);
		this.cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				showCameraAttachmentDialog();
			}
		});

	}

	private void readParametersFromData(JSONArray data) {
		try {
			JSONObject obj = data.getJSONObject(0);

			if (obj.has(ARG_UPLOADURL)) {
				uploadUrl = obj.getString(ARG_UPLOADURL);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showCameraAttachmentDialog() {
		cameraAttachmentDialog = new CameraAttachmentDialog(
				cordova.getActivity(),
				android.R.style.Theme_Black_NoTitleBar_Fullscreen, this);
		cameraAttachmentDialog.uploadurl = uploadUrl;
		cameraAttachmentDialog.show();
	}

	/* CameraAttachmentCallback */
	@Override
	public void onCancelled() {
		cameraAttachmentDialog.dismiss();
		callbackContext.success("canceled");
	}

	@Override
	public void onUploadedWithResult(String result) {
		cameraAttachmentDialog.dismiss();
		callbackContext.success(result);
	}
}

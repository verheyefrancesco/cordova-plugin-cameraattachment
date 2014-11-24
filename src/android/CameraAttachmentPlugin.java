package com.checkroom.plugin.cameraattachment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CameraAttachmentPlugin extends CordovaPlugin {
	private final String pluginName = "CameraAttachmentPlugin";

	private CameraAttachmentDialog cameraAttachmentFragment;

	private CallbackContext callbackContext;
	protected CameraAttachmentDialog cameraAttachment;

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
		showCameraAttachmentDialog();
	}

	private void readParametersFromData(JSONArray data) {
		try {
			JSONObject obj = data.getJSONObject(0);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showCameraAttachmentDialog() {
		cameraAttachmentFragment = new CameraAttachmentDialog(
				cordova.getActivity(),
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		cameraAttachmentFragment.show();
	}
}

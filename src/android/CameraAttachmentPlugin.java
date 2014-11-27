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
	private static final String ARG_CANCEL_BUTTON_TEXT = "cancelButtonText";
	private static final String ARG_USE_PHOTO_BUTTON_TEXT = "usePhotoButtonText";
	private static final String ARG_RETAKE_BUTTON_TEXT = "retakeButtonText";
	private static final String ARG_PHOTO_SIZE_WIDTH = "photoSizeWidth";
	private static final String ARG_PHOTO_SIZE_HEIGHT = "photoSizeHeight";

	private String uploadUrl;
	private String cancelButtonText = "Cancel";
	private String usePhotoButtonText = "Use Photo";
	private String retakeButtonText = "Retake";
	private int photoSizeWidth = -1;
	private int photoSizeHeight = -1;

	private CameraAttachmentConfig mConfig;

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

		createConfig();

		showCameraAttachmentDialog();
	}

	private void readParametersFromData(JSONArray data) {
		try {
			JSONObject obj = data.getJSONObject(0);
			if (obj.has(ARG_UPLOADURL)) {
				uploadUrl = obj.getString(ARG_UPLOADURL);
			}
			if (obj.has(ARG_CANCEL_BUTTON_TEXT)) {
				cancelButtonText = obj.getString(ARG_CANCEL_BUTTON_TEXT);
			}
			if (obj.has(ARG_USE_PHOTO_BUTTON_TEXT)) {
				usePhotoButtonText = obj.getString(ARG_USE_PHOTO_BUTTON_TEXT);
			}
			if (obj.has(ARG_RETAKE_BUTTON_TEXT)) {
				retakeButtonText = obj.getString(ARG_RETAKE_BUTTON_TEXT);
			}
			if (obj.has(ARG_PHOTO_SIZE_WIDTH)) {
				photoSizeWidth = obj.getInt(ARG_PHOTO_SIZE_WIDTH);
			}
			if (obj.has(ARG_PHOTO_SIZE_HEIGHT)) {
				photoSizeHeight = obj.getInt(ARG_PHOTO_SIZE_HEIGHT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void createConfig() {
		mConfig = new CameraAttachmentConfig(uploadUrl, cancelButtonText,
				usePhotoButtonText, retakeButtonText, photoSizeWidth,
				photoSizeHeight);
	}

	private void showCameraAttachmentDialog() {
		this.cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				cameraAttachmentDialog = new CameraAttachmentDialog(cordova
						.getActivity(),
						android.R.style.Theme_Black_NoTitleBar_Fullscreen,
						CameraAttachmentPlugin.this);
				cameraAttachmentDialog.setConfig(mConfig);
				cameraAttachmentDialog.show();
			}
		});
	}

	/* CameraAttachmentCallback */
	@Override
	public void onCancelled() {
		callbackContext.success("cancelled");
		cameraAttachmentDialog.dismiss();
	}

	@Override
	public void onUploadedWithResult(String result) {
		callbackContext.success(result);
		cameraAttachmentDialog.dismiss();

	}
}

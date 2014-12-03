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
	private static final String ARG_PHOTO_SIZE = "photoSize";
	private static final String ARG_ARG_BASE_64 = "argBase64";
	private static final String ARG_ARG_FILENAME = "argFileName";
	private static final String ARG_FILENAME = "fileName";

	private String uploadUrl;
	private String cancelButtonText = "Cancel";
	private String usePhotoButtonText = "Use Photo";
	private String retakeButtonText = "Retake";
	private String photoSize = "medium";
	private String argBase64 = "fileContents";
	private String argFileName;
	private String fileName;

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
			if (obj.has(ARG_PHOTO_SIZE)) {
				photoSize = obj.getString(ARG_PHOTO_SIZE);
			}
			if (obj.has(ARG_ARG_BASE_64)) {
				argBase64 = obj.getString(ARG_ARG_BASE_64);
			}
			if (obj.has(ARG_ARG_FILENAME)) {
				String argfn = obj.getString(ARG_ARG_FILENAME);
				if (!argfn.equals("")) {
					argFileName = argfn;
				}
			}
			if (obj.has(ARG_FILENAME)) {
				String fn = obj.getString(ARG_FILENAME);
				if (!fn.equals("")) {
					fileName = fn;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void createConfig() {
		mConfig = new CameraAttachmentConfig(uploadUrl, cancelButtonText,
				usePhotoButtonText, retakeButtonText, photoSize, argBase64,
				argFileName, fileName);
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
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("status", "cancelled");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String result = resultObj.toString();
		callbackContext.success(result);
		cameraAttachmentDialog.dismiss();
	}

	@Override
	public void onUploadedWithResult(String result, int httpStatusCode) {
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("status", "success");

			JSONObject dataResultObj = new JSONObject(result);
			resultObj.put("data", dataResultObj);
			resultObj.put("code", httpStatusCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String resultString = resultObj.toString();
		callbackContext.success(resultString);
		cameraAttachmentDialog.dismiss();
	}

	// success {status: 'success', data: somedatadict}
	// error {status: 'error', code: 500, data: 'some error message'}
	// cancel {status: 'cancelled'}
	// system {status: 'system', code: 999, data: 'No more disk space'}
}

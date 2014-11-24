package com.checkroom.plugin.cameraattachment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CameraAttacher extends CordovaPlugin {
	private final String pluginName = "CameraAttachmentPlugin";

	private static final String ARG_MODE = "mode";

	private RelativeLayout main;
	private CameraAttachment cameraAttachmentFragment;

	private CallbackContext callbackContext;
	protected ViewGroup root; // original Cordova layout
	protected CameraAttachment cameraAttachment; // new layout

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
		showCameraAttachmentFragmeng();
	}

	private void readParametersFromData(JSONArray data) {
		try {
			JSONObject obj = data.getJSONObject(0);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showCameraAttachmentFragmeng() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				main = new RelativeLayout(cordova.getActivity());

				root = (ViewGroup) webView.getParent();
				root.removeView(webView);
				main.addView(webView);

				addCameraAttachmentToLayout();

				cordova.getActivity().setContentView(main);
			}
		};
		cordova.getActivity().runOnUiThread(runnable);
	}

	private void addCameraAttachmentToLayout() {
		cameraAttachmentFragment = new CameraAttachment(cordova.getActivity());

		main.addView(cameraAttachment);
	}
}

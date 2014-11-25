package com.checkroom.plugin.cameraattachment;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	public SurfaceHolder mHolder;
	private Camera mCamera;
	private PreviewCallback previewCallback;
	private AutoFocusCallback autoFocusCallback;
	private Boolean mIsPhone;
	private Boolean isFocusModeContiniousPicture = false;

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera,
			PreviewCallback previewCb, AutoFocusCallback autoFocusCb) {
		super(context);
		mCamera = camera;
		previewCallback = previewCb;
		autoFocusCallback = autoFocusCb;

		mIsPhone = true;

		// Set focus mode to continuous picture if possible
		Camera.Parameters parameters = camera.getParameters();
		String focusModus = parameters.get("focus-mode-values");

		if (focusModus.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			Camera.Parameters params = mCamera.getParameters();
			params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			mCamera.setParameters(params);
			autoFocusCallback = null;
			isFocusModeContiniousPicture = true;
		}

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			// Fix for "autofocus failed"
			// http://stackoverflow.com/questions/8820702/autofocus-throwing-exception
			setAutoFocusIfSupported();
		} catch (IOException e) {
			Log.d("DBG", "Error setting camera preview: " + e.getMessage());
		}
	}

	private void setAutoFocusIfSupported() {
		if (autoFocusCallback != null) {
			Camera.Parameters p = mCamera.getParameters();
			List<String> focusModes = p.getSupportedFocusModes();
			if (focusModes != null
					&& focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// Phone supports autofocus!
				mCamera.autoFocus(autoFocusCallback);
			} else {
				// Phone does not support autofocus!
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Camera preview released in activity
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		/*
		 * If your preview can change or rotate, take care of those events here.
		 * Make sure to stop the preview before resizing or reformatting it.
		 */
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		try {
			// Hard code camera surface rotation 90 degs to match Activity view
			// in portrait
			if (mIsPhone) {
				mCamera.setDisplayOrientation(90);
			} else {
				mCamera.setDisplayOrientation(0);
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.set("orientation", "landscape");
				// set other parameters ..
				mCamera.setParameters(parameters);
			}
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setPreviewCallback(previewCallback);
			mCamera.startPreview();
			if (!isFocusModeContiniousPicture) {
				mCamera.autoFocus(autoFocusCallback);
			}
		} catch (Exception e) {
			Log.d("DBG", "Error starting camera preview: " + e.getMessage());
		}
	}
}

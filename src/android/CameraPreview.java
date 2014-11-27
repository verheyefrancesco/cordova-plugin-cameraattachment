package com.checkroom.plugin.cameraattachment;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	public SurfaceHolder mHolder;
	private Camera mCamera;
	private PreviewCallback previewCallback;
	private AutoFocusCallback autoFocusCallback;
	private Boolean mIsPhone;
	private Boolean isFocusModeContiniousPicture = false;
	private Context mContext;

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera,
			PreviewCallback previewCb, AutoFocusCallback autoFocusCb) {
		super(context);
		mCamera = camera;
		mContext = context;
		previewCallback = previewCb;
		autoFocusCallback = autoFocusCb;

		mIsPhone = true;

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
			setAutoFocusIfSupported();
		} catch (IOException e) {
			Log.d("DBG", "Error setting camera preview: " + e.getMessage());
		}
	}

	@SuppressLint("InlinedApi")
	private void setAutoFocusIfSupported() {
		Camera.Parameters parameters = mCamera.getParameters();
		String focusModes = parameters.get("focus-mode-values");

		if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			Camera.Parameters params = mCamera.getParameters();
			params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			mCamera.setParameters(params);
			autoFocusCallback = null;
			isFocusModeContiniousPicture = true;
		} else {
			if (autoFocusCallback != null) {
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					// Phone supports autofocus!
					mCamera.autoFocus(autoFocusCallback);
				} else {
					// Phone does not support autofocus!
				}
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Camera preview released in activity
	}

	private int mRotation = 90;

	public int getSurfaceRotation() {
		return mRotation;
	}

	public enum TheOrientation {
		PORTRAIT, PORTRAIT_UPSIDE_DOWN, LANDSCAPE_LEFT, LANDSCAPE_RIGHT
	}

	public TheOrientation mTheOrientation;

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
			int orientation = mContext.getResources().getConfiguration().orientation;

			switch (((WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_0:
				mRotation = 90;
				mTheOrientation = TheOrientation.PORTRAIT;
				break;
			case Surface.ROTATION_90:
				mRotation = 0;
				mTheOrientation = TheOrientation.LANDSCAPE_LEFT;
				break;
			case Surface.ROTATION_180:
				mRotation = 270;
				mTheOrientation = TheOrientation.PORTRAIT_UPSIDE_DOWN;
				break;
			case Surface.ROTATION_270:
				mRotation = 180;
				mTheOrientation = TheOrientation.LANDSCAPE_RIGHT;
				break;
			default:
				mRotation = 90;
			}

			if (mOrientationCallback != null) {
				mOrientationCallback.onOrientationChanged(mTheOrientation);
			}

			mCamera.setDisplayOrientation(mRotation);

			if (mIsPhone) {
				if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					// mCamera.setDisplayOrientation(90);
				} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					// mCamera.setDisplayOrientation(180);
				}
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

	public void setCamera(Camera camera) {
		mCamera = camera;
	}

	public TheOrientationCallback mOrientationCallback;

	public interface TheOrientationCallback {
		public void onOrientationChanged(TheOrientation orientation);
	}
}

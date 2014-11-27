package com.checkroom.plugin.cameraattachment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkroom.plugin.cameraattachment.UploadImageTask.UploadImageTaskCallback;

public class CameraAttachmentDialog extends Dialog implements PreviewCallback,
		android.view.View.OnClickListener, UploadImageTaskCallback {

	private Context mContext;
	private FrameLayout flPreview;
	private Button btnTakePicture;
	private Button btnUsePhoto;
	private Button btnLeftAction; // cancel - retake
	private ImageView ivImagePreview;
	private TextView tvMessage;
	private ProgressBar pbUploading;

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	private CameraAttachmentConfig mConfig;

	private Bitmap mBitmap;

	private boolean previewing = true;

	private CameraAttachmentCallback mCallback;

	public CameraAttachmentDialog(Context context, int theme,
			CameraAttachmentCallback callback) {
		super(context, theme);
		mContext = context;
		mCallback = callback;
		setCancelable(false);
	}

	public void setConfig(CameraAttachmentConfig config) {
		mConfig = config;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getIdFromProjectsRFile(RESOURCE_TYPE_LAYOUT,
				"camera_attachment_dialog"));
		setWidgets();
		resume();
	}

	public void resume() {
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}
		setupAutoFocus();
		mPreview = new CameraPreview(mContext, mCamera, this, autoFocusCB);
		flPreview.addView(mPreview);
		startPreview();
	}

	public void pause() {
		releaseCamera();
		flPreview.removeView(mPreview);
	}

	protected void setWidgets() {
		flPreview = (FrameLayout) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "flScannerFragmentPreview"));
		btnTakePicture = (Button) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "btnCameraAttachmentPluginTakePicture"));
		btnLeftAction = (Button) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "btnCameraAttachmentPluginLeftAction"));
		btnUsePhoto = (Button) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "btnCameraAttachmentPluginTakeUsePhoto"));
		ivImagePreview = (ImageView) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "ivCameraAttachmentPluginTakePreview"));
		tvMessage = (TextView) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "tvCameraAttachmentPluginMessage"));
		pbUploading = (ProgressBar) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "pbCameraAttachmentPluginUpLoading"));

		btnLeftAction.setText(mConfig.getCancelButtonText());
		btnUsePhoto.setText(mConfig.getUsePhotoButtonText());

		btnLeftAction.setOnClickListener(this);
		btnTakePicture.setOnClickListener(this);
		btnUsePhoto.setOnClickListener(this);
	}

	/*
	 * Camera helper
	 */

	private void setupAutoFocus() {
		autoFocusHandler = new Handler();
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}
	}

	private void startPreview() {
		mCamera.setPreviewCallback(this);
		mCamera.startPreview();
		previewing = true;
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;

			mPreview.setCamera(null);
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
	}

	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	/*
	 * R.java util
	 */

	private static final String RESOURCE_TYPE_LAYOUT = "layout";
	private static final String RESOURCE_TYPE_ID = "id";
	private String packageName;

	private int getIdFromProjectsRFile(String resourceType, String resourceId) {
		if (packageName == null) {
			try {
				PackageManager pm = mContext.getPackageManager();
				PackageInfo packageInfo = pm.getPackageInfo(
						mContext.getPackageName(), 0);
				packageName = packageInfo.packageName;
			} catch (NameNotFoundException e) {
			}
		}
		Resources resources = mContext.getApplicationContext().getResources();
		return resources.getIdentifier(resourceId, resourceType, packageName);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == getIdFromProjectsRFile(RESOURCE_TYPE_ID,
				"btnCameraAttachmentPluginTakePicture")) {
			takePicture();
		}
		if (v.getId() == getIdFromProjectsRFile(RESOURCE_TYPE_ID,
				"btnCameraAttachmentPluginLeftAction")) {
			if (btnLeftAction.getText().equals(mConfig.getCancelButtonText())) {
				cancelTakingPicture();
			} else if (btnLeftAction.getText().equals(
					mConfig.getRetakeButtonText())) {
				retake();
			}
		}
		if (v.getId() == getIdFromProjectsRFile(RESOURCE_TYPE_ID,
				"btnCameraAttachmentPluginTakeUsePhoto")) {
			showUploadingUI();
			upload();
		}
	}

	private void takePicture() {
		mCamera.takePicture(null, null, mPictureCallback);
		btnLeftAction.setText(mConfig.getRetakeButtonText());
		btnLeftAction.setVisibility(View.VISIBLE);
		btnTakePicture.setVisibility(View.INVISIBLE);
		btnUsePhoto.setVisibility(View.VISIBLE);
	}

	private void cancelTakingPicture() {
		pause();
		mCallback.onCancelled();
	}

	private void retake() {
		btnLeftAction.setText("Cancel");
		btnTakePicture.setVisibility(View.VISIBLE);
		btnUsePhoto.setVisibility(View.INVISIBLE);
		ivImagePreview.setVisibility(View.INVISIBLE);
	}

	private void showUploadingUI() {
		btnLeftAction.setVisibility(View.INVISIBLE);
		btnTakePicture.setVisibility(View.INVISIBLE);
		btnUsePhoto.setVisibility(View.INVISIBLE);
		tvMessage.setVisibility(View.VISIBLE);
		tvMessage.setText("Uploading");
		pbUploading.setVisibility(View.VISIBLE);
	}

	private void upload() {
		UploadImageTask uploadTask = new UploadImageTask(this,
				mConfig.getUploadUrl(), mBitmap);
		uploadTask.execute(new String[] {});
	}

	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
			try {
				FileOutputStream out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/image.jpg");
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();

				mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);

				ivImagePreview.setImageBitmap(mBitmap);
				ivImagePreview.setVisibility(View.VISIBLE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/* UploadImageTaskCallback callback */
	@Override
	public void onUploadCompleted(boolean success, String result) {
		pause();
		mCallback.onUploadedWithResult(result);
	}

	public interface CameraAttachmentCallback {
		public void onCancelled();

		public void onUploadedWithResult(String result);
	}
}

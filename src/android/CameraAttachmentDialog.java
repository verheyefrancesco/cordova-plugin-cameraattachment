package com.checkroom.plugin.cameraattachment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkroom.plugin.cameraattachment.CameraPreview.TheOrientation;
import com.checkroom.plugin.cameraattachment.CameraPreview.TheOrientationCallback;
import com.checkroom.plugin.cameraattachment.UploadImageTask.UploadImageTaskCallback;

public class CameraAttachmentDialog extends Dialog implements PreviewCallback,
		android.view.View.OnClickListener, UploadImageTaskCallback,
		TheOrientationCallback {

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

	private TheOrientation mOrientationAtTakePicture;

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

	private void resume() {
		if (mCamera == null) {
			mCamera = getCameraInstance();
			setCameraParameters();
		}
		setupAutoFocus();
		mPreview = new CameraPreview(mContext, mCamera, this, autoFocusCB);
		mPreview.mOrientationCallback = this;
		flPreview.addView(mPreview);
		startPreview();
	}

	private void stop() {
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

	private void setCameraParameters() {
		if (mCamera != null) {
			setCameraPreviewSize();
			setCameraPictureSize();
		}
	}

	private void setCameraPreviewSize() {
		Camera.Parameters params = mCamera.getParameters();
		boolean support640x480 = false;
		boolean support1280x720 = false;
		List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
		for (int i = 1; i < sizeList.size(); i++) {
			if (sizeList.get(i).width == 1280 && sizeList.get(i).height == 720) {
				support1280x720 = true;
			}
			if (sizeList.get(i).width == 640 && sizeList.get(i).height == 480) {
				support640x480 = true;
			}
		}

		if (mConfig.isPhotoSizeSmall()) {
			if (support640x480) {
				params.setPreviewSize(640, 480);
			}
		} else {
			if (support1280x720) {
				params.setPreviewSize(1280, 720);
			}
		}
		mCamera.setParameters(params);
	}

	private void setCameraPictureSize() {
		if (mConfig.isPhotoSizeLarge()) {
			setLargePictureSize();
		} else if (mConfig.isPhotoSizeMedium()) {
			setMediumPictureSize();
		} else if (mConfig.isPhotoSizeSmall()) {
			setSmalPictureSize();
		}
	}

	private void setLargePictureSize() {
		Camera.Parameters params = mCamera.getParameters();
		double previewRatio = getCameraPreviewRatio(params);
		List<Camera.Size> sizeList = params.getSupportedPictureSizes();
		Size bestSize;
		bestSize = sizeList.get(0);
		for (Size s : sizeList) {
			if (s.width >= bestSize.width) {
				double ratio = (double) s.width / (double) s.height;
				if (ratio == previewRatio) {
					bestSize = s;
				}
			}
		}
		params.setPictureSize(bestSize.width, bestSize.height);
		mCamera.setParameters(params);
	}

	private void setMediumPictureSize() {
		boolean canSetDefaultMedium = canAndDidSet1280x720PictureSize();
		if (!canSetDefaultMedium) {
			// Find another match with preview ratio
			Camera.Parameters params = mCamera.getParameters();
			double previewRatio = getCameraPreviewRatio(params);
			List<Camera.Size> sizeList = params.getSupportedPictureSizes();
			Size bestSize;
			bestSize = sizeList.get(0);
			for (Size s : sizeList) {
				if (s.width >= bestSize.width && s.width > 1200) {
					double ratio = (double) s.width / (double) s.height;
					if (ratio == previewRatio) {
						params.setPictureSize(bestSize.width, bestSize.height);
						mCamera.setParameters(params);
						return;
					}
				}
			}
		}
	}

	private boolean canAndDidSet1280x720PictureSize() {
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizeList = params.getSupportedPictureSizes();
		boolean support1280x720 = doesSizeListSupportSize(sizeList, 1280, 720);
		if (support1280x720) {
			params.setPictureSize(1280, 720);
		}
		mCamera.setParameters(params);
		return support1280x720;
	}

	private double getCameraPreviewRatio(Camera.Parameters params) {
		double previewRatio = (double) params.getPreviewSize().width
				/ (double) params.getPreviewSize().height;
		return previewRatio;
	}

	private void setSmalPictureSize() {
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizeList = params.getSupportedPictureSizes();
		boolean support640x480 = doesSizeListSupportSize(sizeList, 640, 480);
		if (support640x480) {
			params.setPictureSize(640, 480);
		}
		mCamera.setParameters(params);
	}

	private boolean doesSizeListSupportSize(List<Camera.Size> sizeList,
			int width, int height) {
		for (int i = 1; i < sizeList.size(); i++) {
			if (sizeList.get(i).width == width
					&& sizeList.get(i).height == height) {
				return true;
			}
		}
		return false;
	}

	private Camera getCameraInstance() {
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
			mCamera.stopPreview();
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

		mOrientationAtTakePicture = mPreview.mTheOrientation;

		mCamera.takePicture(null, null, mPictureCallback);
		// pause();
		btnLeftAction.setText(mConfig.getRetakeButtonText());
		btnLeftAction.setVisibility(View.VISIBLE);
		btnTakePicture.setVisibility(View.INVISIBLE);
		btnUsePhoto.setVisibility(View.VISIBLE);
	}

	private void cancelTakingPicture() {
		stop();
		mCallback.onCancelled();
	}

	private void retake() {
		btnLeftAction.setText(mConfig.getCancelButtonText());
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
				mConfig.getUploadUrl(), imageBase64, mConfig.getArgBase64(),
				mConfig.getArgFileName(), mConfig.getFileName());
		uploadTask.execute(new String[] {});
	}

	private String imageBase64 = "";
	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mBitmap = null;
			imageBase64 = null;
			ivImagePreview.setImageBitmap(null);

			mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

			int rotation = mPreview.getSurfaceRotation();
			mBitmap = BitmapUtils.rotateBitmap(mBitmap, rotation);

			int width = camera.getParameters().getPreviewSize().width;
			int height = camera.getParameters().getPreviewSize().height;
			Bitmap b = BitmapUtils.resizedBitmap(mBitmap, width, height);

			// b = BitmapUtils.rotateBitmap(b, 90);
			ivImagePreview.setImageBitmap(b);
			ivImagePreview.setVisibility(View.VISIBLE);

			try {
				FileOutputStream out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/image.jpg");
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();

				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);

				byte[] ba = bao.toByteArray();
				int flag = 0; // you can pass the default 0 = Base64.DEFAULT
				imageBase64 = Base64.encodeToString(ba, flag);

				mBitmap = null;
				b = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/* UploadImageTaskCallback callback */
	@Override
	public void onUploadCompleted(boolean success, String result,
			int httpStatusCode) {
		stop();
		mCallback.onUploadedWithResult(result, httpStatusCode);
	}

	public interface CameraAttachmentCallback {
		public void onCancelled();

		public void onUploadedWithResult(String result, int httpStatusCode);
	}

	@Override
	public void onOrientationChanged(TheOrientation orientation) {
		changeImageViewScaleTypForOrientation(orientation);
	}

	private void changeImageViewScaleTypForOrientation(
			TheOrientation orientation) {
		if (mOrientationAtTakePicture == null) {
			return;
		}
		if (mOrientationAtTakePicture == TheOrientation.PORTRAIT) {
			if (orientation == TheOrientation.LANDSCAPE_LEFT
					|| orientation == TheOrientation.LANDSCAPE_RIGHT) {
				ivImagePreview.setScaleType(ScaleType.CENTER_INSIDE);
			} else {
				ivImagePreview.setScaleType(ScaleType.FIT_XY);
			}
		} else {
			if (orientation == TheOrientation.PORTRAIT) {
				ivImagePreview.setScaleType(ScaleType.CENTER_INSIDE);
			} else {
				ivImagePreview.setScaleType(ScaleType.FIT_XY);
			}
		}
	}
}

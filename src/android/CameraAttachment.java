package com.checkroom.plugin.cameraattachment;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class CameraAttachment extends RelativeLayout {

	private Context mContext;
	private RelativeLayout mRootLayout;

	public CameraAttachment(Context context) {
		super(context);
		mContext = context;
		create();
	}

	private void create() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRootLayout = (RelativeLayout) inflater.inflate(
				R.layout.camera_attachment, this, false);
		this.addView(mRootLayout);
	}
}

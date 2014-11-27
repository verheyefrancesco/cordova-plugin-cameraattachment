package com.checkroom.plugin.cameraattachment;

public class CameraAttachmentConfig {

	private String uploadUrl;
	private String cancelButtonText = "Cancel";
	private String usePhotoButtonText = "Use Photo";
	private String retakeButtonText = "Retake";
	private int photoSizeWidth = -1;
	private int photoSizeHeigth = -1;

	public CameraAttachmentConfig(String uploadUrl, String cancelButtonText,
			String usePhotoButtonText, String retakeButtonText,
			int photoSizeWidth, int photoSizeHeight) {
		this.uploadUrl = uploadUrl;
		this.cancelButtonText = cancelButtonText;
		this.usePhotoButtonText = usePhotoButtonText;
		this.retakeButtonText = retakeButtonText;
		this.photoSizeWidth = photoSizeWidth;
		this.photoSizeHeigth = photoSizeHeight;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public String getCancelButtonText() {
		return cancelButtonText;
	}

	public String getUsePhotoButtonText() {
		return usePhotoButtonText;
	}

	public String getRetakeButtonText() {
		return retakeButtonText;
	}

	public int getPhotoSizeWidth() {
		return photoSizeWidth;
	}

	public int getPhotoSizeHeight() {
		return photoSizeHeigth;
	}
}

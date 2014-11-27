package com.checkroom.plugin.cameraattachment;

public class CameraAttachmentConfig {

	private String uploadUrl;
	private String cancelButtonText = "Cancel";
	private String usePhotoButtonText = "Use Photo";
	private String retakeButtonText = "Retake";
	private String photoSize = "medium"; // "small" - "medium" - "large"

	public CameraAttachmentConfig(String uploadUrl, String cancelButtonText,
			String usePhotoButtonText, String retakeButtonText, String photoSize) {
		this.uploadUrl = uploadUrl;
		this.cancelButtonText = cancelButtonText;
		this.usePhotoButtonText = usePhotoButtonText;
		this.retakeButtonText = retakeButtonText;
		this.photoSize = photoSize;
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

	public String getPhotoSize() {
		return photoSize;
	}

	public boolean isPhotoSizeLarge() {
		return photoSize.equals("large");
	}

	public boolean isPhotoSizeMedium() {
		return photoSize.equals("medium");
	}

	public boolean isPhotoSizeSmall() {
		return photoSize.equals("small");
	}
}

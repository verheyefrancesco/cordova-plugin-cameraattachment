package com.checkroom.plugin.cameraattachment;

public class CameraAttachmentConfig {

	private String uploadUrl;
	private String cancelButtonText = "Cancel";
	private String usePhotoButtonText = "Use Photo";
	private String retakeButtonText = "Retake";
	private String photoSize = "medium"; // "small" - "medium" - "large"
	private String argBase64 = "fileContents";
	private String argFileName;
	private String fileName;

	public CameraAttachmentConfig(String uploadUrl, String cancelButtonText,
			String usePhotoButtonText, String retakeButtonText,
			String photoSize, String argBase64, String argFileName, String fileName) {
		this.uploadUrl = uploadUrl;
		this.cancelButtonText = cancelButtonText;
		this.usePhotoButtonText = usePhotoButtonText;
		this.retakeButtonText = retakeButtonText;
		this.photoSize = photoSize;
		this.argBase64 = argBase64;
		this.argFileName = argFileName;
		this.fileName = fileName;
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

	public String getArgBase64() {
		return argBase64;
	}

	public String getArgFileName() {
		return argFileName;
	}
	
	public String getFileName(){
		return fileName;
	}
}

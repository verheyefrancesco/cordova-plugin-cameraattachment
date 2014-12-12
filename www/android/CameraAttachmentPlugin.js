
/**
 * Constructor
 */
function CameraAttachmentPlugin() {
}

CameraAttachmentPlugin.prototype.show = function(options, cb) {
  
	var defaults = {
        uploadUrl : 'your_upload_url',
        cancelButtonText : 'Cancel',
        usePhotoButtonText : 'Use Photo',
        retakeButtonText : 'Retake',
        photoSize: 'medium',
        argBase64: 'fileContents',
        argFileName: '',
        fileName: ''
    };

	for (var key in defaults) {
		if (typeof options[key] !== "undefined") {
			defaults[key] = options[key];
		}
	}

	var callback = function(json) {
		cb(JSON.parse(json));
	};
  
	cordova.exec(callback, 
		null, 
		"CameraAttachmentPlugin", 
		"show",
		[defaults]);
};

var cameraAttachment = new CameraAttachmentPlugin();
module.exports = cameraAttachment;

// Make plugin work under window.plugins
if (!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.cameraAttachment) {
    window.plugins.cameraAttachment = cameraAttachment;
}
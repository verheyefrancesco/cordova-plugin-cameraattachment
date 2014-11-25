/**
 * Phonegap DatePicker Plugin Copyright (c) Greg Allen 2011 MIT Licensed
 * Reused and ported to Android plugin by Daniel van 't Oever
 */

/**
 * Constructor
 */
function CameraAttachment() {
  //this._callback;
}

/**
 * show - true to show the ad, false to hide the ad
 */
CameraAttachment.prototype.show = function(options, cb) {
  
	var defaults = {
        uploadUrl : 'your_upload_url'
    };

	for (var key in defaults) {
		if (typeof options[key] !== "undefined") {
			defaults[key] = options[key];
		}
	}

	//this._callback = cb;

	var callback = function(message) {
		if(isNaN(message) == false) {
			cb(message);
		}
	}
  
	cordova.exec(callback, 
		null, 
		"CameraAttachmentPlugin", 
		defaults.mode,
		[defaults]
	);
};

var cameraAttachment = new CameraAttachment();
module.exports = cameraAttachment;

// Make plugin work under window.plugins
if (!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.cameraAttachment) {
    window.plugins.cameraAttachment = cameraAttachment;
}
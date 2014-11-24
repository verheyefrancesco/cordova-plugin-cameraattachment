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
        mode : 'date',
        date : -1,
        minDate: -1,
        maxDate: -1,
        minuteInterval:1,
        positiveButtonText: 'Set',
        negativeButtonText: 'Cancel',
        setDateTitle: 'Set date',
        setTimeTitle: 'Set time'
    };

	for (var key in defaults) {
		if (typeof options[key] !== "undefined") {
			defaults[key] = options[key];
		}
	}

	//this._callback = cb;

	var callback = function(message) {
		var timestamp = Date.parse(message);
		if(isNaN(timestamp) == false) {
			cb(new Date(message));
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
/**
  Phonegap CameraAttachment Plugin
  https://github.com/francescobitmunks/cordova-plugin-cameraattachment

  MIT Licensed
*/

var exec = require('cordova/exec');
/**
 * Constructor
 */
function CameraAttachmentPlugin() {
    this._callback;
}

/**
 * show - true to show the ad, false to hide the ad
 */
CameraAttachmentPlugin.prototype.show = function(options, cb) {
    var padDate = function(date) {
      if (date.length == 1) {
        return ("0" + date);
      }
      return date;
    };

    var defaults = {
        mode : 'date',
        date : -1,
        minDate: -1,
        maxDate: -1,
        minuteInterval:1,
        positiveButtonText: 'Set',
        negativeButtonText: 'Cancel',
        setDateTitle: 'Set date',
        setTimeTitle: 'Set time',
        x: '0',
        y: '0',
    };

    for (var key in defaults) {
        if (typeof options[key] !== "undefined")
            defaults[key] = options[key];
    }
    this._callback = cb;

    exec(null, 
      null, 
      "CameraAttachmentPlugin", 
      "show",
      [defaults]
    );
};

CameraAttachmentPlugin.prototype._photoUploaded = function(result) {
    var d = new Date(parseFloat(date) * 1000);
    if (this._callback)
        this._callback(d);
}

CameraAttachmentPlugin.prototype._photoUploadedCanceled = function() {
    if (this._callback)
        this._callback();
}



var cameraAttachmentPlugin = new CameraAttachmentPlugin();
module.exports = cameraAttachmentPlugin;

// Make plugin work under window.plugins
if (!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.cameraAttachmentPlugin) {
    window.plugins.cameraAttachmentPlugin = cameraAttachmentPlugin;
}

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

    var defaults = {
        uploadUrl : 'your_upload_url'
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

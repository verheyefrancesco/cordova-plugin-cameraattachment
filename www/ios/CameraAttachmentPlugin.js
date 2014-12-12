var exec = require('cordova/exec');
/**
 * Constructor
 */
function CameraAttachmentPlugin() {
    this._callback;
}

CameraAttachmentPlugin.prototype.show = function(options, cb) {

    var defaults = {
        uploadUrl : 'your_upload_url',
        cancelButtonText : 'Cancel',
        usePhotoButtonText : 'Use Photo',
        retakeButtonText : 'Retake',
        photoSize: "medium",
        argBase64: 'fileContents',
        argFileName: 'fileName'
    };

    for (var key in defaults) {
        if (typeof options[key] !== "undefined"){
          defaults[key] = options[key];
        }
    }
    this._callback = cb;

    exec(null, 
      null, 
      "CameraAttachmentPlugin", 
      "show",
      [defaults]);
};

CameraAttachmentPlugin.prototype._photoUploaded = function(json) {
    json = json.replace(/&#34;/g, '"');
    if (this._callback){
      this._callback({status:'success', data: JSON.parse(json)});
    }
};

CameraAttachmentPlugin.prototype._photoUploadCanceled = function() {
    if (this._callback){
      this._callback({status: 'cancelled'});
    }
};

var cameraAttachmentPlugin = new CameraAttachmentPlugin();
module.exports = cameraAttachmentPlugin;

// Make plugin work under window.plugins
if (!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.cameraAttachmentPlugin) {
    window.plugins.cameraAttachmentPlugin = cameraAttachmentPlugin;
}
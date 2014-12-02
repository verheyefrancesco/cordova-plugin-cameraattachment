# CameraAttachment Plugin for Cordova/PhoneGap 3.0 (iOS and Android)


## Installation

1) Make sure that you have [Node](http://nodejs.org/) and [Cordova CLI](https://github.com/apache/cordova-cli) or [PhoneGap's CLI](https://github.com/mwbrooks/phonegap-cli) installed on your machine.

2) Add a plugin to your project using Cordova CLI:

```bash
cordova plugin add https://github.com/francescobitmunks/cordova-plugin-cameraattachment
```
Or using PhoneGap CLI:

```bash
phonegap local plugin add https://github.com/francescobitmunks/cordova-plugin-cameraattachment
```

## Usage

```js
var uploadUrl = 'http://10.0.1.4:8500/upload/upload';
var cancelButtonText = 'Annuleer';
var usePhotoButtonText = 'Gebruik';
var retakeButtonText = 'Opnieuw';
var photoSize = 'medium';
var argBase64 = 'fileContents';
var argFileName = 'fileName';
var fileName = 'demo_image.jpg';
            
var options = {
    uploadUrl: uploadUrl,
    cancelButtonText: cancelButtonText,
    usePhotoButtonText: usePhotoButtonText,
    retakeButtonText: retakeButtonText,
    photoSize: photoSize,
    argBase64: argBase64,
    argFileName: argFileName,
    fileName: fileName
};

cameraAttachmentPlugin.show(options, 
    function(result){
        alert("upload result: " + result);  
});
```

## Options

### uploadUrl - iOS, Android
The url to upload the base64 image. (HTTPPOST)

Type: String

Default: `your_upload_url`

### cancelButtonText - Android
Label for cancel button.

Type: String

Default: `Cancel`

### usePhotoButtonText - Android
Label to confirm to use the photo.

Type: String

Default: `Use Photo`

### retakeButtonText - Android
Label to retake a picture button.

Type: String

Default: `Retake`

### photoSize - iOS, Android
Size of the picture

Type: String

Default: `medium`

Values: `small` | `medium` | `large`

### argBase64 - iOS, Android
Arg for the base64 parameter in the POST

Type: String

Default: `fileContents`

### argFileName - iOS, Android
Arg for the fileName parameter in the POST (omit if empty)

Type: String

Default: ``

### fileName - iOS, Android
The fileName parameter value in the POST

Type: String

Default: ``

## Requirements
- PhoneGap 3.0 or newer / Cordova 3.0 or newer
- Android 2.3.1 or newer / iOS 5 or newer

## Example

```js
var uploadUrl = 'http://10.0.1.4:8500/upload/upload';
var cancelButtonText = 'Annuleer';
var usePhotoButtonText = 'Gebruik';
var retakeButtonText = 'Opnieuw';
var photoSize = 'medium';
var argBase64 = 'fileContents';
var argFileName = 'fileName';
var fileName = 'demo_image.jpg';
            
var options = {
    uploadUrl: uploadUrl,
    cancelButtonText: cancelButtonText,
    usePhotoButtonText: usePhotoButtonText,
    retakeButtonText: retakeButtonText,
    photoSize: photoSize,
    argBase64: argBase64,
    argFileName: argFileName,
    fileName: fileName
};

cameraAttachmentPlugin.show(options, 
    function(result){
        alert("upload result: " + result);  
});
```
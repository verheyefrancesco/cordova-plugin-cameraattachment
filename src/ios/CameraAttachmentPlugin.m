//
//  CameraAttachmentPlugin.m
//  CameraAttachmentPlugin
//
//  Created by Francesco Verheye on 21/11/14.
//  Copyright (c) 2014 Cheqroom. All rights reserved.
//

#import "CameraAttachmentPlugin.h"

@implementation CameraAttachmentPlugin{
    CameraAttachmentViewController *_cameraAttachmentViewController;
    NSString *_uploadUrl;
}

- (void)show:(CDVInvokedUrlCommand*)command
{
    NSMutableDictionary *options = [command argumentAtIndex:0];
    
    [self readPamaretersFromOptions:options];
    
    [self showCameraAttachmentViewController];
}

-(void) readPamaretersFromOptions:(NSMutableDictionary*)options
{
    if([options objectForKey:@"uploadUrl"])
    {
        _uploadUrl = [options objectForKey:@"uploadUrl"];
    }
    
    /*
    // B-OFFICE
    _uploadUrl = @"http://10.0.1.31:8500/upload/upload";
    // C-OFFICE
    _uploadUrl = @"http://192.168.9.108/upload/upload";
     */
}

-(void) showCameraAttachmentViewController
{
    _cameraAttachmentViewController = [[CameraAttachmentViewController alloc] init];
    _cameraAttachmentViewController.delegate = self;
    _cameraAttachmentViewController.uploadUrl = _uploadUrl;
    [self.viewController presentViewController:_cameraAttachmentViewController animated:YES completion:nil];
}

#pragma mark - CameraAttachmentViewControllerDelegate
-(void) cameraAttachmentVC:(CameraAttachmentViewController *)cameraAttachmentVC onClosed:(BOOL)fromCancel andUploadResult:(NSString *)uploadResult
{
    if(fromCancel)
    {
        [self jsUploadCancelled];
    }
    else
    {
        [self jsUploadWithResult:uploadResult];
    }
    [_cameraAttachmentViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - JS API
-(void) jsUploadCancelled
{
    NSString* jsCallback = @"datePicker._photoUploadedCanceled();";
    [self.commandDelegate evalJs:jsCallback];
}
- (void)jsUploadWithResult:(NSString*)result
{
    NSString* jsCallback = [NSString stringWithFormat:@"datePicker._photoUploaded(\"%@\");", result];
    //NSLog(jsCallback);
    //[super writeJavascript:jsCallback];
    [self.commandDelegate evalJs:jsCallback];
}

@end

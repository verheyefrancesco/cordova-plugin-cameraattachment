//
//  CameraAttachmentPlugin.m
//  CameraAttachmentPlugin
//
//  Created by Francesco Verheye on 21/11/14.
//  Copyright (c) 2014 Cheqroom. All rights reserved.
//

#import "CameraAttachmentPlugin.h"
#import "CameraAttachmentConfig.h"

@implementation CameraAttachmentPlugin{
    CameraAttachmentViewController *_cameraAttachmentViewController;
    CameraAttachmentConfig *_config;
}

- (void)show:(CDVInvokedUrlCommand*)command
{
    NSMutableDictionary *options = [command argumentAtIndex:0];
    
    [self createConfigWithOptions:options];
    
    [self showCameraAttachmentViewController];
}

-(void) createConfigWithOptions:(NSMutableDictionary*)options
{
    _config = [[CameraAttachmentConfig alloc] initWithDictionary:options];
}

-(void) showCameraAttachmentViewController
{
    _cameraAttachmentViewController = [[CameraAttachmentViewController alloc] initWithConfig:_config];
    _cameraAttachmentViewController.delegate = self;
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

-(void) cameraAttachmentVC:(CameraAttachmentViewController *)cameraAttachmentVC uploadFailedWithError:(NSString *)errorMessage
{
    [self jsUploadFailedWithError:errorMessage];
    [_cameraAttachmentViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - JS API
-(void) jsUploadCancelled
{
    NSString* jsCallback = @"cameraAttachmentPlugin._photoUploadCanceled();";
    [self.commandDelegate evalJs:jsCallback];
}

- (void)jsUploadWithResult:(NSString*)result
{
    result = [result stringByReplacingOccurrencesOfString:@"\"" withString:@"&#34;"];
    NSString* jsCallback = [NSString stringWithFormat:@"cameraAttachmentPlugin._photoUploaded(\"%@\");", result];
    [self.commandDelegate evalJs:jsCallback];
}

-(void) jsUploadFailedWithError:(NSString*)errorMessage
{
    NSMutableDictionary *resultDic = [NSMutableDictionary dictionary];
    [resultDic setValue:errorMessage forKey:@"message"];
    
    NSError * err;
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:resultDic options:0 error:&err];
    NSString * result = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    result = [result stringByReplacingOccurrencesOfString:@"\"" withString:@"&#34;"];
    
    NSString* jsCallback = [NSString stringWithFormat:@"cameraAttachmentPlugin._photoUploadedError(\"%@\");", result];
    [self.commandDelegate evalJs:jsCallback];
}

@end

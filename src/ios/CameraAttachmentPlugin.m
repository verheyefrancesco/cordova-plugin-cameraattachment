//
//  CameraAttachmentPlugin.m
//  CameraAttachmentPlugin
//
//  Created by Francesco Verheye on 21/11/14.
//  Copyright (c) 2014 Cheqroom. All rights reserved.
//

#import "CameraAttachmentPlugin.h"
#import "CameraAttachmentViewController.h"

@implementation CameraAttachmentPlugin{
    CameraAttachmentViewController *_cameraAttachmentViewController;
}


- (void)show:(CDVInvokedUrlCommand*)command
{
    NSMutableDictionary *options = [command argumentAtIndex:0];
    
    _cameraAttachmentViewController = [[CameraAttachmentViewController alloc] init];
    [self.viewController presentViewController:_cameraAttachmentViewController animated:YES completion:nil];
}

@end

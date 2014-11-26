//
//  CameraAttachmentViewController.m
//  CameraAttachmentPlugin
//
//  Created by Francesco Verheye on 21/11/14.
//  Copyright (c) 2014 Cheqroom. All rights reserved.
//

#import "CameraAttachmentViewController.h"

@interface CameraAttachmentViewController ()

@end

@implementation CameraAttachmentViewController{
    UIImagePickerController *_pictureViewController;
    PhotoUploader *_uploader;
    CameraAttachmentConfig *_config;
}

-(instancetype) initWithConfig:(CameraAttachmentConfig*)config
{
    self = [super init];
    if(self)
    {
        _config = config;
        
        //_config.photoSizeWidth = -1;
        //_config.photoSizeHeight = -1;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    if(!_pictureViewController)
    {
        [self showTakePictureViewController];
    }
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) showTakePictureViewController
{
    _pictureViewController = [[UIImagePickerController alloc] init];
    _pictureViewController.delegate = self;
    _pictureViewController.sourceType = UIImagePickerControllerSourceTypeCamera;
    
    [self presentViewController:_pictureViewController animated:NO completion:NULL];
}

#pragma mark - Image Picker Controller delegate methods
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    UIImage *image = info[UIImagePickerControllerOriginalImage];
    self.imageView.image = image;
    self.containerView.hidden = NO;
    [self.activityIndicator startAnimating];
    self.messageLabel.text = _config.uploadingMessage;
    
    [picker dismissViewControllerAnimated:NO completion:NULL];
    
    [self uploadImage:image];
}

-(void) imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissViewControllerAnimated:NO completion:nil];
    if(self.delegate)
    {
        [self.delegate cameraAttachmentVC:self onClosed:YES andUploadResult:@""];
    }
}

-(void) uploadImage:(UIImage*)image
{
    _uploader = [[PhotoUploader alloc] init];
    _uploader.delegate = self;
    
    if(_config.photoSizeHeight != -1 && _config.photoSizeWidth != -1)
    {
        UIImageOrientation orient = image.imageOrientation;
        
        if(orient == UIImageOrientationUp || orient == UIImageOrientationUpMirrored || orient == UIImageOrientationDown || orient == UIImageOrientationDownMirrored)
        {
            [_uploader uploadImage:image andImageWidth:_config.photoSizeWidth andImageHeight:_config.photoSizeHeight toUrl:_config.uploadUrl];
        } else
        {
            [_uploader uploadImage:image andImageWidth:_config.photoSizeHeight andImageHeight:_config.photoSizeWidth toUrl:_config.uploadUrl];
        }
    } else
    {
        [_uploader uploadImage:image toUrl:_config.uploadUrl];
    }
}

#pragma mark - PhotoUploaderDelegate
-(void) photoUploader:(PhotoUploader *)photoUploader didUploadWithResult:(NSString *)result andSuccess:(BOOL)success
{
    if(self.delegate)
    {
        [self.delegate cameraAttachmentVC:self onClosed:NO andUploadResult:result];
    }
}

@end

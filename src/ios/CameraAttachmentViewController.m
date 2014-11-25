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
    self.messageLabel.text = @"Uploading";
    
    [picker dismissViewControllerAnimated:NO completion:NULL];
    
    [self uploadImage:image];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    if(self.delegate)
    {
        [self.delegate cameraAttachmentVC:self onClosed:YES andUploadResult:nil];
    }
}

-(void) uploadImage:(UIImage*)image
{
    _uploader = [[PhotoUploader alloc] init];
    _uploader.delegate = self;
    [_uploader uploadImage:image andImageWidth:1024 andImageHeight:720 toUrl:self.uploadUrl];
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

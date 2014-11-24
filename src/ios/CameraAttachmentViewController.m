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
    UIImagePickerController *pictureViewController;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    if(!pictureViewController)
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
    pictureViewController = [[UIImagePickerController alloc] init];
    pictureViewController.delegate = self;
    pictureViewController.sourceType = UIImagePickerControllerSourceTypeCamera;
    
    [self presentViewController:pictureViewController animated:NO completion:NULL];
}

#pragma mark - Image Picker Controller delegate methods

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    UIImage *image = info[UIImagePickerControllerOriginalImage];
    
    self.containerView.hidden = NO;
    [self.activityIndicator startAnimating];
    self.messageLabel.text = @"Uploading";
    self.imageView.image = image;
    
    [self uploadImage:image];
    
    [picker dismissViewControllerAnimated:NO completion:NULL];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissViewControllerAnimated:YES completion:NULL];
}

-(void) uploadImage:(UIImage*) image
{
    
}



@end

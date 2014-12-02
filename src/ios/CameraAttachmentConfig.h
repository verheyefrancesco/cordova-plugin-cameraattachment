//
//  CameraAttachmentConfig.h
//  HelloWorld
//
//  Created by Francesco Verheye on 26/11/14.
//
//

#import <Foundation/Foundation.h>

@interface CameraAttachmentConfig : NSObject

@property(nonatomic) NSString* uploadUrl;
@property(nonatomic) NSString* cancelButtonText;
@property(nonatomic) NSString* usePhotoButtonText;
@property(nonatomic) NSString* retakeButtonText;
@property(nonatomic) NSString* uploadingMessage;
@property(nonatomic) NSString* photoSize;
@property(nonatomic) NSString* argBase64;
@property(nonatomic) NSString* argFileName;


-(instancetype) initWithDictionary:(NSMutableDictionary*)dict;
@end

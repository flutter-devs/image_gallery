#import "FlutterGallaryPlugin.h"
#import <image_gallery/image_gallery-Swift.h>

@implementation FlutterGallaryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterGallaryPlugin registerWithRegistrar:registrar];
}
@end

#import "FlutterGallaryPlugin.h"
#import <flutter_gallary_plugin/flutter_gallary_plugin-Swift.h>

@implementation FlutterGallaryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterGallaryPlugin registerWithRegistrar:registrar];
}
@end

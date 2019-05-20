import Flutter
import UIKit
import Photos

public class SwiftFlutterGallaryPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "image_gallery", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterGallaryPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if (call.method == "getPlatformVersion") {
            result("iOS " + UIDevice.current.systemVersion)
        }
        else if (call.method == "getAllImages") {
            
            DispatchQueue.main.async {
                
                let imgManager = PHImageManager.default()
                let requestOptions = PHImageRequestOptions()
                requestOptions.isSynchronous = true
                let fetchOptions = PHFetchOptions()
                fetchOptions.sortDescriptors = [NSSortDescriptor(key:"creationDate", ascending: true)]
                
                fetchOptions.predicate = NSPredicate(format: "mediaType = %d || mediaType = %d", PHAssetMediaType.image.rawValue, PHAssetMediaType.video.rawValue)
                
                let fetchResult = PHAsset.fetchAssets(with: fetchOptions)
                var allImages = [String]()
                var allAssetsDict = [NSDictionary]()
                
                var totalItration = 0
                print("fetchResult.count : \(fetchResult.count)")
                
                var savedLocalIdentifiers = [String]()
                
                for index in 0..<fetchResult.count
                {
                    let assetDetailsDict: NSMutableDictionary = [:]
                    let asset = fetchResult.object(at: index) as PHAsset
                    let localIdentifier = asset.localIdentifier
                    savedLocalIdentifiers.append(localIdentifier)
                    
                    imgManager.requestImage(for: asset, targetSize: CGSize(width: 512.0, height: 512.0), contentMode: PHImageContentMode.aspectFit, options: PHImageRequestOptions(), resultHandler:{(image, info) in
                        
                        asset.getURL(completionHandler: { (url, type) in
                            
                            assetDetailsDict.setValue(url?.path, forKey: "actulaPath")
                            
                            if image != nil {
                                var imageData: Data?
                                if let cgImage = image!.cgImage, cgImage.renderingIntent == .defaultIntent {
                                    imageData = UIImageJPEGRepresentation(image!, 0.8)
                                }
                                else {
                                    imageData = UIImagePNGRepresentation(image!)
                                }
                                let guid = ProcessInfo.processInfo.globallyUniqueString;
                                let tmpFile = String(format: "image_picker_%@.jpg", guid);
                                let tmpDirectory = NSTemporaryDirectory();
                                let tmpPath = (tmpDirectory as NSString).appendingPathComponent(tmpFile);
                                if(FileManager.default.createFile(atPath: tmpPath, contents: imageData, attributes: [:])) {
                                    allImages.append(tmpPath)
                                    
                                    assetDetailsDict.setValue(tmpPath, forKey: "thumbailPath")
                                    assetDetailsDict.setValue(type, forKey: "typeOfMedia")
                                    allAssetsDict.append(assetDetailsDict)
                                }
                            }
                            totalItration += 1
                            if totalItration == (fetchResult.count) {
                                result(allAssetsDict)
                                //result(allImages)
                            }
                        })
                        
                        
                    })
                }
            }
        }
    }
}

extension PHAsset {
    
    func getURL(completionHandler : @escaping ((_ responseURL : URL?, _ typeOfMedia : String ) -> Void)){
        if self.mediaType == .image {
            let options: PHContentEditingInputRequestOptions = PHContentEditingInputRequestOptions()
            options.canHandleAdjustmentData = {(adjustmeta: PHAdjustmentData) -> Bool in
                return true
            }
            self.requestContentEditingInput(with: options, completionHandler: {(contentEditingInput: PHContentEditingInput?, info: [AnyHashable : Any]) -> Void in
                completionHandler(contentEditingInput!.fullSizeImageURL as URL?, "image")
            })
        } else if self.mediaType == .video {
            let options: PHVideoRequestOptions = PHVideoRequestOptions()
            options.version = .original
            PHImageManager.default().requestAVAsset(forVideo: self, options: options, resultHandler: {(asset: AVAsset?, audioMix: AVAudioMix?, info: [AnyHashable : Any]?) -> Void in
                if let urlAsset = asset as? AVURLAsset {
                    let localVideoUrl: URL = urlAsset.url as URL
                    completionHandler(localVideoUrl, "video")
                } else {
                    completionHandler(nil, "")
                }
            })
        }
    }
}

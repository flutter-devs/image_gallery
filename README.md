For help getting started with Flutter, view our online
[documentation](https://flutter.io/).


[![Pub](https://img.shields.io/badge/Pub-0.1.1-orange.svg?style=flat-square)](https://pub.dartlang.org/packages/image_gallery)


# image_gallery

Flutter plugin to showing all the images from the storage in Android and iOS .

## Usage


```dart
 Future<void> loadImageList() async {
    List allImageTemp;
      allImageTemp = await FlutterGallaryPlugin.getAllImages;


    setState(() {
      this.allImage = allImageTemp;
    });
  }


```

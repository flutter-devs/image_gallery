import 'package:flutter/material.dart';
import 'dart:io';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:image_gallery/image_gallery.dart';

void main() => runApp(
    new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Object> allImage = new List();

  @override
  void initState() {
    super.initState();
    loadImageList();
  }

  Future<void> loadImageList() async {
    List allImageTemp;
      allImageTemp = await FlutterGallaryPlugin.getAllImages;


    setState(() {
      this.allImage = allImageTemp;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      debugShowCheckedModeBanner: false,
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Image Gallery'),
        ),
        body: _buildGrid()
        ,
      ),
    );
  }

  Widget _buildGrid() {
    return GridView.extent(
        maxCrossAxisExtent: 150.0,
        // padding: const EdgeInsets.all(4.0),
        mainAxisSpacing: 4.0,
        crossAxisSpacing: 4.0,
        children: _buildGridTileList(allImage.length));
  }

  List<Container> _buildGridTileList(int count) {

    return List<Container>.generate(
    count,
    (int index) =>
    Container(child: Image.file(File(allImage[index].toString()),
    width: 96.0,
    height: 96.0,
    fit: BoxFit.contain,)));
  }





}

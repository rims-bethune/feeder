import 'package:flutter/material.dart';
import 'news.dart';


class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(title: 'Feeder', home: News());
  }
}

void main() => runApp(MyApp());

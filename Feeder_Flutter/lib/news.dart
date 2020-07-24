import 'package:flutter/material.dart';
import 'package:webfeed/webfeed.dart';
import 'package:http/http.dart' as http;
import 'package:qrscan/qrscan.dart' as scanner;
import 'package:url_launcher/url_launcher.dart';
import 'settings.dart';

class News extends StatefulWidget {
  @override
  NewsState createState() => new NewsState();
}

class NewsState extends State<News> {
  Future<RssFeed> news;
  Future<RssFeed> getRss() async {
    final response = await http.get(await SharedPreferencesHelper.getUrl());
    return new RssFeed.parse(response.body);
  }

  Future<RssFeed> getRssFromQrCode() async {
    String photoScanResult = await scanner.scan();
    SharedPreferencesHelper.setUrl(photoScanResult);
    final response = await http.get(photoScanResult);
    return new RssFeed.parse(response.body);
  }

  @override
  void initState() {
    super.initState();
    news = getRss();
  }

  Widget createListView(BuildContext context, AsyncSnapshot snapshot) {
    List<RssItem> items = snapshot.data.items;
    return new ListView.builder(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        return new Column(
          children: <Widget>[
            new ListTile(
              title: new Text(items.elementAt(index).title),
              subtitle: new Text(items.elementAt(index).description),
              onTap: (){
                launch(items.elementAt(index).link);
              },
            )
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("Feeder"),
          actions: <Widget>[
            // action button
            IconButton(
              icon: Icon(Icons.settings),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => Settings()),
                );
              },
            ),

          ],

        ),
        body: FutureBuilder<RssFeed>(
          future: news,
          builder: (BuildContext context, AsyncSnapshot snapshot) {
            switch (snapshot.connectionState) {
              case ConnectionState.none:
              case ConnectionState.waiting:
                return new Text('loading...');
              default:
                if (snapshot.hasError)
                  return new Text('Error: ${snapshot.error}');
                else
                  return createListView(context, snapshot);
            }
          },
        ),
        floatingActionButton:
        Column(mainAxisAlignment: MainAxisAlignment.end, children: [
          FloatingActionButton(
            heroTag: "scan_btn",
            tooltip: 'Scan',
            child: Icon(Icons.crop_free),
            onPressed: () {
              setState(() {
                news = getRssFromQrCode();
              });
            },
          ),
          SizedBox(
            height: 10,
          ),FloatingActionButton(
            heroTag: "reload_btn",
            tooltip: 'Reload',
            child: Icon(Icons.refresh),
            onPressed: () {
              setState(() {
                news = getRss();
              });
            },
          )
        ]));
  }
}

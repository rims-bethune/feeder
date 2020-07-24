import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Settings extends StatefulWidget {
  @override
  SettingsState createState() => new SettingsState();
}

class SettingsState extends  State<Settings> {
  final _formKey = GlobalKey<FormState>();
  final myController = TextEditingController();

  @override
  void initState() {
    super.initState();
    loadSettings();
  }

  //todo : don't use async within a initState
  void loadSettings() async {
    setState(() async {
      myController.text = await SharedPreferencesHelper.getUrl();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Settings"),
      ),
      body: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Form(
              key: _formKey,
              child: Column(
                  children: <Widget>[

                    TextFormField(
                      controller: myController,
                    ),

                    RaisedButton(
                      onPressed: () {
                        // Validate returns true if the form is valid, otherwise false.
                        if (_formKey.currentState.validate()) {
                          SharedPreferencesHelper.setUrl(myController.text);
                          Navigator.pop(context);
                        }
                      },
                      child: Text('Save'),
                    )
                  ]
              )
          )
      ),
    );
  }
}


class SharedPreferencesHelper {

  static Future<String> getUrl() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString("url") ?? 'https://www.lemonde.fr/rss/une.xml';
  }

  static Future<bool> setUrl(String value) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.setString("url", value);
  }
}

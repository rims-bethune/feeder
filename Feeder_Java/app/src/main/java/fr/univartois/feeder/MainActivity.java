package fr.univartois.feeder;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton load_btn = findViewById(R.id.load_btn);
        load_btn.setOnClickListener(view -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url = preferences.getString("url", "https://www.lemonde.fr/rss/une.xml");
            Downloader downloader = new Downloader(url);
            downloader.start();
        });

        ListView listView = findViewById(R.id.load_list);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            RssItem item = (RssItem) adapterView.getItemAtPosition(position);
            Uri uri = Uri.parse(item.link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Downloader downloader = new Downloader(result.getContents());
                downloader.start();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void scan(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }


    class Downloader extends Thread {
        private String url;

        Downloader(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                final List<RssItem> news = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_GRANTED) {
                    InputStream stream = new URL(this.url).openConnection().getInputStream();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(stream, null);
                    int eventType = parser.getEventType();
                    boolean done = false;
                    RssItem item = null;
                    while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                        String name = null;
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.START_TAG:
                                name = parser.getName();
                                if (name.equalsIgnoreCase("item")) {
                                    item = new RssItem();
                                } else if (item != null) {
                                    if (name.equalsIgnoreCase("link")) {
                                        item.link = parser.nextText();
                                    } else if (name.equalsIgnoreCase("description")) {
                                        item.description = parser.nextText().trim();
                                    } else if (name.equalsIgnoreCase("pubDate")) {
                                        item.pubDate = parser.nextText();
                                    } else if (name.equalsIgnoreCase("title")) {
                                        item.title = parser.nextText().trim();
                                    }
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                name = parser.getName();
                                if (name.equalsIgnoreCase("item") && item != null) {
                                    news.add(item);
                                } else if (name.equalsIgnoreCase("channel")) {
                                    done = true;
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                }


                final ListView listView = findViewById(R.id.load_list);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        RssItemAdapter adapter = new RssItemAdapter(
                                getApplicationContext(),
                                news);
                        listView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "Loading news from " + url + ".. done", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "UnknownHostException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
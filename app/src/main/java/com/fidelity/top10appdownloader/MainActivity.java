package com.fidelity.top10appdownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedUrlCache = "INALIDATED";
    private final String STATE_FEED_URL = "current_url";
    private final String STATE_FEED_LIMIT = "current_feedLimit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_FEED_URL);
            feedLimit = savedInstanceState.getInt(STATE_FEED_LIMIT);
        }
        Log.d(TAG, "onCreate: Started AsyncTask");
        listApps = findViewById(R.id.xmlListView);
        downloadUrl(String.format(feedUrl, feedLimit));
        Log.d(TAG, "onCreate: Done");

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_FEED_URL, feedUrl);
        outState.putInt(STATE_FEED_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        if(feedLimit == 10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else{
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int meniId = item.getItemId();


        switch(meniId){
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: FeedLimit for "+item.getTitle() +" has been changed to "+ feedLimit);
                }else{
                    Log.d(TAG, "onOptionsItemSelected: FeedLimit for "+ item.getTitle()+ " remains the same");
                }
                break;
            case R.id.mnuReset:
                feedUrlCache = "Nonsense";
                break;
            default:
                return  super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    private void downloadUrl(String feedUrl) {
        if(!feedUrl.equals(feedUrlCache)){
        Log.d(TAG, "downloadUrl: start");
        DownloadFilesTask downloadFilesTask = new DownloadFilesTask();
        downloadFilesTask.execute(feedUrl);
        feedUrlCache = feedUrl;
        Log.d(TAG, "downloadUrl: end");
        }else{
            Log.d(TAG, "downloadUrl: feed URL unchanged");
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadFilesTask";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with string " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error Downloading the XML");
            }
            return rssFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplication parseApplication = new ParseApplication();
            parseApplication.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplication.getApplication()
//            );
            FeedAdapter arrayAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record,parseApplication.getApplication());
            listApps.setAdapter(arrayAdapter);
        }
    }

    private String downloadXML(String urlPath) {
        StringBuilder xmlResult = new StringBuilder();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            Log.d(TAG, "downloadXML: response code was " + responseCode);
//            InputStream inputStream = con.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            int charsRead;
            char[] inputBuffer = new char[500];
            while (true) {
                charsRead = bufferedReader.read(inputBuffer);
                if (charsRead < 0) {
                    break;
                }
                if (charsRead > 0) {
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
            }
            bufferedReader.close();
            return xmlResult.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: Invalid url " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO Exception reading data" + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "downloadXML: Needs permission? " + e.getMessage());
        }
        return null;
    }
}
package com.example.user.surface;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by zhengwei on 2015/12/17.
 */
public class RouteTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = Family_Map.downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        RouteParserTask parserTask = new RouteParserTask();

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }
}


package com.example.user.surface;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by user on 2015/12/17.
 */
public class NearbysearchTask extends AsyncTask<String, Integer, String> {

        String data = null;

// Invoked by execute() method of this object
@Override
protected String doInBackground(String... url) {
        try{
        data = Family_Map.downloadUrl(url[0]);
            Log.v("placerun", "data" + data);
        }catch(Exception e){
        Log.d("Background Task",e.toString());
        }
        return data;
        }

// Executed after the complete execution of doInBackground() method
@Override
protected void onPostExecute(String result){
        NearbysearchParserTask parserTask = new NearbysearchParserTask();

        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParseTask
        parserTask.execute(result);
        }
}

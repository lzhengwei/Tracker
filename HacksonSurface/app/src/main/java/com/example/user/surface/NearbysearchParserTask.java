package com.example.user.surface;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2015/12/17.
 */
public class NearbysearchParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {
    JSONObject jObject;

    // Invoked by execute() method of this object
    @Override
    protected List<HashMap<String,String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;
        NearbysearchJson placeJsonParser = new NearbysearchJson();

        try{
            jObject = new JSONObject(jsonData[0]);
            /** Getting the parsed data as a List construct */
            places = placeJsonParser.parse(jObject);
            Log.v("placerun", "parser" + places);
        }catch(Exception e){
            Log.d("Exception",e.toString());
        }
        return places;
    }

    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(List<HashMap<String,String>> list){
        // Clears all the existing markers
        //map.clear();
        Log.v("placerun", "execute0" + list.size());
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                Log.v("placerun", "execute" + list.size());
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
                // Getting name
                String name = hmPlace.get("place_name");
                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker.
                //This will be displayed on taping the marker

                markerOptions.title(name + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.police));

 /*           markerOptions.title(name + " : " + vicinity);
            switch (GPS.spinner.getSelectedItemPosition())
            {
                case 0:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas2));
                    break;
                case 1:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking3));
                    break;
                case 2:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fixm));
                    break;
                case 3:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fixm));
                    break;
                default:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bigfix4));
            }*/

                // Placing a marker on the touched position
                Family_Map.map.addMarker(markerOptions);
            }
        }
    }
}

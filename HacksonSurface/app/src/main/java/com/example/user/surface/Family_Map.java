package com.example.user.surface;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhengwei on 2017/5/6.
 */

public class Family_Map extends AppCompatActivity {
    static GoogleMap map;
    private Handler GPSget_Handler = new Handler();
    private Button path_button,showpolice_button;
    private LatLng ElderLK;
    private Location myLocation = null;
    private LocationManager myLocationManager;
    private String strLocationPrivider;
    private Spinner zoom;
    private int zoomsize=15;
    private boolean runElderLocation=true;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        GPSget_Handler.postDelayed(GPSget,

                5000);

        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        strLocationPrivider = myLocationManager.getBestProvider(criteria, true);
// API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // No one provider activated: prompt GPS
            if (strLocationPrivider == null || strLocationPrivider.equals("")) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            // At least one provider activated. Get the coordinates
            switch (strLocationPrivider) {
                case "passive":
                    myLocationManager.requestLocationUpdates(strLocationPrivider, MINIMUM_TIME, MINIMUM_DISTANCE, mLocationListener);
                    Location location = myLocationManager.getLastKnownLocation(strLocationPrivider);
                    break;

                case "network":
                    break;

                case "gps":
                    break;

            }

            // One or both permissions are denied.
        } else {

            // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }

        }

        myLocationManager.requestLocationUpdates(strLocationPrivider, 1000, 10, mLocationListener);

        zoom=(Spinner)findViewById(R.id.spinner);
        path_button=(Button)findViewById(R.id.buttonPath);
        showpolice_button=(Button)findViewById(R.id.button2);
        ArrayAdapter adaptezoom=new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,new String[]{"1","2","3","4","5"});
        zoom.setAdapter(adaptezoom);

        path_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPSget_Handler.removeCallbacks(GPSget);
                if(ElderLK!=null)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ElderLK, 15));
                StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
                url.append("origin=" + surface.HomeLK.latitude+ "," +surface.HomeLK.longitude);
                url.append("&destination=" + ElderLK.latitude + "," + ElderLK.longitude);
                url.append("&sensor=false");
                RouteTask routetask = new RouteTask();
                routetask.execute(url.toString());
                runElderLocation=false;
            }
        });
        showpolice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");//
                map.clear();
                sb.append("location=" + myLocation.getLatitude() + "," + myLocation.getLongitude());
                sb.append("&radius=5000");
                sb.append("&name=police");
                sb.append("&sensor=true");
                sb.append("&key=AIzaSyC3RdGMjDxFiJv5A86bk2Qeminzzxrsb18");

                NearbysearchTask placesTask = new NearbysearchTask();
                Log.v("URLplace","is "+sb+" -----");
                // Invokes the "doInBackground()" method of the class PlaceTask
                placesTask.execute(sb.toString());
                if(ElderLK!=null)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ElderLK, 14));
                runElderLocation=false;
            }
        });
        zoom.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(ElderLK!=null)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ElderLK, (position+5)*2));
                zoomsize=(position+5)*2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.homemarker));
        markerOptions.position(surface.HomeLK);
        map.addMarker(markerOptions);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                new AlertDialog.Builder(Family_Map.this)
                        .setTitle("地標資訊")
                        .setMessage(marker.getTitle() + "\n" + getAddressbyGeoPoint(ElderLK))
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .show();
                return false;
            }
        });
    }
    public String getAddressbyGeoPoint(LatLng addlocation)
    {

        String strReturn = "Adress error";String returnAddress="Adress error";
        try
        {
            if (addlocation != null)
            {
                Geocoder gc = new Geocoder(Family_Map.this, Locale.getDefault());

                double geoLatitude = addlocation.latitude;
                double geoLongitude = addlocation.longitude;

                List<Address> lstAddress = gc.getFromLocation(geoLatitude, geoLongitude, 1);

                StringBuilder sb = new StringBuilder();
                returnAddress = lstAddress.get(0).getAddressLine(0);
                if (lstAddress.size() > 0)
                {
                    Address adsLocation = lstAddress.get(0);

                    for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++)
                    {
                        sb.append(adsLocation.getAddressLine(i)).append("\n");
                    }
                    sb.append(adsLocation.getCountryName());
                    sb.append(adsLocation.getPostalCode()).append("\n");
                    sb.append(adsLocation.getLocality()).append("\n");




                }

                strReturn = sb.toString();
            }
        }
        catch(Exception e)
        {
            Log.v("addressbegin",e.getMessage());
            strReturn=e.getMessage();
            e.printStackTrace();
        }
        return returnAddress;
    }
    private final Runnable GPSget = new Runnable()
    {
        public void run()
        {
            Thread t = new Thread(new sendPostRunnable(2));
            t.start();
            GPSget_Handler.postDelayed(GPSget, 5000);

        }
    };
    public Location getLocationPrivider(LocationManager lm)
    {
        Location retLocation = null;
        try
        {
            Criteria mCriteria01 = new Criteria();
            mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
            mCriteria01.setAltitudeRequired(false);
            mCriteria01.setBearingRequired(false);
            mCriteria01.setCostAllowed(true);
            mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
            strLocationPrivider = lm.getBestProvider(mCriteria01, true);
        }
        catch(Exception e)
        {
            Log.v("strLocationPrivider","success");
        }
        return retLocation;
    }
    public final LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            myLocation=location;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), 10));

            Log.v("GPSchange",location.getLatitude()+" "+location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

    };
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.v("while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    //================================= Http post ============================================================
    private String uriAPI ="http://192.168.137.1:17027/GPSget.php";
    private final int REFRESHDATA = 0x00000001;
    protected static final int REFRESH_DATA = 0x00000001;

    private String senddata(int mode)
    {
        Log.v("senddata","1");
        HttpPost httpRequest = new HttpPost(uriAPI);
        Log.v("senddata","2");
      /*
       * Post運作傳送變數必須用NameValuePair[]陣列儲存
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //params.add(new BasicNameValuePair("walkdata",String.valueOf(backtime) ));
        try
        {
          /* 發出HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity( params, HTTP.UTF_8));
            Log.v("senddata","3");
          /* 取得HTTP response */
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
          /* 若狀態碼為200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
             /* 取出回應字串 */
                String strResult = EntityUtils.toString(httpResponse
                        .getEntity());
                // 回傳回應字串
                Log.v("senddata","4");
                return strResult;

            }
        } catch (Exception e)

        {
            Log.v("senddataerror",e.toString());
            e.printStackTrace();

        }
        return null;
    }
    class sendPostRunnable implements Runnable
    {
        int mode=1;
        public sendPostRunnable(int inmode)
        {
            mode=inmode;
        }
        @Override
        public void run()
        {
            String result = senddata(mode);


            mHandler.obtainMessage(REFRESHDATA, result).sendToTarget();
        }

    }
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case REFRESHDATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null) {
                        // 印出網路回傳的文字
                        Log.v("sendresult", result);
                        int spaceindex=0;
                        for(int i=0;i<result.length();i++)
                        {
                            if(result.charAt(i)==' ')
                            {
                                spaceindex=i;
                                break;
                            }
                        }
                        double lon= Double.parseDouble(result.substring(0,spaceindex)),lai= Double.parseDouble(result.substring(spaceindex+1,result.length()));

                        ElderLK=new LatLng(lai,lon);
                        Log.v("ElderLK", ElderLK.latitude+" "+ElderLK.longitude+" "+lon );
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.eldermarker));
                        markerOptions.position(ElderLK);
                        map.addMarker(markerOptions);
                        if(runElderLocation) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ElderLK, zoomsize));
                        }
                        //Log.v("distance",distance(ElderLK,surface.HomeLK)+"");
                        //  Toast.makeText(TestActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
    private double distance(LatLng lk1,LatLng lk2) {
        int R = 6371; // km (change this constant to get miles)
        double dLat = (lk2.latitude-lk1.latitude) * Math.PI / 180;
        double dLon = (lk2.longitude-lk2.longitude) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lk1.latitude * Math.PI / 180 ) * Math.cos(lk2.latitude * Math.PI / 180 ) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        return d;
    }
    //==============================================================================================
}

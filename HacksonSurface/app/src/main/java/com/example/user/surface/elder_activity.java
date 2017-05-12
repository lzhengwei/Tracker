package com.example.user.surface;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengwei on 2017/5/6.
 */

public class elder_activity extends AppCompatActivity {
    public Button mainbutton;
    private Handler GPSget_Handler = new Handler();
    private LatLng ElderLK;
    private Location myLocation = null;
    private LocationManager myLocationManager;
    String sendstate="0",strLocationPrivider;
    int warningsound;
    private SoundPool sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.elder_layout);
        Thread t = new Thread(new sendPostRunnable(1));
        t.start();
        GPSget_Handler.postDelayed(GPSget, 5000);
        sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);

        warningsound=sound.load(this,R.raw.sos,1);
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocation = getLocationPrivider(myLocationManager);

        mainbutton=(Button) findViewById(R.id.sos);
        mainbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendstate="1";
                Thread t = new Thread(new sendPostRunnable(1));
                t.start();
                sound.play(warningsound, 1, 1, 0, 0, 1);
                try {
                    Thread.sleep(5000);
                    sound.stop(warningsound);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(elder_activity.this, Family_Map.class);
                startActivity(intent);
            }

        });
        mainbutton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changemode.writetxtdata("0");
                Log.v("changemode","yes");
                return true;
            }
        });

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
            retLocation = lm.getLastKnownLocation(strLocationPrivider);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return retLocation;
    }
    public final LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            myLocation=location;
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
    //================================= Http post ============================================================
    private String[] uriAPI =new String[]{"","http://192.168.137.1:17027/state.php","http://192.168.137.1:17027/GPS.php"};
    private final int REFRESHDATA = 0x00000001;
    protected static final int REFRESH_DATA = 0x00000001;

    private String senddata(int mode)
    {
        Log.v("senddata","1");
        HttpPost httpRequest = new HttpPost(uriAPI[mode]);
        Log.v("senddata","2");
      /*
       * Post運作傳送變數必須用NameValuePair[]陣列儲存
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        switch (mode) {
            case 1:
                params.add(new BasicNameValuePair("GPS_Lon", sendstate));
                params.add(new BasicNameValuePair("GPS_Lai", sendstate));
                break;
            case 2:
                params.add(new BasicNameValuePair("GPS_Lon", String.valueOf(myLocation.getLongitude())));
                params.add(new BasicNameValuePair("GPS_Lai", String.valueOf(myLocation.getLatitude())));
                break;
        }
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

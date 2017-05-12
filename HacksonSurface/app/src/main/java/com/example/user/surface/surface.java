package com.example.user.surface;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class surface extends AppCompatActivity {
    public Button idbutton;
    public Button searchbutton;
    public Button setbutton;
    public Button idbutton1;
    public Button searchbutton1;
    public Button setbutton1;
    private Button condition1;
    private Handler Checkstate_Hanlder = new Handler();
    private  NotificationManager notificationManager;
    private Notification notification;
    final int notifyID = 1; // 通知的識別號碼ma
    static int mainmode=0;
    static LatLng HomeLK;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static String[] PERMISSIONS_LOCATION= {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean isloss=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surface);
        verifyStoragePermissions(this);
        HomeLK=new LatLng(22.754000,120.335500);
        readdata();
        Log.v("mainmode",mainmode+"");
        //mainmode=1;
        if(mainmode==1)
        {
            Log.v("mainmode",mainmode+"");

            Intent intent = new Intent();
            intent.setClass(surface.this, elder_activity.class);
            startActivity(intent);
            finish();
        }
        else {



            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            idbutton=(Button) findViewById(R.id.identity);
            idbutton1=(Button) findViewById(R.id.identity1);
            searchbutton=(Button) findViewById(R.id.position);
            searchbutton1=(Button) findViewById(R.id.position1);
            setbutton=(Button) findViewById(R.id.set);
            setbutton1=(Button) findViewById(R.id.set1);
            condition1=(Button) findViewById(R.id.condition);
            //個人資料 button

            condition1.setBackgroundColor(Color.GREEN);

            idbutton = (Button) findViewById(R.id.identity);
            idbutton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, Inputidentity.class);
                    startActivity(intent);

                }

            });
            idbutton1=(Button) findViewById(R.id.identity1);
            idbutton1.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, Inputidentity.class);
                    startActivity(intent);

                }

            });
            //定位查詢 button
            searchbutton1 = (Button) findViewById(R.id.position1);
            searchbutton1.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, Family_Map.class);
                    startActivity(intent);

                }

            });
            searchbutton = (Button) findViewById(R.id.position);
            searchbutton.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, Family_Map.class);
                    startActivity(intent);

                }

            });
            //設定模式 button
            setbutton = (Button) findViewById(R.id.set);
            setbutton.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, changemode.class);
                    startActivity(intent);
                }

            });
            setbutton1 = (Button) findViewById(R.id.set1);
            setbutton1.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(surface.this, changemode.class);
                    startActivity(intent);
                }

            });
            Checkstate_Hanlder.postDelayed(Checkstate, 5000);
            final int requestCode = notifyID; // PendingIntent的Request Code
            final Intent intentc = new Intent(getApplicationContext(), Family_Map.class); // 開啟另一個Activity的Intent
            final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
            final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext()); // 建立TaskStackBuilder
            stackBuilder.addParentStack(Family_Map.class); // 加入目前要啟動的Activity，這個方法會將這個Activity的所有上層的Activity(Parents)都加到堆疊中
            stackBuilder.addNextIntent(intentc); // 加入啟動Activity的Intent
            final PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, flags); // 取得PendingIntent
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
            notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.warning).setContentTitle("您的家人狀態異常").setContentText("請盡快聯絡").setContentIntent(pendingIntent)
                    .setSound( Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sos)).build();
        }
       //button宣告
        //init nofication



    }
    private final Runnable Checkstate = new Runnable()
    {
        public void run()
        {
            Thread t = new Thread(new surface.sendPostRunnable(2));
            t.start();
            Checkstate_Hanlder.postDelayed(Checkstate, 5000);

        }
    };
    private void readdata()
    {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();
            //讀取文件檔路徑
            File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/Tracker/TrackerMode"  + ".txt");
            if (file.exists()) {
                FileReader mFileReader = new FileReader(mSDFile.getParent() + "/" + mSDFile.getName() + "/Tracker/TrackerMode"  + ".txt");
                BufferedReader mBufferedReader = new BufferedReader(mFileReader);

                String mTextLine = mBufferedReader.readLine();
                if(mTextLine.equals("0"))
                {
                    mainmode=0;
                }
                else
                    mainmode=1;
                Log.v("readdata",mTextLine);

            }
        }
        catch (Exception x)
        {Log.v("readdata",x.toString());}
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
            );
        }
    }

//================================================================================================
//================================= Http post ============================================================
private String uriAPI ="http://192.168.137.1:17027/stateget.php";
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
                        if(Integer.parseInt(result)==1) {
                            isloss = true;
                            condition1.setBackgroundColor(Color.RED);
                            condition1.setText("目前狀態:異常");
                            notificationManager.notify(notifyID, notification); // 發送通知
                        }
                        else
                        {
                            condition1.setBackgroundColor(Color.GREEN);
                            condition1.setText("目前狀態:安全");
                        }



                        //  Toast.makeText(TestActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    //==============================================================================================
    }



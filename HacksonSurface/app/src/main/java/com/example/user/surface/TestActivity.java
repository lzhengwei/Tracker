package com.example.user.surface;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;


/**
 * Created by Jay on 2016/9/13.
 */
public class TestActivity extends AppCompatActivity implements SensorEventListener {

    final Context context = this;
    private Button btn_work,btn_starttest;
    private TextView answertext;
    private RelativeLayout testchart;
    private GraphicalView testview;
    private Spinner signallist;
    //============ Answer ===========================================
    private  String answer="";
    //============== readfile =======================================
    private String viewfilename;
    private int fileid=-1,ratio=5;
    private double datamax=0,datamin=100,showdatamax=0,showdatamin=100;
    //============= view ==========================================
    private double[] viewdata=new double[1000], viewdata2=new double[1000],dx;
    private int viewdata_index=0,viewdata_index2=0;
    private double[] mspdatam1=new double[2000];
    private double[] mspdatam2=new double[2000];

    //============ tiptool =============================================
   private PopupWindow popup = null;
    private View layout;
    private View viewParent;
    //========= main ====================================
    int Tugmode=0,fileinmode=0,mainmode=surface.mainmode,showmode=0,Btmode=0;
    //========== walk ==========================================
    private boolean up1=false,isturn=false,iswalked1=false,timeup=false;
    int walkpoint[]=new int[10];
    int mspwalkpoint[] =new int[30];
    int mspwalkpoint2[] =new int[30];
    int walkdataindex=0,Walkpoint,Btdatasize1=0,Btdatasize2=0;
    String txtdata="";
    //==============================sensor=================================
    private SensorManager sensorManager,compassManager;
    private Sensor accelerometer,magntic;
    private float[] record=new float[5];

    private float lastX=0, lastY=0, lastZ=0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];
    private float azimuth;
    
    private float init_orentation,orentationvalue,lst_orentation;
    //==============sensor time======================================
    private float sensorsec=0,sensorpoint=0;
    private int sensorsecond=0,sensorcountsec=0;
    //====================time================================================
    private float uptime=0,alltime=0,walktime=0,sec=0,turnaroundtime=0,backtime=0;
    private float secondpoint;
    private float second;
    private float[] timespit=new float[5];
    //==================Gait==============================================
    private int step = 0,datanumber=0;
    private double lstValue = 0,oriValue;  //上次的值
    private double curValue = 0,range=2;  //当前值
    private boolean motiveState = false;   //是否处于运动状态
    private boolean processState = false;   //标记当前是否已经在计步
    //===========average data search===================================
    double[] peak=new double[3];
    double[] save=new double[5],averagedata=new double[10];
    double valley;
    double[] time=new double[100];
    int searchmode=1,stepcount=0;
    double average_mvalue,mvalue,mdelta;
    //========== sound ==========================================
    public MediaPlayer mediaPlayer;
    private SoundPool sound;
    int startsound,upsound,walksound,endsound,turnaroundsound,walkbacksound,ding,downsound,startnoseatsound;
    //============= draw ========================================
    int gridindex[]=new int[20];
//================= Bluetooth ====================================
private static final UUID BLUETOOTH_SPP_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectedThread connect1,connect2;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket,nnSocket;
    BluetoothDevice mmDevice,nnDevice;
    OutputStream mmOutputStream,nnOutputStream;
    private Handler tryconnect_handler=new Handler();
    String btreceivedata1="",btreceivedata2="";
    //========================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

//=================== main ==========================================
        testchart = (RelativeLayout) findViewById(R.id.test);   //三軸
        answertext=(TextView) findViewById(R.id.textView2);
        btn_starttest=(Button)findViewById(R.id.button7);
        signallist=(Spinner)findViewById(R.id.spinner5);

        //   testview=(GraphicalView)findViewById(R.id.view2);
        //======================sensor======================================
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
       // compassManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        boolean haveaccsensor=true,havemagsensor=true;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            Log.v("sensor", "sensor get ");
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
           // sensorManager.registerListener(TestActivity.this, accelerometer, 100000);

        } else {
            haveaccsensor=false;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success! we have an accelerometer
            magntic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            // compassManager.registerListener(compass, compassManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),100000);
        }
        else
        havemagsensor=false;
        if(!haveaccsensor || !havemagsensor)
        {
            String showsensorwrong="";
            if(!haveaccsensor && !havemagsensor)
                showsensorwrong="無\n三軸加速規感測器\n方向感測器";
            else if(!haveaccsensor)
                showsensorwrong="無\n三軸加速規感測器";
            else if(!havemagsensor)
                showsensorwrong="無\n方向感測器";

            ShowAlertMessage(showsensorwrong);
        }
        //=================  sound ========================================
        mediaPlayer=new MediaPlayer();
        mediaPlayer=MediaPlayer.create(TestActivity.this,R.raw.endvoice);

        sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        startsound=sound.load(this,R.raw.readyvoice,1);
        upsound=sound.load(this,R.raw.upvoice,1);
        walksound=sound.load(this,R.raw.walkvoice,1);
        turnaroundsound=sound.load(this,R.raw.turnaroundvoice,1);
        walkbacksound=sound.load(this,R.raw.walkbackvoice,1);
        endsound=sound.load(this,R.raw.endvoice,1);
        ding=sound.load(this,R.raw.walksound,1);
        downsound=sound.load(this,R.raw.downvoice,1);
        startnoseatsound=sound.load(this,R.raw.readynoseatdown,1);
        //============ read file ===================================
        Bundle bundle= this.getIntent().getExtras();
        Log.v("mainmode",mainmode+"");

        if(mainmode==0) {
            boolean nodata=bundle.getBoolean("nodata");
             viewfilename=bundle.getString("filename");
            Log.v("filename", viewfilename);
            Log.v("fileid", fileid + "");
            if(nodata)
            {
                fileid=bundle.getInt("fileid");
                writetxtdataorigin();
            }
            setTitle(viewfilename + "結果");
            btn_starttest.setVisibility(View.INVISIBLE);
            Log.v("fileindmode",fileinmode+"");
        }
        else
        {
            switch(mainmode)
            {
                case 1:
                    setTitle("起立行走測驗");
                    break;
                case 2:
                    setTitle("10公尺走路測驗");
                    break;
            }
        }
        Log.v("mainmode", mainmode + "");

        //====================== button ==========================================================================
        btn_starttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final  int sensorrate=100000;
                switch(mainmode)
                {
                    case 1:
                        /*if(which==2)
                        {
                            Btmode=1;
                            txtdata+="B\n";
                            Bluetoothopen();


                        }*/
                        Bluetoothopen();

                        sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                        sensorManager.registerListener(TestActivity.this, accelerometer,  SensorManager.SENSOR_DELAY_NORMAL);
                        sensorManager.registerListener(TestActivity.this, magntic,  SensorManager.SENSOR_DELAY_NORMAL);
                        searchmode = 1;
                        sec = sensorsec;

                        break;
                    case 2:
                       /* if(which==2)
                        {
                            Btmode=1;
                            txtdata+="B\n";
                            Bluetoothopen();

                        }*/
                        Bluetoothopen();

                        sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                        sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                        searchmode=1;
                        sec = sensorsec;
                        break;
                }
                btn_starttest.setVisibility(View.INVISIBLE);
                String[] startmode={"手機模式","","感測器模式"};
             /*   new AlertDialog.Builder(TestActivity.this)
                        .setTitle("以配對裝置")
                                //.setMessage("選擇一裝置進行配對")
                        .setItems(startmode, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(mainmode)
                                {
                                    case 1:
                                        if(which==2)
                                        {
                                            Btmode=1;
                                            txtdata+="B\n";
                                           Bluetoothopen();


                                        }

                                            sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                                            sensorManager.registerListener(TestActivity.this, accelerometer,  SensorManager.SENSOR_DELAY_NORMAL);
                                            sensorManager.registerListener(TestActivity.this, magntic,  SensorManager.SENSOR_DELAY_NORMAL);
                                            searchmode = 1;
                                            sec = sensorsec;

                                        break;
                                    case 2:
                                        if(which==2)
                                        {
                                            Btmode=1;
                                            txtdata+="B\n";
                                            Bluetoothopen();

                                        }
                                        sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                                        sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                                        searchmode=1;
                                        sec = sensorsec;
                                        break;
                                }
                                btn_starttest.setVisibility(View.INVISIBLE);



                            }
                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();*/

            }
        });
        btn_work = (Button) findViewById(R.id.button8);
        btn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signallist.setVisibility(View.VISIBLE);

                showmode=2;
                answer="";
                readdata(viewfilename);
                if(viewfilename.charAt(0)=='1')
                {  fileinmode=2;CCnorm();}
                else
                    fileinmode=1;

                //  Log.v("CCnorm", CCnorm()+"") ;
               // Log.v("walkpoint",walkpoint[0]+"");
                answertext.setText("測驗結果\n"+answer);
                showchart();
            }
        });
        String[] showlistvalue=new String[]{" ","手機訊號","感測器訊號"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, showlistvalue);
        signallist.setAdapter(adapter);

        signallist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void  onItemSelected(AdapterView<?> parent, View view, int position, long id)  {
                switch (position) {
                    case 1:
                        Btmode=0;
                        showchart();
                        Log.v("spinner", "0");
                        break;
                    case 2:
                        Btmode=1;
                        showchart();
                        Log.v("spinner", "1");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this,magntic);
        Log.v("log", "destroy");
        if(connect1!=null)
            connect1.cancel();
        if(connect2!=null)
            connect2.cancel();
        Thread.currentThread().interrupt();
        // compassManager.unregisterListener(compass);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_settings:
                try {
                    File mSDFile = Environment.getExternalStorageDirectory();
                    //讀取文件檔路徑
                    File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + viewfilename+".txt");
                    deletefile.delete();
                    if(!deletefile.exists())
                    {
                        Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();

                    }
                   // MainActivity.filelist.remove(fileid);
                    // Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();


                }
                catch (Exception x)
                {
                    Log.v("delete exception",x.toString());
                }
                break;
           /* case R.id.action_deleteall:
                try {
                    File mSDFile = Environment.getExternalStorageDirectory();
                    //讀取文件檔路徑
                    for(int i=0;i<MainActivity.filelist.size();i++) {
                        String deletefilename=MainActivity.filelist.get(i);
                        File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + deletefilename + ".txt");
                        deletefile.delete();

                        // Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();
                    }
                       MainActivity.filelist.clear();
                    Toast.makeText(TestActivity.this, "已刪除所有紀錄", Toast.LENGTH_SHORT).show();

                }
                catch (Exception x)
                {
                    Log.v("delete exception",x.toString());
                }
                break;*/
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();

                sensorcountsec++;
                sensorpoint=(float)sensorcountsec/10;
                sensorsec=sensorsecond+sensorpoint;
                // secondpoint=getTimessecond();
                if(sensorcountsec>=9)
                { sensorsecond= (int)(sensorsec+1);sensorcountsec=-1;}

                float[] value = event.values;
                deltaX = Math.abs(lastX - value[0]);
                deltaY = Math.abs(lastY - value[1]);
                deltaZ = Math.abs(lastZ - value[2]);
                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
                mvalue=magnitude2(value[0], value[1]);
                mdelta= magnitude(deltaX, deltaY, deltaZ);

                switch(mainmode)
                {
                    case 1:
                        switch(Tugmode) {
                            case 0:
                                Log.v("time",sensorsec+"");

                                if(sensorsec-sec>=12)
                                {
                                    btwrite('m',"a");
                                    btwrite('n',"a");
                                    Log.v("time", "over" + sensorsec + "");
                                    sound.play(ding, 1, 1, 0, 0, 1);
                                    sound.play(upsound, 1, 1, 0, 0, 1);
                                    Tugmode=1;
                                }
                                break;
                            case 1:
                                //Log.v("up","yes");
                                save[4] = save[3];
                                save[3] = save[2];
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = Math.abs(lastY);
                                average_mvalue = (save[4] + save[3] + save[0] + save[2] + save[1]) / 5;

                                //=========================================================

                                //=========================================================
                                if (mdelta <= 0.4)
                                    mdelta = 0;
                                //  updata += lastY + " ";
                                record[2] = record[1];
                                record[1] = record[0];
                                record[0] = (float) mdelta;
                                if (mdelta != 0 && record[1] == 0 && record[2] == 0 && !motiveState) {
                                    motiveState = true;
                                    oriValue = average_mvalue;
                                    uptime = sensorsec;
                                    alltime=sensorsec;
                                    //sound.play(upsound, 1, 1, 0, 0, 1);
                                    Log.v("upget", ">1  " + "\nvalue : " + oriValue + "\ndelta : " + mdelta);
                                    //  Log.v("upget",">1  "+"\ntime : "+sec);
                                } else if (motiveState) {
                                    //  up=true;
                                    //================================================
                                    walkdataindex++;
                                    txtdata=txtdata+average_mvalue+"\n";
                                    //===============================================
                                   // lstValue = average_mvalue;
                                   // Log.v("upget","lstvalue : " + lstValue );

                                    if (average_mvalue>=8) {
                                        motiveState = false;
                                        up1 = true;
                                        save = new double[10];
                                        uptime = sensorsec - uptime;
                                        walktime=sensorsec;
                                        sound.play(ding, 1, 1, 0, 0, 1);
                               /* if (lstValue < average_mvalue) {
                                    lstValue = average_mvalue;
                                    Log.v("down", " > " + lstValue);
                                } else if (lstValue > average_mvalue && Math.abs(average_mvalue - oriValue) >2 ) {
                                    lstValue = -9999;
                                    motiveState = false;
                                    up1=true;
                                    save=new double[10];
                                    uptime=sec-uptime;
                                    sound.play(ding, 1, 1, 0, 0, 1);*/
                                   /* try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                        sound.play(walksound, 1, 1, 0, 0, 1);
                                        Tugmode = 2;
                                        txtdata+="E"+walkdataindex+"\n";
                                        Log.v("E",walkdataindex+"");

                                    }
                                }

                                break;
                            case 2:
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                average_mvalue = (save[0] + save[2] + save[1]) / 3;
                                averagedata[4] = averagedata[3];
                                averagedata[3] = averagedata[2];
                                averagedata[2] = averagedata[1];
                                averagedata[1] = averagedata[0];
                                averagedata[0] = average_mvalue;
                                //  viewdata += average_mvalue + " ";
                                //---------------------------------------------------------
                                // walkdata[walkdataindex]=average_mvalue;
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                //-----------------------------------------------------
                                switch (searchmode) {
                                    case 1:
                                        if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] && averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4] && peak[0] == 0) {
                                            peak[0] = averagedata[2];
                                            searchmode = 2;

                                        }
                                        break;
                                    case 2:
                                        if (averagedata[2] < averagedata[1] && averagedata[2] < averagedata[0] && averagedata[2] < averagedata[3] && averagedata[2] < averagedata[4]) {
                                            if (Math.abs(peak[0] - averagedata[2]) > 0.5) {
                                                valley = averagedata[2];
                                                Walkpoint=walkdataindex-3;
                                                searchmode = 3;
                                            } else {
                                                //    Log.v("valley back 1", "valley : " +  save[1] + " peak[0] : " + peak[0]);
                                                peak[0] = 0;
                                                searchmode = 1;
                                            }
                                        }
                                        break;
                                    case 3:

                                        if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] && averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4]) {
                                            if (Math.abs(averagedata[2] - valley) > 0.7) {

                                                //Log.v("valley get", "valley : " + valley + " peak[0] : " + peak[0] + " peak[1] : " +save[1]);
                                                peak[0] = averagedata[2];
                                                time[stepcount] = sensorsec;
                                                stepcount++;
                                                Log.v("step time", "time " + stepcount + " = " + time[stepcount]);

                               /*   sound.autoPause();
                                   sound.play(walksound, 1, 1, 0, 0, 1);*/
                                                //  peak[1] = 0;
                                                if (stepcount >= 5) {
                                                    Tugmode = 3;
                                                   /* if (orentationvalue > 180)
                                                        init_orentation = orentationvalue;
                                                    else
                                                        init_orentation = orentationvalue + 180;*/
                                                    init_orentation=orentationvalue;
                                                    Log.v("ori init","ori :"+orentationvalue+"init : "+init_orentation);

                                                    sound.play(ding, 1, 1, 0, 0, 1);
                                                    //walktime = (float) ((time[1] - time[0]) + (time[2] - time[1]) + (time[3] - time[2]) + (time[4] - time[3])) / 4;
                                                    walktime=sensorsec-walktime;
                                                    timespit[1] = sensorsec;
                                                    sound.play(turnaroundsound, 1, 1, 0, 0, 1);

                                                    isturn = true;
                                                    iswalked1 = true;
                                                    txtdata+="E"+walkdataindex+"\n";
                                                    Log.v("E",walkdataindex+"");

                                                    Tugmode = 3;

                                                } else
                                                    searchmode = 2;
                                            } else {
                                                peak[0] = 0;
                                                searchmode = 1;
                                            }

                                        }
                                        break;
                                }
                                break;
                            case 3:
                                //turndata += lastZ + " ";
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                average_mvalue = (save[0] + save[2] + save[1]) / 3;
                                //=========================================================
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                //=========================================================
                                if (Math.abs(orentationvalue - init_orentation) > 160 && Math.abs(orentationvalue - init_orentation) < 200 && isturn) {
                                    isturn = false;
                                    turnaroundtime = sensorsec - timespit[1];
                                    timespit[2]=sensorsec;
                                    Tugmode = 4;
                                    sound.play(ding, 1, 1, 0, 0, 1);
                                    Log.v("ori","ori"+orentationvalue+"init"+init_orentation);
                                    Log.v("Time", " sec : " + sec + " walktime : " + walktime
                                            + "step 1 : " + time[0] + "step 2 : " + time[1] + "step 3 : " + time[2] + "step 4 : " + time[3] + "step 5 : " + time[4] +
                                            " turnaround time :  " + turnaroundtime);
                           /* try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                                    sound.play(walkbacksound, 1, 1, 0, 0, 1);
                                    txtdata+="E"+walkdataindex+"\n";
                                    Log.v("E",walkdataindex+"");
                                }
                                break;
                            case 4:
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                average_mvalue = (save[0] + save[2] + save[1]) / 3;
                                //=========================================================
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                //=========================================================
                                if (Math.abs(value[0]) > 8) {
                                    record[2] = record[1];
                                    record[1] = record[0];
                                    record[0] = Math.abs(lastX);
                                    if (record[1]>5 && record[0] >5) {
                                        alltime=sensorsec-alltime;
                                        backtime = sensorsec - timespit[2];
                                        Log.v("Time", " sec : " + sec + " walktime : " + walktime +
                                                "step 1 : " + time[0] + "step 2 : " + time[2] + "step 3 : " + time[3] + "step 4 : " + time[4] + "step 5 : " + time[5] +
                                                " turnaround time :  " + turnaroundtime
                                                + " back time :  " + backtime);

                                      /*  if (getIPAddress() != null) {
                                            Thread t = new Thread(new sendPostRunnable());
                                            t.start();
                                        } else {
                                            Toast.makeText(TUG_MainActivity.this, "目前無連接網路，無法上傳資料", Toast.LENGTH_LONG).show();
                                        }*/
                                        String time="t總時間 : "+String.format("%.2f", alltime)+" sec"+
                                                "\nt起立時間 : "+String.format("%.2f", uptime)+" sec"+
                                                "\nt行走時間 : "+String.format("%.2f",walktime)+" sec"+
                                                "\nt轉彎時間 : "+String.format("%.2f", turnaroundtime)+
                                                "\nt返回座位時間 : "+String.format("%.2f", backtime);
                                        txtdata+=time+"\n";
                                        for(int i=0;i<5;i++)
                                        {
                                            txtdata+="p"+walkpoint[i]+"\n";
                                            Log.v("walkpoint",walkpoint[i]+"");
                                        }
                                        txtdata+="E"+walkdataindex+"\n";
                                        Log.v("E", walkdataindex + "");
                                       if(Btmode==0)
                                        writetxtdata();
                                        walkdataindex=0;
                                        //writetxtdata_andGO();
                                        Tugmode = -1;
                                        btwrite('m',"o");
                                        btwrite('n', "o");
                                        initial_variables();

                                    }
                                }
                                break;
                        }
                        break;
                    case 2:
                        Log.v("time",sensorsec+"");

                        if(sensorsec-sec<=13 && !timeup)
                        {
                            if(sensorsec-sec>=12) {
                                btreceivedata1="";
                                btreceivedata2="";
                                btwrite('m',"a");
                                btwrite('n',"a");
                                Log.v("time", "over" +sensorsec + "");

                                sound.play(ding, 1, 1, 0, 0, 1);
                                sound.play(walksound, 1, 1, 0, 0, 1);
                                save = new double[10];
                                averagedata = new double[10];
                                timeup = true;
                                sec=sensorsec;
                            }
                            mvalue=0;
                        }

                        save[2] = save[1];
                        save[1] = save[0];
                        save[0] = mvalue;
                        average_mvalue = (save[0] + save[2] + save[1]) / 3;
                        averagedata[4] = averagedata[3];
                        averagedata[3] = averagedata[2];
                        averagedata[2] = averagedata[1];
                        averagedata[1] = averagedata[0];
                        averagedata[0] =average_mvalue;
                        if(timeup )
                        {
                            //  walkdata[walkdataindex]=average_mvalue;
                            walkdataindex++;
                            txtdata=txtdata+average_mvalue+"\n";
                            /*txtdata=txtdata+"x"+lastX+"\n";
                            txtdata=txtdata+"y"+lastY+"\n";
                            txtdata=txtdata+"z"+lastZ+"\n";
                            txtdata=txtdata+"m"+mvalue+"\n";*/
                        }
                        switch (searchmode) {
                            case 1:
                                if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] &&  averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4] && peak[0] == 0) {
                                    peak[0] = averagedata[2];
                                    searchmode = 2;

                                }
                                break;
                            case 2:
                                if (averagedata[2] < averagedata[1] && averagedata[2] < averagedata[0] &&  averagedata[2] < averagedata[3] && averagedata[2] < averagedata[4]) {
                                    if (Math.abs(peak[0] - averagedata[2]) > 0.5) {
                                        valley = averagedata[2];
                                        Log.v("step valley index",walkdataindex+"");
                                        Walkpoint=walkdataindex-3;
                                        second=sensorsec;
                                        searchmode = 3;
                                    } else {
                                        //    Log.v("valley back 1", "valley : " +  save[1] + " peak[0] : " + peak[0]);
                                        peak[0] = 0;
                                        searchmode = 1;
                                    }
                                    Log.v("step valley ",walkdataindex+"");

                                }
                                break;
                            case 3:
                                if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] &&  averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4]) {
                                    if (Math.abs(averagedata[2] - valley) > 0.7) {
                                        //sound.play(ding, 1, 1, 0, 0, 1);
                                        //Log.v("valley get", "valley : " + valley + " peak[0] : " + peak[0] + " peak[1] : " +save[1]);
                                        peak[0] = averagedata[2];
                                        time[stepcount]=second;
                                        //  maintext.setText(maintext.getText()+"\n time "+stepcount+" is "+ time[stepcount]);
                                        walkpoint[stepcount]=Walkpoint;
                                        stepcount++;
                               /*   sound.autoPause();
                                   sound.play(walksound, 1, 1, 0, 0, 1);*/
                                        //  peak[1] = 0;
                                        if(stepcount>=10)
                                        {

                                            sound.play(ding, 1, 1, 0, 0, 1);

                                            walktime=(float)((time[4]-time[2])+(time[6]-time[4])+(time[8]-time[6]))/3;
                                            // maintext.setText(maintext.getText() + "\nwalk time is " + walktime);

                                            double walkspeed=(120*5)/(sensorsec-sec);
                                            for(int i=0;i<stepcount;i++)
                                            {
                                                txtdata+="p"+walkpoint[i]+"\n";
                                                Log.v("walkpoint",walkpoint[i]+"");
                                            }
                                            txtdata+="l120\n"+"s"+String.format("%.2f", walkspeed)+"\n"+"";
                                            walkdataindex=0;
                                            if(Btmode==0)
                                            writetxtdata();
                                            btwrite('m',"o");
                                            btwrite('n',"o");
                                        //    Log.v("txt",txtdata);
                                           // writetxtdata_andGO();
                                            searchmode=1;
                                            initial_variables();

                                     /*       String[] titles = new String[] { "折線1"}; // 定義折線的名稱

                                            testchart.removeAllViews();
                                            double dx[]=new double[walkdataindex],dy[]=new double[5];
                                            for(int i=0;i<5;i++)
                                            {
                                                dy[i]=walkdata[(int)walkpoint[i]];
                                            }
                                            for(int i=0;i<walkdataindex;i++)
                                            {
                                                dx[i]=i;
                                            }
                                            lx.clear();
                                            ly.clear();
                                            ly.add(walkdata);
                                            lx.add(dx);
                                            Log.v("walkpoint",walkpoint.length+"");
                                            barx.add(walkpoint);
                                            bary.add(dy);



                                            int[] colors = new int[] { Color.RED};// 折線的顏色
                                            PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.TRIANGLE }; // 折線點的形狀
                                            XYMultipleSeriesDataset dataset = buildDatset(titles, lx, ly); // 儲存座標值
                                            XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);
                                            setChartSettings(renderer, "折線圖展示", "X軸名稱", "Y軸名稱", 0, walkdataindex, 8, 18, Color.BLACK);// 定義折線圖
                                            View chart = ChartFactory.getLineChartView(this, dataset, renderer);

                                            testchart.addView(chart);*/

                                            //  pointview();
                                        }
                                        else
                                            searchmode = 2;
                                    }
                                    else
                                    {
                                        peak[0]=0;
                                        searchmode = 1;
                                    }

                                }
                                break;
                        }
                        break;
                }
                break;
        }

        if (mags != null && accels != null ) {
            gravity = new float[9];
            magnetic = new float[9];
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
            float[] outGravity = new float[9];
            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X, SensorManager.AXIS_Z, outGravity);
            SensorManager.getOrientation(outGravity, values);
         //   azimuth = values[0] * 57.2957795f;
            azimuth=(float)Math.toDegrees(values[0] +360)%360;
            if(azimuth<0)
                azimuth+=360;

            lst_orentation=azimuth;

                if( Math.abs(lst_orentation-azimuth)<=30)
                orentationvalue = azimuth;
                Log.v("degree",azimuth+"");


            //Log.v("ori",orentationvalue+"");
           // mvalue=(float)Math.sqrt(accels[0]*accels[0]*+accels[1]*accels[1]+accels[2]*accels[2]);
            // maintext.setText(azimuth+"");
            mags = null;
            accels = null;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void showchart()
    {
        int btdatamax= Btdatasize1>Btdatasize2?Btdatasize1:Btdatasize2;
        testchart.removeAllViews();
        List<double[]> lx = new ArrayList<double[]>(); // 點的x坐標
        List<double[]> ly = new ArrayList<double[]>(); // 點的y坐標
        // 數值X,Y坐標值輸入
        if(Btmode==0) {
            dx = new double[viewdata_index];
            for (int i = 0; i < viewdata_index; i++) {
                dx[i] = i;
            }
            lx.clear();
            ly.clear();
            Log.v("length viewdatax_index", viewdata_index + "");
            Log.v("length viewdatax_index", viewdata_index2 + "");

            lx.add(dx);
            ly.add(viewdata);
            if (viewdata_index > 300) {
                lx.add(dx);
                ly.add(viewdata2);
                if (fileid == 0) {
                    lx.add(new double[]{92, 250});
                    ly.add(new double[]{5, 5});
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    lx.add(new double[]{walkpoint[i]});
                    ly.add(new double[]{viewdata[walkpoint[i]]});
                }
            }
        }
        else
        {
            if(Btdatasize1>0) {
                dx = new double[Btdatasize1];
                for (int i = 0; i < Btdatasize1; i++) {
                    dx[i] = i;
                }
                lx.clear();
                ly.clear();
                lx.add(dx);
                ly.add(mspdatam1);
            }
            if(Btdatasize2>0) {
                dx = new double[Btdatasize2];
                for (int i = 0; i < Btdatasize2; i++) {
                    dx[i] = i;
                }
                lx.add(dx);
                ly.add(mspdatam2);
            }
            Log.v("Btdatasize1 2", Btdatasize1 + " "+Btdatasize2 + " "+lx.size());

        }

        String[] titles = new String[]{"右腳","左腳",""}; // 定義折線的名稱
        int[] colors = new int[]{Color.RED,Color.BLUE,Color.GREEN};// 折線的顏色
        PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.TRIANGLE}; // 折線點的形狀
        XYMultipleSeriesRenderer renderer;
        XYMultipleSeriesDataset dataset;
        switch(fileid)
        {
            case 1:
                dataset = buildDatset(1, titles, lx, ly); // 儲存座標值
                renderer = buildRenderer(1,colors, styles, true);
                setChartSettings(renderer, "折線圖展示", "Time ( s )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);// 定義折
                break;
            case 0:
                dataset = buildDatset(3,titles, lx, ly); // 儲存座標值
                renderer = buildRenderer(3,colors, styles, true);
                setChartSettings(renderer, "折線圖展示", "\nGait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);
                break;
            default:
                if(Btmode==0 ) {
                    dataset = buildDatset(2, titles, lx, ly); // 儲存座標值
                    renderer = buildRenderer(2, colors, styles, true);
                    setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);// 定義折線圖
                }
                else
                {
                    switch(lx.size())
                    {
                        case 2:
                            dataset = buildDatset(2, titles, lx, ly); // 儲存座標值
                            renderer = buildRenderer(2, colors, styles, true);
                            setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, btdatamax, datamin, datamax, Color.BLACK);// 定義折線圖
                            break;
                        case 1:
                            dataset = buildDatset(1, titles, lx, ly); // 儲存座標值
                            renderer = buildRenderer(1, colors, styles, true);
                            setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, btdatamax, datamin, datamax, Color.BLACK);// 定義折線圖
                            break;
                        default:
                            dataset = buildDatset(0, titles, lx, ly); // 儲存座標值
                            renderer = buildRenderer(0, colors, styles, true);
                            Toast.makeText(TestActivity.this, "無此感測器訊號資料", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
                break;
        }

        testview=ChartFactory.getLineChartView(context, dataset, renderer);
        // init_tooltip();
        View chart = ChartFactory.getLineChartView(context, dataset, renderer);
        testchart.removeAllViews();
        testchart.addView(testview);
    }
    // 定義折線圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {

        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setLabelsTextSize(18);
        renderer.setAxisTitleTextSize(24);
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK); // X軸線顏色
        renderer.setXLabels(0);
        renderer.setLegendTextSize(24);

        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK); // Y軸線顏色
        renderer.setPointSize(1);

        renderer.setLabelsColor(Color.BLACK); // 設定標籤顏色
        renderer.setMarginsColor(Color.WHITE); // 設定背景顏色
        renderer.setShowGrid(true); // 設定格線
        renderer.setShowGridX(false);

        renderer.setGridColor(Color.BLACK);
        renderer.setShowCustomTextGrid(true);

        renderer.setClickEnabled(false);

      //  renderer.setSelectableBuffer(10);
        renderer.setZoomButtonsVisible(true);//设置可以缩放
     //   renderer.setInScroll(true);
        renderer.setPanEnabled(true, true);
        switch( fileid )
        {
            case 1:
                renderer.addTextLabel(92,"100%");
                renderer.addTextLabel(250,"200%");
                renderer.addTextLabel(380,"300%");
                renderer.setShowLegend(true);
                break;
            case 2:
                renderer.setShowLegend(false);
                renderer.addTextLabel(1," 開始                     ");
                renderer.addTextLabel(23,"         起立時間");
                renderer.addTextLabel(490,"行走時間                                                                 ");
                renderer.addTextLabel(540,"  轉彎時間");
                renderer.addTextLabel(950,"返回座位時間                    ");
                break;
            default:
                switch (fileinmode)
                {
                    case 1:
                        if(Btmode==0) {
                            renderer.setShowLegend(false);
                            renderer.addTextLabel(1, " 開始 ");
                            renderer.addTextLabel(gridindex[0], "         起立時間");
                            renderer.addTextLabel(gridindex[1], "行走時間          ");
                            renderer.addTextLabel(gridindex[2], "  轉彎時間");
                            renderer.addTextLabel(gridindex[3], "返回座位時間 ");
                        }
                        else
                        {
                            renderer.setShowLegend(false);
                            renderer.addTextLabel(1, " 開始 ");
                            renderer.addTextLabel(gridindex[0]*5, "         起立時間");
                            renderer.addTextLabel(gridindex[1]*5, "行走時間          ");
                            renderer.addTextLabel(gridindex[2]*5, "  轉彎時間");
                            renderer.addTextLabel(Btdatasize1, "返回座位時間 ");
                        }
                        break;
                    case 2:
                        String[] per2=new String[]{"L0","L1","L2","L3","L4","L5","L6","L7","L8","L9"};

                        String[] per=new String[]{"0","1","2","3","4","5","6","7","8","9"};
                       // String[] per=new String[]{"100%","100","200%","200","300%","300","400%","400","500%","500"};
                        renderer.setShowLegend(false);
                        renderer.addTextLabel(1, " 開始 ");
                        if(Btmode==0) {
                            for(int i=0;i<10;i++)
                            {
                               // if(i%2==0)
                                    renderer.addTextLabel(walkpoint[i], per[i]);
                            }
                        }
                        else
                        {
                            for(int i=0;i<10;i++)
                            {
                               // if(i%2==0)
                                {
                                    renderer.addTextLabel(mspwalkpoint2[i], per2[i]);Log.v("grid",per[i]+" point "+walkpoint[i]);
                                    renderer.addTextLabel(mspwalkpoint[i], per[i]);Log.v("grid",per[i]+" point "+walkpoint[i]);
                                }
                            }
                        }

                        break;
                }
                break;

        }


    }

    // 定義折線圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int length,int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

       // int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();

            switch(showmode)
            {
                case 1:
                    if(i==0)
                        r.setColor(colors[0]);
                    else
                        r.setColor(colors[1]);
                    break;
                case 2:
                    r.setColor(colors[i]);
                    break;
            }


            r.setPointStyle(styles[0]);
            r.setFillPoints(fill);

            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }

        return renderer;
    }

    // 資料處理
    private XYMultipleSeriesDataset buildDatset(int length,String[] titles, List<double[]> xValues, List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

      //  int length = titles.length; // 折線數量
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            double[] xV = xValues.get(i); // 獲取第i條線的資料
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // 有幾個點
         //  Log.v("length seriesLength",seriesLength+"");
            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
    public void init_tooltip()
    {
        //================== vie tool ===================================
        final Context contextTemp = context;
       final int toolTipWidth = 74;
        final int toolTipHeight = 51;



        //  toolTipHeight = dip2px(contextTemp, toolTipHeight);
        // toolTipWidth = DensityUtil.dip2px(contextTemp, toolTipWidth);

        LayoutInflater inflater = LayoutInflater.from(contextTemp);
         //layout = inflater.inflate(R.layout.chart_tooltext, null);
        // 获取toolTip中两个TextView,这两个TextView的值会不断变化
       // final TextView distanceTotal = (TextView)layout.findViewById(R.id.distance_total);
        //final TextView calorieTotal = (TextView)layout. findViewById(R.id.calorie_total);
       // testchart.setClickable(true);
       // testview.setClickable(true);
        testview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MotionEvent motionEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, event.getX(), event.getY(),
                        event.getMetaState());
                testview.onTouchEvent(motionEvent);
                testview.setHorizontalScrollBarEnabled(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SeriesSelection seriesSelection = testview.getCurrentSeriesAndPoint();
                        double[] xy = testview.toRealPoint(0);
                        if (seriesSelection != null) {
                            //distanceTotal.setText(dx[seriesSelection.getPointIndex()] + "");
                         //   calorieTotal.setText((int) viewdatax[seriesSelection.getPointIndex()] + "");

                            double[] clickPoint = testview.toRealPoint(0);

                            double xValue = seriesSelection.getXValue();// 基准点的x坐标
                            double yValue = seriesSelection.getValue();// 基准点的y坐标

                            double xPosition = event.getRawX() - event.getX() + dx[1] + ((event.getX() - dx[1]) * xValue / clickPoint[0]);
                            double yPosition = event.getRawY() - event.getY() + dx[0]
                                    + ((event.getY() - dx[0]) * (datamax - yValue) / (datamax - clickPoint[1]));
                            int xOffset = (int) (xPosition - toolTipWidth / 2);
                            // 减去7个dip是为了让poupup和点之间的距离高一点。
                            int yOffset = (int) (yPosition - toolTipHeight);

                            if (popup != null) {
                                popup.dismiss();
                            }
                            popup = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            popup.showAtLocation(testview, Gravity.NO_GRAVITY, xOffset, yOffset);
                            Toast.makeText(
                                    TestActivity.this,
                                    "Chart element in series index " + seriesSelection.getSeriesIndex()
                                            + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                                            + " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
                                            + " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(TestActivity.this, "click!!!", Toast.LENGTH_SHORT).show();
                            if (popup != null) {
                                popup.dismiss();
                            }
                            //  testview.setBottom(0);
                          /*  testview.setTop(20);
                            testview.setLeft(20);
                            testview.setRight(0);*/
                            //testview.repaint();
                        }

                        break;
                    case MotionEvent.ACTION_SCROLL:
                        Toast.makeText(TestActivity.this, "scroll", Toast.LENGTH_SHORT).show();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Toast.makeText(TestActivity.this, "move", Toast.LENGTH_SHORT).show();
                        Log.v("action", "move");
                        break;
                }

                // SeriesSelection seriesSelection = ((RelativeLayout) v);

                //当点击的位置是对应某一个点时，开始获取该点处的数据，并且弹出PopupWindow.
                  /* if (seriesSelection != null) {
                        distanceTotal.setText(seriesSelection.getValue() + "");
                        calorieTotal.setText((int) carolies[seriesSelection.getPointIndex()] + "");
                        Log.i("data", seriesSelection.getValue() + " " + carolies[seriesSelection.getPointIndex()]);
                        //以下代码是为了计算tooltip弹出的位置。
                        // 实际点击处的x,y坐标
                        double[] clickPoint = chartView.toRealPoint(0);

                        double xValue = seriesSelection.getXValue();// 基准点的x坐标
                        double yValue = seriesSelection.getValue();// 基准点的y坐标

                        double xPosition = event.getRawX() - event.getX() + margin[1] + ((event.getX() - margin[1]) * xValue / clickPoint[0]);
                        double yPosition = event.getRawY() - event.getY() + margin[0]
                                + ((event.getY() - margin[0]) * (renderer.getYAxisMax() - yValue) / (renderer.getYAxisMax() - clickPoint[1]));
                        int xOffset = (int) (xPosition - toolTipWidth / 2);
                        // 减去7个dip是为了让poupup和点之间的距离高一点。
                        int yOffset = (int) (yPosition - toolTipHeight - DensityUtil.dip2px(contextTemp, 7));
                        initPopupWindow(contextTemp);

                        popup.showAtLocation(viewParent, Gravity.NO_GRAVITY, xOffset, yOffset);

                    } else { //当点击的位置不是图表上折点的位置时，如果上一次点击弹出的popup还存在，就把它dismiss掉。
                        if (popup != null) {
                            popup.dismiss();
                        }
                    }*/


                return true;
            }
        });
    }
    private void writetxtdata() {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();

            Log.v("filewriter ", txtdata);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH_mm");
            Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
            String filename;
            filename = formatter.format(curDate);
            Log.v("mainmode",mainmode+"");

            switch (mainmode) {
                case 1:
                    filename = "起立行走測驗"+filename ;//+ filename;
                    break;
                case 2:
                    filename = "10公尺走路測驗" + filename;
                    break;
            }
            viewfilename=filename;
            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/");
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdirs();

            }
            Log.v("filewriter ", "1");

            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/"+filename  + ".txt",false);
            Log.v("filewriter ", "2");

            mfilewriter.write(txtdata);
            Log.v("filewriter ", "3");

            mfilewriter.close();

            //MainActivity.filelist.add(filename);
            Log.v("filewriter ", "4");

            Log.v("filename", filename);

        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }

    }
    private void readdata(String filename)
    {

        try
        {
            //取得SD卡儲存路徑
            File mSDFile = Environment.getExternalStorageDirectory();
            //讀取文件檔路徑
            File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");
            if(file.exists()) {
                Log.v("file","exist");

                FileReader mFileReader = new FileReader(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");


                BufferedReader mBufferedReader = new BufferedReader(mFileReader);
                String mReadText = "";
                String mTextLine = mBufferedReader.readLine();


                //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出

                //============================================================
                // r , t , w , l , s , p , x
                int index = 0,mspindex1=0,mspindex2=0,pointindex=0,peakindex=0,peakindex1=0;
                viewdata_index=0;
                Queue peakqueue = new LinkedList(),peakqueu2 = new LinkedList();

                while (mTextLine != null) {
                    //  Log.v("readline",mTextLine);
                    switch (mTextLine.charAt(0)) {
                        case 'r':
                            answer="步態對稱性係數 : "+String.format("%.6f", CCnorm())+answer;
                            break;
                        case 't':
                            answer += mTextLine.substring(1) + "\n";
                            break;
                        case 'w':
                            answer += "步頻" + mTextLine.substring(1);
                            break;
                        case 'l':
                            answer += "\n跨步長 " + mTextLine.substring(1)+" cm";
                            break;
                        case 's':
                            answer += "\n走路速度 " + mTextLine.substring(1)+" cm/sec";
                            break;
                        case 'p':
                            Log.v("walkpoint", mTextLine.substring(1));
                            walkpoint[pointindex] = Integer.valueOf(mTextLine.substring(1));
                            pointindex++;
                            break;
                        case 'E':
                            gridindex[index]= Integer.valueOf(mTextLine.substring(1));
                            index++;
                            Log.v("gridindex", gridindex[index-1]+mTextLine.substring(1));

                            break;
                        case 'B':
                            Btmode=1;
                            break;
                        case 'm':
                           // mspdatam[mspindex]=Double.valueOf(mTextLine.substring(1));
                          /*  if(q.size()<=30)
                                q.add(Double.valueOf(mTextLine.substring(1)));
                            mspdatam[index]=average(q);
                            if(q.size()>30)
                                q.remove();
                            Log.v("Queue",q.size()+"");*/
                            mspdatam1[mspindex1]=Double.valueOf(mTextLine.substring(1));
                            if(mspindex1>30)
                            peakqueue.add(mspdatam1[mspindex1]);
                            if(peakqueue.size()>=21 &&mspindex1>30)
                            {
                                boolean ispeak=true;
                                ArrayList arraylist = new ArrayList(peakqueue);
                                for(int s=1;s<=10;s++)
                                {
                                    if((double)arraylist.get(10+s)<(double)arraylist.get(10)||(double)arraylist.get(10-s)<(double)arraylist.get(10))
                                    {
                                        ispeak=false;
                                        break;
                                    }

                                }
                                peakqueue.poll();
                                if(ispeak && Math.abs((double)arraylist.get(10) - (double)arraylist.get(0))>0.3) {
                                    mspwalkpoint[peakindex]=mspindex1-11;
                                    peakindex++;
                                    Log.v("ispeak1", arraylist.get(10) + " index is " + mspindex1);
                                }
                            }
                            mspindex1++;
                            break;
                        case 'n':
                            // mspdatam[mspindex]=Double.valueOf(mTextLine.substring(1));
                          /*  if(q.size()<=30)
                                q.add(Double.valueOf(mTextLine.substring(1)));
                            mspdatam[index]=average(q);
                            if(q.size()>30)
                                q.remove();
                            Log.v("Queue",q.size()+"");*/
                            mspdatam2[mspindex2]=Double.valueOf(mTextLine.substring(1));
                            if(mspindex2>30)
                            peakqueu2.add(mspdatam2[mspindex2]);
                            if(peakqueu2.size()>=21)
                            {
                                boolean ispeak=true;
                                ArrayList arraylist = new ArrayList(peakqueu2);
                                for(int s=1;s<=10;s++)
                                {
                                    if((double)arraylist.get(10+s)<(double)arraylist.get(10)||(double)arraylist.get(10-s)<(double)arraylist.get(10))
                                    {
                                        ispeak=false;
                                        break;
                                    }

                                }
                                peakqueu2.poll();
                                if(ispeak&& Math.abs((double)arraylist.get(10) - (double)arraylist.get(0))>0.3) {
                                    mspwalkpoint2[peakindex1]=mspindex2-11;
                                    peakindex1++;
                                    Log.v("ispeak2", arraylist.get(10) + " index is " + mspindex2);
                                }
                            }
                            mspindex2++;
                            break;

                        case 'o':
                            viewdata2[viewdata_index2] = Double.valueOf(mTextLine.substring(1));
                            viewdata_index2++;
                            break;
                        default:
                            viewdata[viewdata_index] = Double.valueOf(mTextLine);
                            if (viewdata[viewdata_index] > datamax)
                                datamax = viewdata[viewdata_index];
                            else if (viewdata[viewdata_index] < datamin)
                                datamin = viewdata[viewdata_index];
                            viewdata_index++;
                            break;
                    }
                    //   mReadText += mTextLine+"\n";
                    mTextLine = mBufferedReader.readLine();
                }
                Btdatasize1=mspindex1-1;
                Btdatasize2=mspindex2-1;
                //syncmsp();
                int Max=Btdatasize1<Btdatasize2?Btdatasize2:Btdatasize1,shiftvalue=0;
                ratio=Max/viewdata_index;
                for(int i=0;i<5;i++) {
                    shiftvalue+=Math.abs(mspwalkpoint2[i]-mspwalkpoint[i]);
                    Log.v("mspwalk point 1 ", mspwalkpoint[i]+"" );
                    Log.v("mspwalk point 2 ", mspwalkpoint2[i]+"" );
                    Log.v("mspwalk point dec ", mspwalkpoint2[i]-mspwalkpoint[i]+"" );
                }
                shiftvalue=shiftvalue/5;
                syncmsp(shiftvalue);
                Log.v("mspindex",mspindex1+" "+mspindex2);
                Log.v("viewdata_index",viewdata_index+" ratio : "+ratio);
            }

        }
        catch(Exception e)
        {
            Log.v("readdata error",e.toString());

        }
    }
    public void initial_variables()
    {
//=========boolean=============================



        up1=false;
        iswalked1=false;
        isturn=false;
        motiveState=false;
        timeup = false;
        processState=false;

        //====value======================================
        init_orentation= 0;
        lstValue=0;
        sensorsec=0;
        stepcount=0;
        peak[0]=0;
//=========time===========================
        uptime=0;
        alltime=0;
        sec=0;
        backtime=0;
        turnaroundtime=0;
        walktime=0;

        save =new double[10];
        record =new float[10];
        averagedata=new double[10];
        walkpoint=new int[10];
        sensorManager.unregisterListener(this,accelerometer);
        sensorManager.unregisterListener(this,magntic);
        searchmode=1;

        try {
            if (btreceivedata1.length() > 100)
                decode('m');
            if (btreceivedata2.length() > 100)
                decode('n');
            Log.v("receive data1", btreceivedata1);
            Log.v("receive data2", btreceivedata2);
            btreceivedata1 = "";
            btreceivedata2 = "";
            if (Btmode == 1)
                writetxtdata();
            mainmode = 0;


            if (connect1 != null)
            {  connect1.cancel();connect1.flag=false;}
            if (connect2 != null)
            { connect2.cancel();connect2.flag=false;}
          //  sound.release();
         // sound.play(endsound, 1, 1, 0, 0, 1);
            mediaPlayer.stop();
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(Exception x){ShowAlertMessage(x.toString());}
    }
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }
    public double magnitude2(float x, float y) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y );
        return magnitude;
    }
    private void writetxtdataorigin() {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();


            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP");
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdirs();
            }





            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + viewfilename + ".txt");
            if(fileid==0)
            mfilewriter.write(oridata3stepright);
            else if(fileid==1)
                mfilewriter.write(oridatatTUG);
            mfilewriter.close();
           // MainActivity.filelist.add(filename);
            //Log.v("filename", filename);
            //viewfilename=filename;
        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }

    }
    public void ShowAlertMessage(String message)
    {
        new AlertDialog.Builder(TestActivity.this)
                .setTitle("程式執行發生錯誤")
                .setMessage(message)
                        //.setMessage("選擇一裝置進行配對")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    private void syncmsp(int shiftvalue)
    {
      //  int shift=mspwalkpoint2[0]-mspwalkpoint[0];
        Btdatasize2=Btdatasize2-shiftvalue;
        for(int i=0;i<Btdatasize2-shiftvalue;i++)
        {
            mspdatam2[i]=mspdatam2[i+shiftvalue];
        }
    }
    //====================================== BlueTooth =================================================
    private void Bluetoothopen()
    {
       List<String> BTpair;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
        {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
        BTpair = new ArrayList<>();

        if (!mBluetoothAdapter.isEnabled()) {

            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            new AlertDialog.Builder(TestActivity.this)
                    .setTitle("藍芽尚未開啟")
                    .setMessage("Open bluetooth")
                    .setPositiveButton("開啟藍芽", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult( enableIntent,1);
                            mmDevice=mBluetoothAdapter.getRemoteDevice("20:15:09:01:89:93");
                            nnDevice=mBluetoothAdapter.getRemoteDevice("20:16:04:11:03:13");
                            try {
                                socketConnect();
                            }
                            catch (Exception e)
                            {Log.v("FindBT",e.toString());}

                        }
                    })
                    .show();
// Otherwise, setup the chat session
        }else {
            //  btview.setText("Bluttooth 已開啟");
            mmDevice=mBluetoothAdapter.getRemoteDevice("20:15:09:01:89:93");
            nnDevice=mBluetoothAdapter.getRemoteDevice("20:16:04:11:03:13");
            try {
                socketConnect();
            }
            catch (Exception e)
            {Log.v("FindBT",e.toString());}
        }


    }
    private void socketConnect()
    {
        String s = "d";
        boolean btconnectgood1=true,btconnectgood2=true;
        try {
            Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
            mmSocket.connect();
            Log.v("OpenBT", "mmsocket");
            mmOutputStream = mmSocket.getOutputStream();
            connect1 = new ConnectedThread(mmSocket);
            connect1.start();
             s = "d";
            mmOutputStream.write(s.getBytes());
        }
        catch (Exception x)
        {
            btconnectgood1=false;

        }
        try {
            Method mm = nnDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            nnSocket = (BluetoothSocket) mm.invoke(nnDevice, 1);
            nnSocket.connect();
            Log.v("OpenBT", "nnsocket");
            nnOutputStream = nnSocket.getOutputStream();
            connect2 = new ConnectedThread(nnSocket);
            connect2.start();

            s = "x";
            nnOutputStream.write(s.getBytes());
        }
        catch (Exception x)
        {
            btconnectgood2=false;
        }
        if(!btconnectgood1 || !btconnectgood2)
        {
            if(!btconnectgood1 && !btconnectgood2)
            Toast.makeText(TestActivity.this, "無穿戴式裝置模式", Toast.LENGTH_SHORT).show();
            else if(!btconnectgood1)
            Toast.makeText(TestActivity.this, "無穿戴式裝置模式1", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(TestActivity.this, "無穿戴式裝置模式2", Toast.LENGTH_SHORT).show();


        }
        else
        {
            Btmode=1;
            txtdata+="B\n";
        }


    }
    public Runnable openBT=new Runnable() {
        @Override
        public void run() {
            Log.v("OpenBT", "socketcreat");
            if (mmDevice != null) {
                try {

                    // mmSocket = mmDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SPP_UUID);
                    Log.v("OpenBT", "socketcontect");
                    Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                    mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
                    mmSocket.connect();

                    if (mmSocket.isConnected()) {
                        Log.v("Bluetooth","成功連接 ");
                        //lksend_Handler.postDelayed(begin_send_lk, 5000);

                    }
                    else
                        Log.v("Bluetooth", "連接失敗 ");
                    Log.v("Bluetooth", "與 " + mmDevice.getName() + " 成功連接");

                    mmOutputStream = mmSocket.getOutputStream();
                               // beginListenForData(); //開始傾聽藍芽裝置的資料

                }
                catch(Exception e)
                {Log.v("OpenBT",e.toString());}
            }
            else
                tryconnect_handler.postDelayed(openBT,1000);
        }

    };
    public class ConnectedThread extends Thread {
        private final BluetoothSocket ttSocket;
        private final InputStream mmInStream;
        private Handler connecthandler;
        byte[] readBuffer;
        private boolean flag=true;
        public ConnectedThread(BluetoothSocket socket) {
            Log.d("connect", "create ConnectedThread");
            ttSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {
                Log.e("connect", "temp sockets not created", e);
            }

            mmInStream = tmpIn;


        }
        int bytes =0;
        public void run() {
            Log.i("connect", "BEGIN mConnectedThread");


            // Keep listening to the InputStream while connected
            while (flag) {
                try {
                    // Read from the InputStream
                      if(ttSocket.isConnected())
                    bytes= mmInStream.available();
                    //  byte[] buffer = new byte[bytes];
                    //  Log.i(TAG, "mmInStream.available()"+mmInStream.available() );
                    readBuffer = new byte[bytes];
                    if (bytes > 0) {
                        bytes = mmInStream.read(readBuffer);
                        final String str =  new String(readBuffer);;

                        if(ttSocket.getRemoteDevice()==mmSocket.getRemoteDevice())
                        { btreceivedata1=btreceivedata1+str;
                            //Log.v("data","1"+str+" device"+ttSocket.getRemoteDevice());
                        }
                        else
                        { btreceivedata2=btreceivedata2+str;
                            //Log.v("data","2"+str+" device"+ttSocket.getRemoteDevice());
                        }
                        //final  String str= new String(readBuffer, 0, readBuffer.length, "ASCII")+"\r";
                       /* if(alldata[stringindex].length()>1020) {
                            alldata[stringindex] = alldata[stringindex] + "\n";
                            Log.v("srtingindex",stringindex+"");
                        }*/

                        /*runOnUiThread(new Runnable() {
                            public void run() {

                                //maintext.setText(str+"___"+readBuffer.toString());
                                // Log.v("receivedd",str+"___"+readBuffer.toString());
                            }

                        });*/




                        // Send the obtained bytes to the UI Activity
                        //  }
                    }
                } catch (IOException e) {
                    Log.e("connect", "disconnected", e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //  overstep();
                            //btview.setText("at connectthread Disconnect!!");
                        }
                    });
                    break;
                }
            }
        }
        public void cancel() {
            try {
                ttSocket.close();

            } catch (IOException e) {
            }
        }
    }
    private void btwrite(char device,String data)
    {
        try {
            switch(device)
            {
                case 'm':
                     mmOutputStream.write(data.getBytes());
                     break;
                case 'n':
                    nnOutputStream.write(data.getBytes());
                    break;
                default:

                    break;
            }
        }
        catch (Exception x)
        {
            Log.v("OutputStream",x.toString());
        }
    }
    public void decode(char device)
    {
        boolean start=false;
        String xx="",yy="",zz="",receivedata="";
        int index=0,number=0,length=0;
        double denumber=0,x,y,z;
        double[] save=new double[30];
        Queue q = new LinkedList(),peakqueue=new LinkedList();

        switch(device)
        {
            case 'm':
                length=btreceivedata1.length();
                receivedata=btreceivedata1;
                break;
            case 'n':
                length=btreceivedata2.length();
                receivedata=btreceivedata2;
                break;

        }
        // for(int x=0;x<=stringindex;x++)
        Log.v("String length alldata",length+"");

        {
            for(int i=0;length-i>=50&&length>12;)
            {
                if(!start)
                {
                    if(receivedata.charAt(i)=='a')
                        start=true;
                    i++;

                }
                else if( length-i>=50)
                {
                    for(int d=0;d<4;d++)
                    {
                        xx+=receivedata.charAt(i);
                        i=i+1;
                    }
                    number=Integer.parseInt( xx, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    x=(float)denumber;

                    for(int d=0;d<4;d++)
                    {
                        yy+=receivedata.charAt(i);
                        i=i+1;
                    }
                    number=Integer.parseInt( yy, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    y=(float)denumber;

                    for(int d=0;d<4;d++)
                    {
                        zz+=receivedata.charAt(i);
                        i=i+1;
                    }
                    number=Integer.parseInt( zz, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    z=(float)denumber;

                    //mspdatam[index]=(float)magnitude((float) x, (float)y,(float)z);
                /*    if(q.size()<=30)
                    q.add(magnitude((float) x, (float)y,(float)z));
                    mspdatam[index]=average(q);
                    if(q.size()>30)
                    q.remove();*/

                  //  Log.v("receivedata "+device,(float)magnitude((float) x, (float)y,(float)z)+"");
                    if(q.size()<30) {
                        switch(device)
                        {
                            case 'm':
                                mspdatam1[index]=(float)magnitude((float) x, (float)y,(float)z);
                                txtdata+="m"+mspdatam1[index]+"\n";
                                q.add(mspdatam1[index]);
                                break;
                            case 'n':
                                mspdatam2[index]=(float)magnitude((float) x, (float)y,(float)z);
                                txtdata+="n"+mspdatam2[index]+"\n";
                                q.add(mspdatam2[index]);
                                break;

                        }
                    }
                    else {
                        q.add(magnitude((float) x, (float) y, (float) z));
                        switch(device)
                        {
                            case 'm':
                                mspdatam1[index]=average(q);
                                txtdata+="m"+mspdatam1[index]+"\n";
                                peakqueue.add(mspdatam1[index]);

                                break;
                            case 'n':
                                mspdatam2[index]=average(q);
                                txtdata+="n"+mspdatam2[index]+"\n";
                                peakqueue.add(mspdatam1[index]);
                                break;

                        }
                        q.poll();
                        //Log.v("queue "+device,average(q)+"");

                    }
                    //x=average(q);
                    if(peakqueue.size()>=21)
                    {
                        boolean ispeak=true;
                        ArrayList arraylist = new ArrayList(peakqueue);
                        for(int s=1;s<=10;s++)
                        {
                            if((double)arraylist.get(10+s)>(double)arraylist.get(10)&&(double)arraylist.get(10-s)>(double)arraylist.get(10))
                            {
                                ispeak=false;
                                break;
                            }

                        }
                        peakqueue.poll();
                        Log.v("ispeak",arraylist.get(10)+"");
                    }
                    index++;
                    xx="";yy="";zz="";

                }
            }

        }
        Log.v("decode index", index + "");
       // Log.v("txtdata",txtdata);
        q.clear();

        //initial_variables();

    }
    //=================================================================================================
    private double CCnorm()
    {
        //int Uflong=(walkpoint[6]-walkpoint[2])*5,Aflong=(walkpoint[7]-walkpoint[3])*ratio,ccsize;\
        Log.v("ccsize", "mspwalkpoint[6]"+ mspwalkpoint[4]+" "+mspwalkpoint[2]+ "mspwalkpoint2[6]"+ mspwalkpoint2[4]+" "+mspwalkpoint2[2]);

        int Uflong=(mspwalkpoint[2]-mspwalkpoint[1]),Aflong=(mspwalkpoint2[2]-mspwalkpoint2[1]),ccsize;
        ccsize=Uflong>Aflong?Uflong:Aflong;
        Log.v("ccsize","ccsize"+ccsize+" walkpoint3 : "+walkpoint[6]+" walkpoint");
        double answer=0,averUF=0,averAF=0;
        double[] aUF=new double[ccsize+1],aAF=new double[ccsize+1],cc=new double[ccsize+1];
        for(int i=0;i<ccsize;i++)
        {
            aAF[i+1]=mspdatam2[mspwalkpoint2[1]+i];
            averAF+= aAF[i+1];
            aUF[i+1]=mspdatam1[mspwalkpoint[1]+i];
            averUF+=aUF[i+1];
        }
        averAF=averAF/ccsize;
        averUF=averUF/ccsize;
        Log.v("average","a "+averAF+" u "+averUF);
        double Max=0,sum=0,a=0,u=0;
        for(int k=1;k<=ccsize;k++) {
            sum+=(aAF[k]-averAF)*(aUF[k]-averUF);
            a+=(aAF[k]-averAF)*(aAF[k]-averAF);
            u+=(aUF[k]-averUF)*(aUF[k]-averUF);
        }
        double AcUF=0,AcAF=0,sumU=0;
       /* for(int n=1;n<=ccsize;n++)
        {
            if (n - 0 <= 0 || n - 0 > ccsize)
            { //Log.v("n-k",n-k+"");
                aAF[n] = 0;
                aUF[n]=0;
                a=0;}
            else
            {a=aAF[n - 0];u=aAF[n];}
            sum += aAF[n] * aAF[n];
            sumU+=aUF[n]*aUF[n];
        }*/
        AcUF=sumU;AcAF=sum;
        answer=sum/(Math.sqrt(a*u));
        Log.v("CC", answer + "    Max " + sum + "   AcUF" + u + "    AcAF" + a);
       /* double Max=0,sum=0,a=0,u=0;
        for(int k=0;k<ccsize;k++) {

            for (int n = 1; n <= ccsize; n++) {

                if (n - k <= 0 || n - k > ccsize)
                { //Log.v("n-k",n-k+"");
               // aAF[n] = 0;
                  //  aUF[n] = 0;
                    ;
                  //  a=0;
                }
                else
                {a=aAF[n - k];u=aUF[n];}
                sum += a * u;
                a=0;u=0;
            }
            cc[k]=sum;
            if (cc[k] > Max)
                Max = cc[k];
            sum=0;

        }
        double AcUF=0,AcAF=0,sumU=0;
        for(int n=1;n<=ccsize;n++)
        {
            if (n - 0 <= 0 || n - 0 > ccsize)
            { //Log.v("n-k",n-k+"");
                 aAF[n] = 0;
                aUF[n]=0;
                a=0;}
            else
            {a=aAF[n - 0];u=aAF[n];}
            sum += aAF[n] * aAF[n];
            sumU+=aUF[n]*aUF[n];
        }
        AcUF=sumU;AcAF=sum;
        answer=Max/(Math.sqrt(AcUF*AcAF));
        Log.v("CC", answer + "    Max " + Max + "   AcUF" + AcUF + "    AcAF" + AcAF);*/

        return answer;
    }
    public double average(Queue que)
    {
        double ave=0;
        ArrayList arraylist = new ArrayList(que);

        double[] d=new double[30];

        for(int x=0;x<30;x++)
        {
           // Log.v("average",arraylist.get(x)+"");
            ave+=(double)arraylist.get(x);
        }
        //Log.v("queue","ave sum"+ave/100);

        //Log.v("average",ave/30+"");
        return ave/30.0;
    }
  private String oridata3stepright="l122\n"+
          "s96\n"+
          "6.79904438783371\n" +
          "6.86064909924406\n" +
          "6.90540987279096\n" +
          "6.93506019833850\n" +
          "6.95929739894187\n" +
          "7.00290062247783\n" +
          "7.02469081766829\n" +
          "7.07296181063080\n" +
          "7.11442808924089\n" +
          "7.16967570149622\n" +
          "7.21121421131243\n" +
          "7.25764825520633\n" +
          "7.30323497671567\n" +
          "7.36738822149374\n" +
          "7.41370022463268\n" +
          "7.46886715619014\n" +
          "7.51691392957603\n" +
          "7.56808490778629\n" +
          "7.60623306559522\n" +
          "7.67001502426406\n" +
          "7.72748521095942\n" +
          "7.82762277865854\n" +
          "7.91678261626640\n" +
          "8.01067238105467\n" +
          "8.06676353062058\n" +
          "8.09489494329272\n" +
          "8.08920235864650\n" +
          "8.04395448050344\n" +
          "7.96992580654914\n" +
          "7.89513249298157\n" +
          "7.81137916029627\n" +
          "7.70809516843773\n" +
          "7.58312321642210\n" +
          "7.44723800497706\n" +
          "7.25528575508916\n" +
          "7.08011557687636\n" +
          "6.89119626725051\n" +
          "6.71936764732001\n" +
          "6.53768870562012\n" +
          "6.36662531614413\n" +
          "6.18103265708991\n" +
          "5.98414589004217\n" +
          "5.78744549325091\n" +
          "5.58265116340151\n" +
          "5.36610488684634\n" +
          "5.16193423370071\n" +
          "4.99382813433376\n" +
          "4.84558580819132\n" +
          "4.70927759028934\n" +
          "4.63547294845956\n" +
          "4.49579269578505\n" +
          "4.34686144807021\n" +
          "4.17635842175663\n" +
          "4.03433064814534\n" +
          "3.92066926406320\n" +
          "3.85280942446990\n" +
          "3.83292106204458\n" +
          "3.86820610998987\n" +
          "3.93973250604480\n" +
          "4.05810729868769\n" +
          "4.22910609747554\n" +
          "4.39649369126964\n" +
          "4.62519612369338\n" +
          "4.89412042269212\n" +
          "5.16886856849281\n" +
          "5.57267671448084\n" +
          "5.97613251469231\n" +
          "6.41797544995720\n" +
          "6.85898286825414\n" +
          "7.30416804328448\n" +
          "7.74612590262147\n" +
          "8.16713028614963\n" +
          "8.56160794513607\n" +
          "8.94875828529935\n" +
          "9.29973642431323\n" +
          "9.60552962772545\n" +
          "9.92543620472439\n" +
          "10.0497985782274\n" +
          "10.5192311479199\n" +
          "10.8761739899041\n" +
          "11.0514907301917\n" +
          "11.2339542528358\n" +
          "11.2776469339093\n" +
          "11.3468471245014\n" +
          "11.5281262517898\n" +
          "11.6899429212845\n" +
          "11.8494051291153\n" +
          "11.8977499529837\n" +
          "11.8663580260438\n" +
          "11.7582118768229\n" +
          "11.6220222205009\n" +
          "11.4520489602383\n" +
          "11.4233750705575\n" +
          "10.9222003316750\n" +
          "10.8476956039312\n" +
          "10.7661842326925\n" +
          "10.4878244821766\n" +
          "10.3042263277818\n" +
          "9.94886422048250\n" +
          "9.46726800908196\n" +
          "9.13024361863014\n" +
          "8.80867350971589\n" +
          "8.60819961782653\n" +
          "8.39949846723837\n" +
          "8.21740269830451\n" +
          "7.98798110754770\n" +
          "7.76570548191807\n" +
          "7.54938464982414\n" +
          "7.45645929363802\n" +
          "7.05534168327007\n" +
          "6.77834144153019\n" +
          "6.69036241678079\n" +
          "6.55654674541713\n" +
          "6.57412869633535\n" +
          "6.55974037888367\n" +
          "6.39250893703987\n" +
          "6.23584412157017\n" +
          "6.07943675835270\n" +
          "6.01055450144901\n" +
          "6.01007107051459\n" +
          "6.08038711004419\n" +
          "6.14645316020368\n" +
          "6.19612515950112\n" +
          "6.21848168522013\n" +
          "6.19344599491794\n" +
          "6.15167696155304\n" +
          "6.11785782714445\n" +
          "6.14631261407430\n" +
          "6.16417678440769\n" +
          "6.20700705911647\n" +
          "6.24785624909596\n" +
          "6.26272713435209\n" +
          "6.26240623269506\n" +
          "6.25247012482100\n" +
          "6.23126960817006\n" +
          "6.20867649001992\n" +
          "6.20795687201436\n" +
          "6.21931420083791\n" +
          "6.22127769477615\n" +
          "6.22822058142344\n" +
          "6.23756989884692\n" +
          "6.23477663631480\n" +
          "6.22405664767905\n" +
          "6.20317109448271\n" +
          "6.19388823641925\n" +
          "6.20873150690607\n" +
          "6.23027365721049\n" +
          "6.24712338319178\n" +
          "6.28114173495385\n" +
          "6.31281747270404\n" +
          "6.34745590375781\n" +
          "6.36980944462359\n" +
          "6.39625279241677\n" +
          "6.42739973346439\n" +
          "6.44895260447435\n" +
          "6.47851823472140\n" +
          "6.53292288189136\n" +
          "6.56682371666374\n" +
          "6.60806703278094\n" +
          "6.65336532712011\n" +
          "6.70769391401190\n" +
          "6.75160151187589\n" +
          "6.81951313010745\n" +
          "6.87601189099666\n" +
          "6.96669183873608\n" +
          "7.06378868066145\n" +
          "7.17650022936324\n" +
          "7.29780257057599\n" +
          "7.42572667082687\n" +
          "7.55529970682418\n" +
          "7.67045144971285\n" +
          "7.76825113702238\n" +
          "7.89537633455142\n" +
          "7.98372939954008\n" +
          "8.06586194566336\n" +
          "8.14186873131563\n" +
          "8.22598372718838\n" +
          "8.27814698295308\n" +
          "8.33608372764507\n" +
          "8.36786451617180\n" +
          "8.41210360324103\n" +
          "8.49509951723321\n" +
          "8.59642941004454\n" +
          "8.69557219182162\n" +
          "8.75941114723130\n" +
          "8.77963309241263\n" +
          "8.71598550370691\n" +
          "8.57719969045293\n" +
          "8.45077115062157\n" +
          "8.29884747353682\n" +
          "8.13410885779396\n" +
          "7.97898192915443\n" +
          "7.84313591442979\n" +
          "7.68858787249779\n" +
          "7.50486538661445\n" +
          "7.27168435023801\n" +
          "6.99145969801031\n" +
          "6.71514726608229\n" +
          "6.45101285909697\n" +
          "6.21775966026875\n" +
          "5.98937714269697\n" +
          "5.80826198658289\n" +
          "5.62640327203053\n" +
          "5.42189394005141\n" +
          "5.22886604223934\n" +
          "5.05693405130800\n" +
          "4.87558309670267\n" +
          "4.70293534348709\n" +
          "4.58166928261244\n" +
          "4.49500454594481\n" +
          "4.42216884207986\n" +
          "4.35264239184697\n" +
          "4.28481051566896\n" +
          "4.21163430627192\n" +
          "4.19783731465864\n" +
          "4.26172045338655\n" +
          "4.43356318001190\n" +
          "4.74586230492901\n" +
          "5.16148632141880\n" +
          "5.64286299565444\n" +
          "6.16296906331100\n" +
          "6.72176184406364\n" +
          "7.25155322550248\n" +
          "7.69944215133259\n" +
          "8.13862777587859\n" +
          "8.57632420616499\n" +
          "8.99818555418761\n" +
          "9.42785702193383\n" +
          "9.87170932522522\n" +
          "10.2655301969978\n" +
          "10.6744229045705\n" +
          "10.8281127336510\n" +
          "11.5209324682949\n" +
          "12.0188361435562\n" +
          "12.2597994781799\n" +
          "12.3196098935142\n" +
          "12.4591068654979\n" +
          "12.6132102129039\n" +
          "12.7577709532229\n" +
          "12.9952884800004\n" +
          "13.3411493932310\n" +
          "13.6023425559936\n" +
          "13.6465583743548\n" +
          "13.6166286645439\n" +
          "13.5349020263367\n" +
          "13.3373583338537\n" +
          "13.3471178725430\n" +
          "12.5724566059066\n" +
          "12.4892790605877\n" +
          "12.4129108155034\n" +
          "12.2379531858008\n" +
          "11.7693079198860\n" +
          "11.3597365954352\n" +
          "10.9620418183583\n" +
          "10.4249026278063\n" +
          "9.93418988089524\n" +
          "9.67663459909236\n" +
          "9.53699380511127\n" +
          "9.28273483791879\n" +
          "9.00771749897808\n" +
          "8.75817716681443\n" +
          "8.43055656440654\n" +
          "8.30847506415323\n" +
          "7.67984506474999\n" +
          "7.25929641741085\n" +
          "7.06432290929329\n" +
          "7.07060503569714\n" +
          "7.00089322894418\n" +
          "6.96775412347318\n" +
          "6.95548095776065\n" +
          "6.79848077450479\n" +
          "6.49907462416832\n" +
          "6.27076146602714\n" +
          "6.20790015213656\n" +
          "6.20844285553253\n" +
          "6.24482659567425\n" +
          "6.33478697417152\n" +
          "6.38735387939570\n" +
          "6.38176524732296\n" +
          "6.36480839542269\n" +
          "6.34965351237852\n" +
          "6.31452664002639\n" +
          "6.29779632760922\n" +
          "6.29162833405667\n" +
          "6.30970365355017\n" +
          "6.31974941615332\n" +
          "6.33787513019267\n" +
          "6.34761976823804\n" +
          "6.34618413559447\n" +
          "6.36601101025080\n" +
          "6.38957185391203\n" +
          "6.41253789893142\n" +
          "6.43669866444365\n" +
          "6.45673470775852\n" +
          "6.47669088185459\n" +
          "6.51079580143446\n" +
          "6.54231020174750\n" +
          "6.59346913980239\n" +
          "6.63976142570172\n" +
          "6.69285168547328\n" +
          "6.74934171346905\n" +
          "6.78106986536178\n" +
          "6.80953382729842\n" +
          "6.86824520839809\n" +
          "6.92137990306645\n" +
          "7.01090449169880\n" +
          "7.12656722922157\n" +
          "7.25387939309897\n" +
          "7.40877301523820\n" +
          "7.56892717240525\n" +
          "7.74632812625935\n" +
          "7.92877113255441\n" +
          "8.08906439218295\n" +
          "8.22769231107238\n" +
          "8.32729642097831\n" +
          "8.40259436283693\n" +
          "8.46748759602912\n" +
          "8.51916853058060\n" +
          "8.55788933124154\n" +
          "8.61978265155288\n" +
          "8.71958522236835\n" +
          "8.79385732640508\n" +
          "8.88486598122160\n" +
          "8.95275428512748\n" +
          "8.94666240887186\n" +
          "8.84538560128683\n" +
          "8.71996030454808\n" +
          "8.54266172098177\n" +
          "8.37824443858600\n" +
          "8.20816734482862\n" +
          "8.01793927323465\n" +
          "7.84312987781825\n" +
          "7.66602886631068\n" +
          "7.49931395647795\n" +
          "7.29676430388745\n" +
          "7.02702220714458\n" +
          "6.80234289659305\n" +
          "6.53940621034758\n" +
          "6.29058727393068\n" +
          "6.08399609433583\n" +
          "5.84664598884297\n" +
          "5.55960889653268\n" +
          "5.31641209346585\n" +
          "5.04631456105217\n" +
          "4.80870270842217\n" +
          "4.61838499786839\n" +
          "4.42500176772743\n" +
          "4.27289818298103\n" +
          "4.13048152753966\n" +
          "4.04270266866439\n" +
          "3.98161375190799\n" +
          "3.87186176639734\n" +
          "3.80653650715072\n" +
          "3.78790078244609\n" +
          "3.82274654817260\n" +
          "4.00777982165424\n" +
          "4.28401509072512\n" +
          "4.63575751481345\n" +
          "5.09834195990645\n" +
          "5.60718473115451\n" +
          "6.15096170220352\n" +
          "6.73767697062446\n" +
          "7.30626924262672\n" +
          "7.83000468963923\n" +
          "8.26997327217759\n" +
          "8.69066451071673\n" +
          "9.14854102939068\n" +
          "9.61332927689392\n" +
          "10.0584300437370\n" +
          "10.5153582960944\n" +
          "10.8816901445289\n" +
          "11.1755007742665\n" +
          "11.9650328421330\n" +
          "12.3133290052696\n" +
          "12.5472907966745\n" +
          "12.6185276133341\n" +
          "12.6414510071400\n" +
          "12.7535645354939\n" +
          "12.9205515223769\n" +
          "13.1907596195495\n" +
          "13.3407662901787\n" +
          "13.4853204120191\n" +
          "13.5320896096120\n" +
          "13.4843752341460\n" +
          "13.3533067861421\n" +
          "13.2613850700349\n" +
          "13.1845560372528\n" +
          "12.4522167549882\n" +
          "12.5833928407134\n" +
          "12.3692047422640\n" +
          "12.1818747358396\n" +
          "11.8711800924388\n" +
          "11.3903566485854\n" +
          "10.9271383221107\n" +
          "10.4176254361413\n" +
          "10.1248324642225\n" +
          "9.75776558163615\n" +
          "9.50692476734245\n" +
          "9.26522595333231\n" +
          "8.96306426818820\n" +
          "8.61860250955327\n" +
          "8.30234651929875\n" +
          "8.10096437858158\n" +
          "7.41775917785368\n" +
          "7.12010873225535\n" +
          "6.92839359188001\n" +
          "6.89938460390966\n" +
          "6.95971123292528\n" +
          "6.95200587088443\n" +
          "6.89860365390037\n" +
          "6.74829403296177\n" +
          "6.63210598860558\n" +
          "6.47412134509303\n" +
          "6.37203150653266\n" +
          "6.37330663528199\n" +
          "6.40255487877895\n" +
          "6.42793022651496\n" +
          "6.42105707417229\n" +
          "6.39702025261841\n" +
          "6.41094088440602\n" +
          "6.42028517561508\n" +
          "6.42855811169586\n" +
          "6.42612534359977\n" +
          "6.43492997476715\n" +
          "6.47219259381438\n" +
          "6.49446572755183\n" +
          "6.54048097251375\n" +
          "6.54569863388736\n" +
          "6.53781730730904\n" +
          "6.52560199610675\n" +
          "6.53391684214265\n" +
          "6.55807266805456\n" +
          "6.58772984085592\n" +
          "6.62534964421387\n" +
          "6.66735385514291\n" +
          "6.70814869484921\n" +
          "6.73662308370046\n" +
          "6.77441619245494\n" +
          "6.82047609765238\n" +
          "6.85866279771342\n" +
          "6.90421514283477\n" +
          "6.93703326770736\n" +
          "6.99266440036044\n" +
          "7.05143007113132\n" +
          "7.13264603861780\n" +
          "7.23280429032282\n" +
          "7.37332082007894\n" +
          "7.54809960803848\n" +
          "7.72465772189767\n" +
          "7.90713984074492\n" +
          "8.08101272097917\n" +
          "8.22686784582317\n" +
          "8.32081760479746\n" +
          "8.38300796133554\n" +
          "8.42524053850707\n" +
          "8.47802409963325\n" +
          "8.54479025455257\n" +
          "8.62836658292959\n" +
          "8.71540334045059\n" +
          "8.78438813888483\n" +
          "8.85526757398641\n" +
          "8.85037060230192\n" +
          "8.81276292012431\n" +
          "8.72114093983697\n" +
          "8.59045056218602\n" +
          "8.46335871172163\n" +
          "8.32099738614898\n" +
          "8.16849130920213\n" +
          "8.00010343728971\n" +
          "7.82078731744782\n" +
          "7.60767115924302\n" +
          "7.41958356466055\n" +
          "7.21482788668299\n" +
          "7.02163487175540\n" +
          "6.83203436298088\n" +
          "6.61472645529721\n" +
          "6.41753181495160\n" +
          "6.15154699480469\n" +
          "5.88565155056001\n" +
          "5.59415773354261\n" +
          "5.31013706947598\n" +
          "5.05855528375055\n" +
          "4.84188538000787\n" +
          "4.63771042712611\n" +
          "4.47248568316473\n" +
          "4.33601262126282\n" +
          "4.21033997142994\n" +
          "4.09508773514865\n" +
          "3.98334086930702\n" +
          "3.87601048930083\n" +
          "3.81626733156731\n" +
          "3.82641450446209\n" +
          "3.91575129280892\n" +
          "4.07216489474182\n" +
          "4.30448415311547\n" +
          "4.58061045343665\n" +
          "4.94181452852503\n" + //================ LL ===================================================
          "o6.66166032645191\n"+
                  "o6.68717782234355\n"+
                  "o6.73575536236452\n"+
                  "o6.79747342921782\n"+
                  "o6.85987745729871\n"+
                  "o6.96048770602617\n"+
                  "o7.08425814809409\n"+
                  "o7.18444665252525\n"+
                  "o7.34403836302223\n"+
                  "o7.44273900130738\n"+
                  "o7.53734733544677\n"+
                  "o7.60247927659819\n"+
                  "o7.67318567331684\n"+
                  "o7.74218863992825\n"+
                  "o7.78494335364485\n"+
                  "o7.85083661862757\n"+
                  "o7.95284511511308\n"+
                  "o8.02892296961644\n"+
                  "o8.09615845081282\n"+
                  "o8.15134633967484\n"+
                  "o8.16939445104445\n"+
                  "o8.18941283372509\n"+
                  "o8.23331719831205\n"+
                  "o8.21377529599080\n"+
                  "o8.32767986936347\n"+
                  "o8.39621521941713\n"+
                  "o8.42284083912864\n"+
                  "o8.44232370278697\n"+
                  "o8.46916318118528\n"+
                  "o8.47720917240326\n"+
                  "o8.43017226646106\n"+
                  "o8.34731253997315\n"+
                  "o8.26580251953753\n"+
                  "o8.17982219499074\n"+
                  "o8.08586236684475\n"+
                  "o7.96059211626719\n"+
                  "o7.80955722841480\n"+
                  "o7.63987841135582\n"+
                  "o7.46417275223397\n"+
                  "o7.21979674689918\n"+
                  "o7.07165558418492\n"+
                  "o6.99699834577021\n"+
                  "o6.88382134359382\n"+
                  "o6.78973638706423\n"+
                  "o6.72051857120290\n"+
                  "o6.66640351138645\n"+
                  "o6.61345328494404\n"+
                  "o6.56297860794693\n"+
                  "o6.49972909149320\n"+
                  "o6.45418274438967\n"+
                  "o6.44564394215062\n"+
                  "o6.43910563522198\n"+
                  "o6.45958800778575\n"+
                  "o6.50610646006342\n"+
                  "o6.55186683629023\n"+
                  "o6.56188423789619\n"+
                  "o6.57650957953803\n"+
                  "o6.66003603305870\n"+
                  "o6.74482183743484\n"+
                  "o6.83786816276422\n"+
                  "o6.95442816228468\n"+
                  "o7.08734835108509\n"+
                  "o7.23289331930723\n"+
                  "o7.40212084897925\n"+
                  "o7.57629400510542\n"+
                  "o7.75589638232223\n"+
                  "o7.92323162512431\n"+
                  "o8.08178533960517\n"+
                  "o8.23894520534723\n"+
                  "o8.39388331417146\n"+
                  "o8.55305953430143\n"+
                  "o8.70394676983349\n"+
                  "o8.82504220775695\n"+
                  "o8.93246449344454\n"+
                  "o9.03274421669693\n"+
                  "o9.12616204963941\n"+
                  "o9.21411704354164\n"+
                  "o9.28561399131770\n"+
                  "o9.34434944075046\n"+
                  "o9.40084276391533\n"+
                  "o9.46100753084778\n"+
                  "o9.58631275752473\n"+
                  "o9.63252903437468\n"+
                  "o9.67662505342101\n"+
                  "o9.71964958652948\n"+
                  "o9.81460077841269\n"+
                  "o10.0136125181837\n"+
                  "o10.1245971095987\n"+
                  "o10.0486372096759\n"+
                  "o9.82151815972667\n"+
                  "o9.61464688708049\n"+
                  "o9.44476895794964\n"+
                  "o9.30853359995836\n"+
                  "o9.18488705256866\n"+
                  "o9.05747866812466\n"+
                  "o8.89632670468017\n"+
                  "o8.69150338759335\n"+
                  "o8.62398622551003\n"+
                  "o8.50153126312520\n"+
                  "o8.38149445488248\n"+
                  "o8.22828565509662\n"+
                  "o8.03413679773983\n"+
                  "o8.02663575993172\n"+
                  "o8.09611466651538\n"+
                  "o8.13282608643735\n"+
                  "o8.00143044802890\n"+
                  "o7.87988931632589\n"+
                  "o7.76552238167144\n"+
                  "o7.67880785589124\n"+
                  "o7.61674164346449\n"+
                  "o7.56921238158517\n"+
                  "o7.48658913594691\n"+
                  "o7.33952794315759\n"+
                  "o7.24046287330247\n"+
                  "o7.13706931899704\n"+
                  "o7.00965567923657\n"+
                  "o6.82812098359884\n"+
                  "o6.54144897955988\n"+
                  "o6.34089159166841\n"+
                  "o6.30971188582339\n"+
                  "o6.42287332374362\n"+
                  "o6.48286075094757\n"+
                  "o6.52441953530526\n"+
                  "o6.52119484701514\n"+
                  "o6.50457431686585\n"+
                  "o6.50240580995473\n"+
                  "o6.52130288824554\n"+
                  "o6.53686091933141\n"+
                  "o6.54700546651405\n"+
                  "o6.54451381721692\n"+
                  "o6.53850483852511\n"+
                  "o6.53258362912098\n"+
                  "o6.52365135335856\n"+
                  "o6.52665582205912\n"+
                  "o6.52663048308439\n"+
                  "o6.53349735615848\n"+
                  "o6.55986086780361\n"+
                  "o6.55625540287331\n"+
                  "o6.56179807225291\n"+
                  "o6.55516576508320\n"+
                  "o6.55000818456260\n"+
                  "o6.56180981803288\n"+
                  "o6.57818839229336\n"+
                  "o6.60120274491806\n"+
                  "o6.62952043223744\n"+
                  "o6.66549793065842\n"+
                  "o6.69080711732380\n"+
                  "o6.74043946225953\n"+
                  "o6.78093368796564\n"+
                  "o6.83573081393381\n"+
                  "o6.88072065081352\n"+
                  "o6.98817051425476\n"+
                  "o7.15431021439198\n"+
                  "o7.31755277169034\n"+
                  "o7.41461990877841\n"+
                  "o7.53648725538330\n"+
                  "o7.65295579380352\n"+
                  "o7.79706513069831\n"+
                  "o7.93023951921444\n"+
                  "o8.07209935851711\n"+
                  "o8.20410781128573\n"+
                  "o8.37075049231859\n"+
                  "o8.51184118834161\n"+
                  "o8.63700043905860\n"+
                  "o8.75759880307464\n"+
                  "o8.89610168098478\n"+
                  "o8.96628933221109\n"+
                  "o9.01261809508697\n"+
                  "o9.01769970435162\n"+
                  "o9.14720542940250\n"+
                  "o9.22664508399420\n"+
                  "o9.30207873221222\n"+
                  "o9.32189692736426\n"+
                  "o9.36470037897149\n"+
                  "o9.35423405674603\n"+
                  "o9.30752292153668\n"+
                  "o9.25537214302882\n"+
                  "o9.21564029112482\n"+
                  "o9.18794763437246\n"+
                  "o9.05337611727105\n"+
                  "o8.87904900274781\n"+
                  "o8.74537631177122\n"+
                  "o8.60449152963551\n"+
                  "o8.46246081826791\n"+
                  "o8.17404522397625\n"+
                  "o7.92888231000839\n"+
                  "o7.71757940512120\n"+
                  "o7.49931174374963\n"+
                  "o7.24472985731084\n"+
                  "o7.03688141684355\n"+
                  "o6.84094921392197\n"+
                  "o6.60110334708568\n"+
                  "o6.40807071822589\n"+
                  "o6.21711631093500\n"+
                  "o6.18048096797123\n"+
                  "o6.06883247682567\n"+
                  "o5.90701468739661\n"+
                  "o5.76485570522748\n"+
                  "o5.69116020846572\n"+
                  "o5.73440321174306\n"+
                  "o5.75878495812758\n"+
                  "o5.76246485835352\n"+
                  "o5.81435606979896\n"+
                  "o5.90050566399394\n"+
                  "o6.01472364724822\n"+
                  "o6.18472480873093\n"+
                  "o6.38943232662416\n"+
                  "o6.60177766768053\n"+
                  "o6.84799250287609\n"+
                  "o7.07121513244099\n"+
                  "o7.45206652338704\n"+
                  "o7.82272440502109\n"+
                  "o8.17252881498686\n"+
                  "o8.49863643865765\n"+
                  "o8.80330333511773\n"+
                  "o9.09787744470760\n"+
                  "o9.37769144591056\n"+
                  "o9.66903129633205\n"+
                  "o9.96929692097955\n"+
                  "o10.2602311745246\n"+
                  "o10.5114283974030\n"+
                  "o10.7622637084215\n"+
                  "o10.9093508845945\n"+
                  "o10.9934999199901\n"+
                  "o11.0323692555166\n"+
                  "o11.0965569046184\n"+
                  "o11.2526216742565\n"+
                  "o11.6013509764040\n"+
                  "o11.8392748134766\n"+
                  "o11.8537536980236\n"+
                  "o11.7825481508612\n"+
                  "o11.7079472576101\n"+
                  "o11.5461017272520\n"+
                  "o11.4213565857409\n"+
                  "o11.2932243530166\n"+
                  "o11.1869155659316\n"+
                  "o10.9947464659562\n"+
                  "o10.8476118236636\n"+
                  "o10.6730552197809\n"+
                  "o10.4545713804325\n"+
                  "o10.1248684063980\n"+
                  "o9.79330863381343\n"+
                  "o9.31910434643027\n"+
                  "o9.15548771936196\n"+
                  "o9.10176676812302\n"+
                  "o8.92553522711472\n"+
                  "o8.67233073910262\n"+
                  "o8.50487227490277\n"+
                  "o8.23992603554070\n"+
                  "o8.01688174472298\n"+
                  "o7.80365031146136\n"+
                  "o7.65672322261636\n"+
                  "o7.47971512271475\n"+
                  "o7.36599853829080\n"+
                  "o7.31094838148097\n"+
                  "o7.28892512710663\n"+
                  "o7.19906538458309\n"+
                  "o7.04563099042014\n"+
                  "o6.68465800142768\n"+
                  "o6.44327962067736\n"+
                  "o6.40405915800497\n"+
                  "o6.45772568775339\n"+
                  "o6.48109645766227\n"+
                  "o6.53946687127928\n"+
                  "o6.54159372485800\n"+
                  "o6.51407248455198\n"+
                  "o6.49311313213858\n"+
                  "o6.49161945114964\n"+
                  "o6.49714876360914\n"+
                  "o6.51172598416108\n"+
                  "o6.51822828217688\n"+
                  "o6.52692877020802\n"+
                  "o6.53610594811907\n"+
                  "o6.54736844182120\n"+
                  "o6.54636934722013\n"+
                  "o6.56969698342057\n"+
                  "o6.58841920059389\n"+
                  "o6.61703161975433\n"+
                  "o6.64334588982331\n"+
                  "o6.66168230837192\n"+
                  "o6.69044369884766\n"+
                  "o6.71677486209442\n"+
                  "o6.77846139910361\n"+
                  "o6.85867508427731\n"+
                  "o6.87140000642154\n"+
                  "o6.92700846871687\n"+
                  "o6.96744205167454\n"+
                  "o7.03436739476438\n"+
                  "o7.11086150927830\n"+
                  "o7.20241365343432\n"+
                  "o7.33188608194008\n"+
                  "o7.40322317055245\n"+
                  "o7.49895250875056\n"+
                  "o7.59945454166436\n"+
                  "o7.71625555905653\n"+
                  "o7.83577038086956\n"+
                  "o7.95376254681718\n"+
                  "o8.07488905469858\n"+
                  "o8.18086396611656\n"+
                  "o8.36955244443397\n"+
                  "o8.45470615214555\n"+
                  "o8.56687024637068\n"+
                  "o8.64748218680052\n"+
                  "o8.77506270955737\n"+
                  "o8.90710644900889\n"+
                  "o8.93249346613450\n"+
                  "o9.04394327993499\n"+
                  "o9.07198754344472\n"+
                  "o9.21507519668850\n"+
                  "o9.41766931972572\n"+
                  "o9.64122013475618\n"+
                  "o9.75768699055305\n"+
                  "o9.75615312048503\n"+
                  "o9.73529340900768\n"+
                  "o9.66719922861091\n"+
                  "o9.63029597861866\n"+
                  "o9.58874717070873\n"+
                  "o9.49821367663033\n"+
                  "o9.33093678663729\n"+
                  "o9.12161716133982\n"+
                  "o8.97039936055525\n"+
                  "o8.72321303530230\n"+
                  "o8.51348400314621\n"+
                  "o8.20614012506368\n"+
                  "o7.92091712475361\n"+
                  "o7.67599494851201\n"+
                  "o7.54473180305015\n"+
                  "o7.39241801748856\n"+
                  "o7.16839450133405\n"+
                  "o6.94703935398774\n"+
                  "o6.75610251237586\n"+
                  "o6.54380549809625\n"+
                  "o6.39972545517931\n"+
                  "o6.26010073806663\n"+
                  "o6.13840249042871\n"+
                  "o5.98936204208185\n"+
                  "o5.93331437493971\n"+
                  "o5.91159739681237\n"+
                  "o5.90693832256172\n"+
                  "o5.86289509125731\n"+
                  "o5.77103872132880\n"+
                  "o5.69978861784135\n"+
                  "o5.77522803781324\n"+
                  "o5.94537928545706\n"+
                  "o6.18426729900575\n"+
                  "o6.47071875143858\n"+
                  "o6.79451231579633\n"+
                  "o7.12283736526381\n"+
                  "o7.45139355849696\n"+
                  "o7.84751816841232\n"+
                  "o8.28918576443457\n"+
                  "o8.70815520274447\n"+
                  "o9.10812035327900\n"+
                  "o9.44114709373740\n"+
                  "o9.74265655838001\n"+
                  "o10.0527661954764\n"+
                  "o10.3508711222119\n"+
                  "o10.6389371542630\n"+
                  "o10.8925315916859\n"+
                  "o11.2173668988879\n"+
                  "o11.5561819413611\n"+
                  "o11.6881115794159\n"+
                  "o11.7551622559951\n"+
                  "o11.8549546003777\n"+
                  "o11.9895294790130\n"+
                  "o12.3109012318842\n"+
                  "o12.6891164144058\n"+
                  "o12.6644869297711\n"+
                  "o12.4808767942726\n"+
                  "o12.3672046044888\n"+
                  "o12.2434491948456\n"+
                  "o12.0674767182025\n"+
                  "o11.9181200726381\n"+
                  "o11.8297001609760\n"+
                  "o11.5421360858545\n"+
                  "o11.2624568227268\n"+
                  "o11.1791962096350\n"+
                  "o10.9067597228513\n"+
                  "o10.5617800168341\n"+
                  "o10.1378009130021\n"+
                  "o9.61541226294980\n"+
                  "o9.19959310012753\n"+
                  "o9.24563126193509\n"+
                  "o9.10726090055421\n"+
                  "o8.73677679489381\n"+
                  "o8.49005194831587\n"+
                  "o8.29186575043455\n"+
                  "o8.02637403325019\n"+
                  "o7.77421574983462\n"+
                  "o7.64916580019721\n"+
                  "o7.43798718770616\n"+
                  "o7.17294780087245\n"+
                  "o7.13695548886259\n"+
                  "o7.13549162256788\n"+
                  "o7.12598043969896\n"+
                  "o7.00921575451896\n"+
                  "o6.71865485067543\n"+
                  "o6.36967510904831\n"+
                  "o6.38979005742275\n"+
                  "o6.54893922524088\n"+
                  "o6.58577338773889\n"+
                  "r";

    String oridatatTUG="t總時間 : 10.57 sec\nt起立時間 : 1.5 sec\nt行走時間 : 3.06 sec \nt轉彎時間 : 1.06sec \nt返回座位時間 : 4.95 sec\n"+
           "6.73572985791241\n" +
            "6.73379183883648\n" +
            "6.72681909088577\n" +
            "6.68909577224970\n" +
            "6.73331624395013\n" +
            "6.76831614745402\n" +
            "6.56435651205458\n" +
            "6.98655954459123\n" +
            "7.06890024019645\n" +
            "7.59354792157795\n" +
            "7.89749775608972\n" +
            "8.58448863304907\n" +
            "7.93981229326516\n" +
            "7.27695079017922\n" +
            "6.26236802652598\n" +
            "5.71963013755228\n" +
            "5.18532151405062\n" +
            "5.40547321773038\n" +
            "5.95297602954937\n" +
            "6.44788199799439\n" +
            "6.57749933319629\n"+
            "6.79904438783371\n" +
            "6.86064909924406\n" +
            "6.90540987279096\n" +
            "6.93506019833850\n" +
            "6.95929739894187\n" +
            "7.00290062247783\n" +
            "7.02469081766829\n" +
            "7.07296181063080\n" +
            "7.11442808924089\n" +
            "7.16967570149622\n" +
            "7.21121421131243\n" +
            "7.25764825520633\n" +
            "7.30323497671567\n" +
            "7.36738822149374\n" +
            "7.41370022463268\n" +
            "7.46886715619014\n" +
            "7.51691392957603\n" +
            "7.56808490778629\n" +
            "7.60623306559522\n" +
            "7.67001502426406\n" +
            "7.72748521095942\n" +
            "7.82762277865854\n" +
            "7.91678261626640\n" +
            "8.01067238105467\n" +
            "8.06676353062058\n" +
            "8.09489494329272\n" +
            "8.08920235864650\n" +
            "8.04395448050344\n" +
            "7.96992580654914\n" +
            "7.89513249298157\n" +
            "7.81137916029627\n" +
            "7.70809516843773\n" +
            "7.58312321642210\n" +
            "7.44723800497706\n" +
            "7.25528575508916\n" +
            "7.08011557687636\n" +
            "6.89119626725051\n" +
            "6.71936764732001\n" +
            "6.53768870562012\n" +
            "6.36662531614413\n" +
            "6.18103265708991\n" +
            "5.98414589004217\n" +
            "5.78744549325091\n" +
            "5.58265116340151\n" +
            "5.36610488684634\n" +
            "5.16193423370071\n" +
            "4.99382813433376\n" +
            "4.84558580819132\n" +
            "4.70927759028934\n" +
            "4.63547294845956\n" +
            "4.49579269578505\n" +
            "4.34686144807021\n" +
            "4.17635842175663\n" +
            "4.03433064814534\n" +
            "3.92066926406320\n" +
            "3.85280942446990\n" +
            "3.83292106204458\n" +
            "3.86820610998987\n" +
            "3.93973250604480\n" +
            "4.05810729868769\n" +
            "4.22910609747554\n" +
            "4.39649369126964\n" +
            "4.62519612369338\n" +
            "4.89412042269212\n" +
            "5.16886856849281\n" +
            "5.57267671448084\n" +
            "5.97613251469231\n" +
            "6.41797544995720\n" +
            "6.85898286825414\n" +
            "7.30416804328448\n" +
            "7.74612590262147\n" +
            "8.16713028614963\n" +
            "8.56160794513607\n" +
            "8.94875828529935\n" +
            "9.29973642431323\n" +
            "9.60552962772545\n" +
            "9.92543620472439\n" +
            "10.0497985782274\n" +
            "10.5192311479199\n" +
            "10.8761739899041\n" +
            "11.0514907301917\n" +
            "11.2339542528358\n" +
            "11.2776469339093\n" +
            "11.3468471245014\n" +
            "11.5281262517898\n" +
            "11.6899429212845\n" +
            "11.8494051291153\n" +
            "11.8977499529837\n" +
            "11.8663580260438\n" +
            "11.7582118768229\n" +
            "11.6220222205009\n" +
            "11.4520489602383\n" +
            "11.4233750705575\n" +
            "10.9222003316750\n" +
            "10.8476956039312\n" +
            "10.7661842326925\n" +
            "10.4878244821766\n" +
            "10.3042263277818\n" +
            "9.94886422048250\n" +
            "9.46726800908196\n" +
            "9.13024361863014\n" +
            "8.80867350971589\n" +
            "8.60819961782653\n" +
            "8.39949846723837\n" +
            "8.21740269830451\n" +
            "7.98798110754770\n" +
            "7.76570548191807\n" +
            "7.54938464982414\n" +
            "7.45645929363802\n" +
            "7.05534168327007\n" +
            "6.77834144153019\n" +
            "6.69036241678079\n" +
            "6.55654674541713\n" +
            "6.57412869633535\n" +
            "6.55974037888367\n" +
            "6.39250893703987\n" +
            "6.23584412157017\n" +
            "6.07943675835270\n" +
            "6.01055450144901\n" +
            "6.01007107051459\n" +
            "6.08038711004419\n" +
            "6.14645316020368\n" +
            "6.19612515950112\n" +
            "6.21848168522013\n" +
            "6.19344599491794\n" +
            "6.15167696155304\n" +
            "6.11785782714445\n" +
            "6.14631261407430\n" +
            "6.16417678440769\n" +
            "6.20700705911647\n" +
            "6.24785624909596\n" +
            "6.26272713435209\n" +
            "6.26240623269506\n" +
            "6.25247012482100\n" +
            "6.23126960817006\n" +
            "6.20867649001992\n" +
            "6.20795687201436\n" +
            "6.21931420083791\n" +
            "6.22127769477615\n" +
            "6.22822058142344\n" +
            "6.23756989884692\n" +
            "6.23477663631480\n" +
            "6.22405664767905\n" +
            "6.20317109448271\n" +
            "6.19388823641925\n" +
            "6.20873150690607\n" +
            "6.23027365721049\n" +
            "6.24712338319178\n" +
            "6.28114173495385\n" +
            "6.31281747270404\n" +
            "6.34745590375781\n" +
            "6.36980944462359\n" +
            "6.39625279241677\n" +
            "6.42739973346439\n" +
            "6.44895260447435\n" +
            "6.47851823472140\n" +
            "6.53292288189136\n" +
            "6.56682371666374\n" +
            "6.60806703278094\n" +
            "6.65336532712011\n" +
            "6.70769391401190\n" +
            "6.75160151187589\n" +
            "6.81951313010745\n" +
            "6.87601189099666\n" +
            "6.96669183873608\n" +
            "7.06378868066145\n" +
            "7.17650022936324\n" +
            "7.29780257057599\n" +
            "7.42572667082687\n" +
            "7.55529970682418\n" +
            "7.67045144971285\n" +
            "7.76825113702238\n" +
            "7.89537633455142\n" +
            "7.98372939954008\n" +
            "8.06586194566336\n" +
            "8.14186873131563\n" +
            "8.22598372718838\n" +
            "8.27814698295308\n" +
            "8.33608372764507\n" +
            "8.36786451617180\n" +
            "8.41210360324103\n" +
            "8.49509951723321\n" +
            "8.59642941004454\n" +
            "8.69557219182162\n" +
            "8.75941114723130\n" +
            "8.77963309241263\n" +
            "8.71598550370691\n" +
            "8.57719969045293\n" +
            "8.45077115062157\n" +
            "8.29884747353682\n" +
            "8.13410885779396\n" +
            "7.97898192915443\n" +
            "7.84313591442979\n" +
            "7.68858787249779\n" +
            "7.50486538661445\n" +
            "7.27168435023801\n" +
            "6.99145969801031\n" +
            "6.71514726608229\n" +
            "6.45101285909697\n" +
            "6.21775966026875\n" +
            "5.98937714269697\n" +
            "5.80826198658289\n" +
            "5.62640327203053\n" +
            "5.42189394005141\n" +
            "5.22886604223934\n" +
            "5.05693405130800\n" +
            "4.87558309670267\n" +
            "4.70293534348709\n" +
            "4.58166928261244\n" +
            "4.49500454594481\n" +
            "4.42216884207986\n" +
            "4.35264239184697\n" +
            "4.28481051566896\n" +
            "4.21163430627192\n" +
            "4.19783731465864\n" +
            "4.26172045338655\n" +
            "4.43356318001190\n" +
            "4.74586230492901\n" +
            "5.16148632141880\n" +
            "5.64286299565444\n" +
            "6.16296906331100\n" +
            "6.72176184406364\n" +
            "7.25155322550248\n" +
            "7.69944215133259\n" +
            "8.13862777587859\n" +
            "8.57632420616499\n" +
            "8.99818555418761\n" +
            "9.42785702193383\n" +
            "9.87170932522522\n" +
            "10.2655301969978\n" +
            "10.6744229045705\n" +
            "10.8281127336510\n" +
            "11.5209324682949\n" +
            "12.0188361435562\n" +
            "12.2597994781799\n" +
            "12.3196098935142\n" +
            "12.4591068654979\n" +
            "12.6132102129039\n" +
            "12.7577709532229\n" +
            "12.9952884800004\n" +
            "13.3411493932310\n" +
            "13.6023425559936\n" +
            "13.6465583743548\n" +
            "13.6166286645439\n" +
            "13.5349020263367\n" +
            "13.3373583338537\n" +
            "13.3471178725430\n" +
            "12.5724566059066\n" +
            "12.4892790605877\n" +
            "12.4129108155034\n" +
            "12.2379531858008\n" +
            "11.7693079198860\n" +
            "11.3597365954352\n" +
            "10.9620418183583\n" +
            "10.4249026278063\n" +
            "9.93418988089524\n" +
            "9.67663459909236\n" +
            "9.53699380511127\n" +
            "9.28273483791879\n" +
            "9.00771749897808\n" +
            "8.75817716681443\n" +
            "8.43055656440654\n" +
            "8.30847506415323\n" +
            "7.67984506474999\n" +
            "7.25929641741085\n" +
            "7.06432290929329\n" +
            "7.07060503569714\n" +
            "7.00089322894418\n" +
            "6.96775412347318\n" +
            "6.95548095776065\n" +
            "6.79848077450479\n" +
            "6.49907462416832\n" +
            "6.27076146602714\n" +
            "6.20790015213656\n" +
            "6.20844285553253\n" +
            "6.24482659567425\n" +
            "6.33478697417152\n" +
            "6.38735387939570\n" +
            "6.38176524732296\n" +
            "6.36480839542269\n" +
            "6.34965351237852\n" +
            "6.31452664002639\n" +
            "6.29779632760922\n" +
            "6.29162833405667\n" +
            "6.30970365355017\n" +
            "6.31974941615332\n" +
            "6.33787513019267\n" +
            "6.34761976823804\n" +
            "6.34618413559447\n" +
            "6.36601101025080\n" +
            "6.38957185391203\n" +
            "6.41253789893142\n" +
            "6.43669866444365\n" +
            "6.45673470775852\n" +
            "6.47669088185459\n" +
            "6.51079580143446\n" +
            "6.54231020174750\n" +
            "6.59346913980239\n" +
            "6.63976142570172\n" +
            "6.69285168547328\n" +
            "6.74934171346905\n" +
            "6.78106986536178\n" +
            "6.80953382729842\n" +
            "6.86824520839809\n" +
            "6.92137990306645\n" +
            "7.01090449169880\n" +
            "7.12656722922157\n" +
            "7.25387939309897\n" +
            "7.40877301523820\n" +
            "7.56892717240525\n" +
            "7.74632812625935\n" +
            "7.92877113255441\n" +
            "8.08906439218295\n" +
            "8.22769231107238\n" +
            "8.32729642097831\n" +
            "8.40259436283693\n" +
            "8.46748759602912\n" +
            "8.51916853058060\n" +
            "8.55788933124154\n" +
            "8.61978265155288\n" +
            "8.71958522236835\n" +
            "8.79385732640508\n" +
            "8.88486598122160\n" +
            "8.95275428512748\n" +
            "8.94666240887186\n" +
            "8.84538560128683\n" +
            "8.71996030454808\n" +
            "8.54266172098177\n" +
            "8.37824443858600\n" +
            "8.20816734482862\n" +
            "8.01793927323465\n" +
            "7.84312987781825\n" +
            "7.66602886631068\n" +
            "7.49931395647795\n" +
            "7.29676430388745\n" +
            "7.02702220714458\n" +
            "6.80234289659305\n" +
            "6.53940621034758\n" +
            "6.29058727393068\n" +
            "6.08399609433583\n" +
            "5.84664598884297\n" +
            "5.55960889653268\n" +
            "5.31641209346585\n" +
            "5.04631456105217\n" +
            "4.80870270842217\n" +
            "4.61838499786839\n" +
            "4.42500176772743\n" +
            "4.27289818298103\n" +
            "4.13048152753966\n" +
            "4.04270266866439\n" +
            "3.98161375190799\n" +
            "3.87186176639734\n" +
            "3.80653650715072\n" +
            "3.78790078244609\n" +
            "3.82274654817260\n" +
            "4.00777982165424\n" +
            "4.28401509072512\n" +
            "4.63575751481345\n" +
            "5.09834195990645\n" +
            "5.60718473115451\n" +
            "6.15096170220352\n" +
            "6.73767697062446\n" +
            "7.30626924262672\n" +
            "7.83000468963923\n" +
            "8.26997327217759\n" +
            "8.69066451071673\n" +
            "9.14854102939068\n" +
            "9.61332927689392\n" +
            "10.0584300437370\n" +
            "10.5153582960944\n" +
            "10.8816901445289\n" +
            "11.1755007742665\n" +
            "11.9650328421330\n" +
            "12.3133290052696\n" +
            "12.5472907966745\n" +
            "12.6185276133341\n" +
            "12.6414510071400\n" +
            "12.7535645354939\n" +
            "12.9205515223769\n" +
            "13.1907596195495\n" +
            "13.3407662901787\n" +
            "13.4853204120191\n" +
            "13.5320896096120\n" +
            "13.4843752341460\n" +
            "13.3533067861421\n" +
            "13.2613850700349\n" +
            "13.1845560372528\n" +
            "12.4522167549882\n" +
            "12.5833928407134\n" +
            "12.3692047422640\n" +
            "12.1818747358396\n" +
            "11.8711800924388\n" +
            "11.3903566485854\n" +
            "10.9271383221107\n" +
            "10.4176254361413\n" +
            "10.1248324642225\n" +
            "9.75776558163615\n" +
            "9.50692476734245\n" +
            "9.26522595333231\n" +
            "8.96306426818820\n" +
            "8.61860250955327\n" +
            "8.30234651929875\n" +
            "8.10096437858158\n" +
            "7.41775917785368\n" +
            "7.12010873225535\n" +
            "6.92839359188001\n" +
            "6.89938460390966\n" +
            "6.95971123292528\n" +
            "6.95200587088443\n" +
            "6.89860365390037\n" +
            "6.74829403296177\n" +
            "6.63210598860558\n" +
            "6.47412134509303\n" +
            "6.37203150653266\n" +
            "6.37330663528199\n" +
            "6.40255487877895\n" +
            "6.42793022651496\n" +
            "6.42105707417229\n" +
            "6.39702025261841\n" +
            "6.41094088440602\n" +
            "6.42028517561508\n" +
            "6.42855811169586\n" +
            "6.42612534359977\n" +
            "6.43492997476715\n" +
            "6.47219259381438\n" +
            "6.49446572755183\n" +
            "6.54048097251375\n" +
            "6.54569863388736\n" +
            "6.53781730730904\n" +
            "6.52560199610675\n" +
            "6.53391684214265\n" +
            "6.55807266805456\n" +
            "6.58772984085592\n" +
            "6.62534964421387\n" +
            "6.66735385514291\n" +
            "6.70814869484921\n" +
            "6.73662308370046\n" +
            "6.77441619245494\n" +
            "6.82047609765238\n" +
            "6.85866279771342\n" +
            "6.90421514283477\n" +
            "6.93703326770736\n" +
            "6.99266440036044\n" +
            "7.05143007113132\n" +
            "7.13264603861780\n" +
            "7.23280429032282\n" +
            "7.37332082007894\n" +
            "7.54809960803848\n" +
            "7.72465772189767\n" +
            "7.90713984074492\n" +
            "8.08101272097917\n" +
            "8.22686784582317\n" +
            "8.32081760479746\n" +
            "8.38300796133554\n" +
            "8.42524053850707\n" +
            "8.47802409963325\n" +
            "8.54479025455257\n" +
            "8.62836658292959\n" +
            "8.71540334045059\n" +
            "8.78438813888483\n" +
            "8.85526757398641\n" +
            "8.85037060230192\n" +
            "8.81276292012431\n" +
            "8.72114093983697\n" +
            "8.59045056218602\n" +//======== trnu
           "6.81587757852892\n" +
            "6.82343979577939\n" +
            "6.82234755142194\n" +
            "6.83298305259797\n" +
            "6.86173565315172\n" +
            "6.85597638033669\n" +
            "7.02053040470893\n" +
            "6.94675521340104\n" +
            "6.98961144272982\n" +
            "6.91342023554143\n" +
            "6.87073452484324\n" +
            "6.68543862138737\n" +
            "6.62277381725593\n" +
            "6.57415734816661\n" +
            "6.54305195978985\n" +
            "6.59732934803028\n" +
            "6.59048352826632\n" +
            "6.77900131903353\n" +
            "6.75288923395306\n" +
            "6.93297245018562\n" +
            "6.99850623196759\n" +
            "7.16490034758276\n" +
            "7.09053238477123\n" +
            "7.02977860519242\n" +
            "6.85499052418581\n" +
            "6.63754697728888\n" +
            "7.10420622370647\n" +
            "7.14885406612723\n" +
            "7.27817730316766\n" +
            "7.37872657954064\n" +
            "7.60512637511862\n" +
           "6.66166032645191\n" +
            "6.68717782234355\n" +
            "6.73575536236452\n" +
            "6.79747342921782\n" +
            "6.85987745729871\n" +
            "6.96048770602617\n" +
            "7.08425814809409\n" +
            "7.18444665252525\n" +
            "7.34403836302223\n" +
            "7.44273900130738\n" +
            "7.53734733544677\n" +
            "7.60247927659819\n" +
            "7.67318567331684\n" +
            "7.74218863992825\n" +
            "7.78494335364485\n" +
            "7.85083661862757\n" +
            "7.95284511511308\n" +
            "8.02892296961644\n" +
            "8.09615845081282\n" +
            "8.15134633967484\n" +
            "8.16939445104445\n" +
            "8.18941283372509\n" +
            "8.23331719831205\n" +
            "8.21377529599080\n" +
            "8.32767986936347\n" +
            "8.39621521941713\n" +
            "8.42284083912864\n" +
            "8.44232370278697\n" +
            "8.46916318118528\n" +
            "8.47720917240326\n" +
            "8.43017226646106\n" +
            "8.34731253997315\n" +
            "8.26580251953753\n" +
            "8.17982219499074\n" +
            "8.08586236684475\n" +
            "7.96059211626719\n" +
            "7.80955722841480\n" +
            "7.63987841135582\n" +
            "7.46417275223397\n" +
            "7.21979674689918\n" +
            "7.07165558418492\n" +
            "6.99699834577021\n" +
            "6.88382134359382\n" +
            "6.78973638706423\n" +
            "6.72051857120290\n" +
            "6.66640351138645\n" +
            "6.61345328494404\n" +
            "6.56297860794693\n" +
            "6.49972909149320\n" +
            "6.45418274438967\n" +
            "6.44564394215062\n" +
            "6.43910563522198\n" +
            "6.45958800778575\n" +
            "6.50610646006342\n" +
            "6.55186683629023\n" +
            "6.56188423789619\n" +
            "6.57650957953803\n" +
            "6.66003603305870\n" +
            "6.74482183743484\n" +
            "6.83786816276422\n" +
            "6.95442816228468\n" +
            "7.08734835108509\n" +
            "7.23289331930723\n" +
            "7.40212084897925\n" +
            "7.57629400510542\n" +
            "7.75589638232223\n" +
            "7.92323162512431\n" +
            "8.08178533960517\n" +
            "8.23894520534723\n" +
            "8.39388331417146\n" +
            "8.55305953430143\n" +
            "8.70394676983349\n" +
            "8.82504220775695\n" +
            "8.93246449344454\n" +
            "9.03274421669693\n" +
            "9.12616204963941\n" +
            "9.21411704354164\n" +
            "9.28561399131770\n" +
            "9.34434944075046\n" +
            "9.40084276391533\n" +
            "9.46100753084778\n" +
            "9.58631275752473\n" +
            "9.63252903437468\n" +
            "9.67662505342101\n" +
            "9.71964958652948\n" +
            "9.81460077841269\n" +
            "10.0136125181837\n" +
            "10.1245971095987\n" +
            "10.0486372096759\n" +
            "9.82151815972667\n" +
            "9.61464688708049\n" +
            "9.44476895794964\n" +
            "9.30853359995836\n" +
            "9.18488705256866\n" +
            "9.05747866812466\n" +
            "8.89632670468017\n" +
            "8.69150338759335\n" +
            "8.62398622551003\n" +
            "8.50153126312520\n" +
            "8.38149445488248\n" +
            "8.22828565509662\n" +
            "8.03413679773983\n" +
            "8.02663575993172\n" +
            "8.09611466651538\n" +
            "8.13282608643735\n" +
            "8.00143044802890\n" +
            "7.87988931632589\n" +
            "7.76552238167144\n" +
            "7.67880785589124\n" +
            "7.61674164346449\n" +
            "7.56921238158517\n" +
            "7.48658913594691\n" +
            "7.33952794315759\n" +
            "7.24046287330247\n" +
            "7.13706931899704\n" +
            "7.00965567923657\n" +
            "6.82812098359884\n" +
            "6.54144897955988\n" +
            "6.34089159166841\n" +
            "6.30971188582339\n" +
            "6.42287332374362\n" +
            "6.48286075094757\n" +
            "6.52441953530526\n" +
            "6.52119484701514\n" +
            "6.50457431686585\n" +
            "6.50240580995473\n" +
            "6.52130288824554\n" +
            "6.53686091933141\n" +
            "6.54700546651405\n" +
            "6.54451381721692\n" +
            "6.53850483852511\n" +
            "6.53258362912098\n" +
            "6.52365135335856\n" +
            "6.52665582205912\n" +
            "6.52663048308439\n" +
            "6.53349735615848\n" +
            "6.55986086780361\n" +
            "6.55625540287331\n" +
            "6.56179807225291\n" +
            "6.55516576508320\n" +
            "6.55000818456260\n" +
            "6.56180981803288\n" +
            "6.57818839229336\n" +
            "6.60120274491806\n" +
            "6.62952043223744\n" +
            "6.66549793065842\n" +
            "6.69080711732380\n" +
            "6.74043946225953\n" +
            "6.78093368796564\n" +
            "6.83573081393381\n" +
            "6.88072065081352\n" +
            "6.98817051425476\n" +
            "7.15431021439198\n" +
            "7.31755277169034\n" +
            "7.41461990877841\n" +
            "7.53648725538330\n" +
            "7.65295579380352\n" +
            "7.79706513069831\n" +
            "7.93023951921444\n" +
            "8.07209935851711\n" +
            "8.20410781128573\n" +
            "8.37075049231859\n" +
            "8.51184118834161\n" +
            "8.63700043905860\n" +
            "8.75759880307464\n" +
            "8.89610168098478\n" +
            "8.96628933221109\n" +
            "9.01261809508697\n" +
            "9.01769970435162\n" +
            "9.14720542940250\n" +
            "9.22664508399420\n" +
            "9.30207873221222\n" +
            "9.32189692736426\n" +
            "9.36470037897149\n" +
            "9.35423405674603\n" +
            "9.30752292153668\n" +
            "9.25537214302882\n" +
            "9.21564029112482\n" +
            "9.18794763437246\n" +
            "9.05337611727105\n" +
            "8.87904900274781\n" +
            "8.74537631177122\n" +
            "8.60449152963551\n" +
            "8.46246081826791\n" +
            "8.17404522397625\n" +
            "7.92888231000839\n" +
            "7.71757940512120\n" +
            "7.49931174374963\n" +
            "7.24472985731084\n" +
            "7.03688141684355\n" +
            "6.84094921392197\n" +
            "6.60110334708568\n" +
            "6.40807071822589\n" +
            "6.21711631093500\n" +
            "6.18048096797123\n" +
            "6.06883247682567\n" +
            "5.90701468739661\n" +
            "5.76485570522748\n" +
            "5.69116020846572\n" +
            "5.73440321174306\n" +
            "5.75878495812758\n" +
            "5.76246485835352\n" +
            "5.81435606979896\n" +
            "5.90050566399394\n" +
            "6.01472364724822\n" +
            "6.18472480873093\n" +
            "6.38943232662416\n" +
            "6.60177766768053\n" +
            "6.84799250287609\n" +
            "7.07121513244099\n" +
            "7.45206652338704\n" +
            "7.82272440502109\n" +
            "8.17252881498686\n" +
            "8.49863643865765\n" +
            "8.80330333511773\n" +
            "9.09787744470760\n" +
            "9.37769144591056\n" +
            "9.66903129633205\n" +
            "9.96929692097955\n" +
            "10.2602311745246\n" +
            "10.5114283974030\n" +
            "10.7622637084215\n" +
            "10.9093508845945\n" +
            "10.9934999199901\n" +
            "11.0323692555166\n" +
            "11.0965569046184\n" +
            "11.2526216742565\n" +
            "11.6013509764040\n" +
            "11.8392748134766\n" +
            "11.8537536980236\n" +
            "11.7825481508612\n" +
            "11.7079472576101\n" +
            "11.5461017272520\n" +
            "11.4213565857409\n" +
            "11.2932243530166\n" +
            "11.1869155659316\n" +
            "10.9947464659562\n" +
            "10.8476118236636\n" +
            "10.6730552197809\n" +
            "10.4545713804325\n" +
            "10.1248684063980\n" +
            "9.79330863381343\n" +
            "9.31910434643027\n" +
            "9.15548771936196\n" +
            "9.10176676812302\n" +
            "8.92553522711472\n" +
            "8.67233073910262\n" +
            "8.50487227490277\n" +
            "8.23992603554070\n" +
            "8.01688174472298\n" +
            "7.80365031146136\n" +
            "7.65672322261636\n" +
            "7.47971512271475\n" +
            "7.36599853829080\n" +
            "7.31094838148097\n" +
            "7.28892512710663\n" +
            "7.19906538458309\n" +
            "7.04563099042014\n" +
            "6.68465800142768\n" +
            "6.44327962067736\n" +
            "6.40405915800497\n" +
            "6.45772568775339\n" +
            "6.48109645766227\n" +
            "6.53946687127928\n" +
            "6.54159372485800\n" +
            "6.51407248455198\n" +
            "6.49311313213858\n" +
            "6.49161945114964\n" +
            "6.49714876360914\n" +
            "6.51172598416108\n" +
            "6.51822828217688\n" +
            "6.52692877020802\n" +
            "6.53610594811907\n" +
            "6.54736844182120\n" +
            "6.54636934722013\n" +
            "6.56969698342057\n" +
            "6.58841920059389\n" +
            "6.61703161975433\n" +
            "6.64334588982331\n" +
            "6.66168230837192\n" +
            "6.69044369884766\n" +
            "6.71677486209442\n" +
            "6.77846139910361\n" +
            "6.85867508427731\n" +
            "6.87140000642154\n" +
            "6.92700846871687\n" +
            "6.96744205167454\n" +
            "7.03436739476438\n" +
            "7.11086150927830\n" +
            "7.20241365343432\n" +
            "7.33188608194008\n" +
            "7.40322317055245\n" +
            "7.49895250875056\n" +
            "7.59945454166436\n" +
            "7.71625555905653\n" +
            "7.83577038086956\n" +
            "7.95376254681718\n" +
            "8.07488905469858\n" +
            "8.18086396611656\n" +
            "8.36955244443397\n" +
            "8.45470615214555\n" +
            "8.56687024637068\n" +
            "8.64748218680052\n" +
            "8.77506270955737\n" +
            "8.90710644900889\n" +
            "8.93249346613450\n" +
            "9.04394327993499\n" +
            "9.07198754344472\n" +
            "9.21507519668850\n" +
            "9.41766931972572\n" +
            "9.64122013475618\n" +
            "9.75768699055305\n" +
            "9.75615312048503\n" +
            "9.73529340900768\n" +
            "9.66719922861091\n" +
            "9.63029597861866\n" +
            "9.58874717070873\n" +
            "9.49821367663033\n" +
            "9.33093678663729\n" +
            "9.12161716133982\n" +
            "8.97039936055525\n" +
            "8.72321303530230\n" +
            "8.51348400314621\n" +
            "8.20614012506368\n" +
            "7.92091712475361\n" +
            "7.67599494851201\n" +
            "7.54473180305015\n" +
            "7.39241801748856\n" +
            "7.16839450133405\n" +
            "6.94703935398774\n" +
            "6.75610251237586\n" +
            "6.54380549809625\n" +
            "6.39972545517931\n" +
            "6.26010073806663\n" +
            "6.13840249042871\n" +
            "5.98936204208185\n" +
            "5.93331437493971\n" +
            "5.91159739681237\n" +
            "5.90693832256172\n" +
            "5.86289509125731\n" +
            "5.77103872132880\n" +
            "5.69978861784135\n" +
            "5.77522803781324\n" +
            "5.94537928545706\n" +
            "6.18426729900575\n" +
            "6.47071875143858\n" +
            "6.79451231579633\n" +
            "7.12283736526381\n" +
            "7.45139355849696\n" +
            "7.84751816841232\n" +
            "8.28918576443457\n" +
            "8.70815520274447\n" +
            "9.10812035327900\n" +
            "9.44114709373740\n" +
            "9.74265655838001\n" +
            "10.0527661954764\n" +
            "10.3508711222119\n" +
            "10.6389371542630\n" +
            "10.8925315916859\n" +
            "11.2173668988879\n" +
            "11.5561819413611\n" +
            "11.6881115794159\n" +
            "11.7551622559951\n" +
            "11.8549546003777\n" +
            "11.9895294790130\n" +
            "12.3109012318842\n" +
            "12.6891164144058\n" +
            "12.6644869297711\n" +
            "12.4808767942726\n" +
            "12.3672046044888\n" +
            "12.2434491948456\n" +
            "12.0674767182025\n" +
            "11.9181200726381\n" +
            "11.8297001609760\n" +
            "11.5421360858545\n" +
            "11.2624568227268\n" +
            "11.1791962096350\n" +
            "10.9067597228513\n" +
            "10.5617800168341\n" +
            "10.1378009130021\n" +
            "9.61541226294980\n" +
            "9.19959310012753\n" +
            "9.24563126193509\n" +
            "9.10726090055421\n" +
            "8.73677679489381\n" +
            "8.49005194831587\n" +
            "8.29186575043455\n" +
            "8.02637403325019\n" +
            "7.77421574983462\n" +
            "7.64916580019721\n" +
            "7.43798718770616\n" +
            "7.17294780087245\n" +
            "7.13695548886259\n" +
            "7.13549162256788\n" +
            "7.12598043969896\n" +
            "7.00921575451896\n" +
            "6.71865485067543\n" +
            "6.36967510904831\n" +
            "6.38979005742275\n" +
            "6.54893922524088\n" +
            "6.58577338773889\n" +//================== down
            "6.57154908979470\n" +
            "6.58167113636241\n" +
            "6.59543891602352\n" +
            "6.53325728050062\n" +
            "6.41271748853687\n" +
            "6.34801529418296\n" +
            "6.15999178797049\n" +
            "5.94276354136643\n" +
            "5.70734289102140\n" +
            "5.78507793239285\n" +
            "5.80494477973104\n" +
            "5.73906356913262\n" +
            "5.84510860644722\n" +
            "6.53500462605533\n" +
            "6.75217033110571\n" +
            "7.28166032972794\n" +
            "7.44932745410251\n" +
            "7.57121802381270\n" +
            "7.24323553945545\n" +
            "7.00500270013751\n" +
            "6.44529197099417\n" +
            "6.48532196610828\n" +
            "6.50994111749948\n" +
            "6.42009435562454\n" +
            "6.46864853971076\n" +
            "6.53764973132144\n" +
            "6.56832137557512\n" +
            "6.62605093882452\n" +
            "6.69845328872010\n" +
            "6.73029422936621\n" +
            "6.75681164449798\n" +
            "6.74218152470075\n";


}
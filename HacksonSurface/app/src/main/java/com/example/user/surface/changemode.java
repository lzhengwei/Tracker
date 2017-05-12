package com.example.user.surface;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;

public class changemode extends AppCompatActivity {

    public Button fambutton;
    public Button oldbutton;
    static int mainmode=0;
    public TextView mode;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        verifyStoragePermissions(this);
       //button宣告
        fambutton=(Button) findViewById(R.id.family);
        oldbutton=(Button) findViewById(R.id.old);
        mode=(TextView)findViewById(R.id.mode);
        int nowmode;
        //family button
        fambutton = (Button) findViewById(R.id.family);
        fambutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                writetxtdata("0");
                mode.setText("家人模式");
            }
        });
        //old button
        oldbutton = (Button) findViewById(R.id.old);
        oldbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                writetxtdata("1");
                mode.setText("老人模式");
            }
        });

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
    static void writetxtdata(String mode) {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();

            String filename;
            filename = "TrackerMode";

            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/Tracker/");
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdir();
                Log.v("creat",mFile.getPath());
            }

            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/Tracker/"+filename  + ".txt",false);
            mfilewriter.write(mode);
            mfilewriter.close();


        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }

    }
   }


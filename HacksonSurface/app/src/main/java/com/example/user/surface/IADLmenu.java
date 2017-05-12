package com.example.user.surface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class IADLmenu extends AppCompatActivity {
    public Button qusetionbutton;
    public Button phonebutton;
    public Button medicinebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iadlmenu);

        //question button
        qusetionbutton = (Button) findViewById(R.id.question);
        qusetionbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IADLmenu.this, iadl.class);
                startActivity(intent);

            }

        });
        //phone button
        phonebutton = (Button) findViewById(R.id.phone);
        phonebutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IADLmenu.this, numbergame.class);
                startActivity(intent);

            }

        });

    }
}

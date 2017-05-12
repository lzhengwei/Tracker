package com.example.user.surface;


import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class numbergame extends AppCompatActivity {

    public String num;
    public int time=0;
    public String checknum;
    public int right=0,fault=0;
    String num1="0935718624";
    String num2="0931742691";
    String num3="0923674355";
    String num4="0987953661";
    String num5="0961582973";
    String num6="0938674981";
    String num7="0973691834";
    String num8="0919149086";
    String num9="0937681247";
    String num10="0967842391";
    public TextView number;
    public TextView result;
    public TextView userintput;
    public TextView scoreresult;
    public Button button0;
    public Button button1;
    public Button button2;
    public Button button3;
    public Button button4;
    public Button button5;
    public Button button6;
    public Button button7;
    public Button button8;
    public Button button9;
    public Button check;
    public Button deltete;
    public Button play;
    private SoundPool sound;
    int number1,number2,number3,number4,number5,number6,number7,number8,number9,number10,rights,faults,complete;
    public int choice=0,exchoice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);
        //語音播放
        sound = new SoundPool(13, AudioManager.STREAM_MUSIC, 5);
        number1=sound.load(this,R.raw.number1,1);
        number2=sound.load(this,R.raw.number2,1);
        number3=sound.load(this,R.raw.number3,1);
        number4=sound.load(this,R.raw.number4,1);
        number5=sound.load(this,R.raw.number5,1);
        number6=sound.load(this,R.raw.number6,1);
        number7=sound.load(this,R.raw.number7,1);
        number8=sound.load(this,R.raw.number8,1);
        number9=sound.load(this,R.raw.number9,1);
        number10=sound.load(this,R.raw.number10,1);
        rights=sound.load(this,R.raw.right,1);
        faults=sound.load(this,R.raw.fault,1);
        complete=sound.load(this,R.raw.complete,1);



        number=(TextView)findViewById(R.id.num);
        result=(TextView)findViewById(R.id.result);
        userintput=(TextView)findViewById(R.id.intput);
        scoreresult=(TextView)findViewById(R.id.score);

        //隨產生電話號碼
        choice=(int)(Math.random()* 10+1);
        if(choice!=exchoice) {
            if (choice == 1) {
                number.setText("範例號碼:" + num1);
                num = num1;
            } else if (choice == 2) {
                number.setText("範例號碼:" + num2);
                num = num2;
            } else if (choice == 3) {
                number.setText("範例號碼:" + num3);
                num = num3;
            } else if (choice == 4) {
                number.setText("範例號碼:" + num4);
                num = num4;
            } else if (choice == 5) {
                number.setText("範例號碼:" + num5);
                num = num5;
            } else if (choice == 6) {
                number.setText("範例號碼:" + num6);
                num = num6;
            } else if (choice == 7) {
                number.setText("範例號碼:" + num7);
                num = num7;
            } else if (choice == 8) {
                number.setText("範例號碼:" + num8);
                num = num8;
            } else if (choice == 9) {
                number.setText("範例號碼:" + num9);
                num = num9;
            } else {
                number.setText("範例號碼:" + num10);
                num = num10;
            }
            exchoice=choice;
        }
        else
            choice=(int)(Math.random()* 10+1);

        userintput.setText("您的輸入:");
        //語音撥放
        play=(Button)findViewById(R.id.play);
        play.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (choice == 1) {
                    sound.play(number1, 1, 1, 0, 0, 1);
                } else if (choice == 2) {
                    sound.play(number2, 1, 1, 0, 0, 1);
                } else if (choice == 3) {
                    sound.play(number3, 1, 1, 0, 0, 1);
                } else if (choice == 4) {
                    sound.play(number4, 1, 1, 0, 0, 1);
                } else if (choice == 5) {
                    sound.play(number5, 1, 1, 0, 0, 1);
                } else if (choice == 6) {
                    sound.play(number6, 1, 1, 0, 0, 1);
                } else if (choice == 7) {
                    sound.play(number7, 1, 1, 0, 0, 1);
                } else if (choice == 8) {
                    sound.play(number8, 1, 1, 0, 0, 1);
                } else if (choice == 9) {
                    sound.play(number9, 1, 1, 0, 0, 1);
                } else {
                    sound.play(number10, 1, 1, 0, 0, 1);
                }
               }
            });
        //提交號碼
        check=(Button) findViewById(R.id.send);
        check.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = userintput.getText().toString();
                checknum=content.substring(5,content.length()-0);
                //userintput.setText(content.substring(5,content.length()-0));

                if( checknum.equals(num))
                {
                    right=right+1;
                    time=time+1;
                    Toast.makeText(numbergame.this, "答對了", Toast.LENGTH_SHORT).show();
                    sound.play(rights, 1, 1, 0, 0, 1);
                    //產生新的號碼
                    choice=(int)(Math.random()* 10+1);
                    if(choice==1) {
                        number.setText("範例號碼:" + num1);
                        userintput.setText("您的輸入:");
                        num = num1;
                    }
                    else if(choice==2) {
                        number.setText("範例號碼:" + num2);
                        userintput.setText("您的輸入:");
                        num=num2;
                    }
                    else if(choice==3) {
                        number.setText("範例號碼:" + num3);
                        userintput.setText("您的輸入:");
                        num=num3;
                    }
                    else if(choice==4) {
                        number.setText("範例號碼:" + num4);
                        userintput.setText("您的輸入:");
                        num=num4;
                    }
                    else if(choice==5) {
                        number.setText("範例號碼:" + num5);
                        userintput.setText("您的輸入:");
                        num=num5;
                    }
                    else if(choice==6) {
                        number.setText("範例號碼:" + num6);
                        userintput.setText("您的輸入:");
                        num=num6;
                    }
                    else if(choice==7) {
                        number.setText("範例號碼:" + num7);
                        userintput.setText("您的輸入:");
                        num=num7;
                    }
                    else if(choice==8) {
                        number.setText("範例號碼:" + num8);
                        userintput.setText("您的輸入:");
                        num=num8;
                    }
                    else if(choice==9) {
                        number.setText("範例號碼:" + num9);
                        userintput.setText("您的輸入:");
                        num=num9;
                    }
                    else {
                        number.setText("範例號碼:" + num10);
                        userintput.setText("您的輸入:");
                        num=num10;
                    }

                    userintput.setText("您的輸入:");
                }
                else
                {
                    fault=fault+1;
                    time=time+1;
                    sound.play(faults, 1, 1, 0, 0, 1);
                }
                scoreresult.setText("次數:"+time+"答對:"+right+"答錯:"+fault);
                if(time==3)
                {
                    Log.v("thead com",time+" "+fault);

                    userintput.setText("已完成");
                    Toast.makeText(numbergame.this, "已完成測驗", Toast.LENGTH_SHORT).show();
                    time=0;
                    fault=0;
                    right=0;
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                                            }
                    sound.play(complete, 1, 1, 0, 0, 1);
                }

            }

        });

        //倒退刪除
        deltete=(Button) findViewById(R.id.del);
        deltete.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = userintput.getText().toString();
                int L=content.length();
                if(L>5)
                    userintput.setText(content.substring(0,content.length()-1));
                else
                    userintput.setText("您的輸入:");
            }

        });

          //buntton0
          button0 = (Button) findViewById(R.id.b0);
          button0.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"0");
              }

          });

          //button1
          button1 = (Button) findViewById(R.id.b1);
          button1.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"1");
              }

          });

          //button2
          button2 = (Button) findViewById(R.id.b2);
          button2.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"2");
              }

          });

          //button3
          button3 = (Button) findViewById(R.id.b3);
          button3.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"3");
              }

          });

          //button4
          button4 = (Button) findViewById(R.id.b4);
          button4.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"4");
              }

          });

          //button5
          button5 = (Button) findViewById(R.id.b5);
          button5.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"5");
              }

          });

          //butoon6
          button6 = (Button) findViewById(R.id.b6);
          button6.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"6");
              }

          });

          //button7
          button7 = (Button) findViewById(R.id.b7);
          button7.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"7");
              }

          });

          //button8
          button8 = (Button) findViewById(R.id.b8);
          button8.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"8");
              }

          });

          //button9
          button9 = (Button) findViewById(R.id.b9);
          button9.setOnClickListener(new Button.OnClickListener() {

              @Override
              public void onClick(View v) {
                  userintput.setText(userintput.getText()+"9");
              }


          });




    }
}

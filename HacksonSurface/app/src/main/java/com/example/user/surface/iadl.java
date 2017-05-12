package com.example.user.surface;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


public class iadl extends AppCompatActivity {
    private Spinner phonespinner;
    private Spinner cookspinner;
    private Spinner washspinner;
    private Spinner medicinespinner;
    private Spinner moneyspinner;
    private Spinner shoppingspinner;
    private Spinner homespinner;
    private Spinner outdoorspinner;
    public String phonestr;
    public String cookstr;
    public String washstr;
    public String medicinestr;
    public String moneystr;
    public String shoppingstr;
    public String homestr;
    public String outdoorstr;
    public Button caculate;
    public TextView score;
    public int s=0,lost=0;
    private String[] listphone = {"獨立使用電話,含查電話簿、撥號等","僅可撥熟悉的電話號碼","僅會接電話,不會撥電話","完全不會使用電話"};
    private String[] listcook={"能獨立計畫、烹煮和擺設一頓適當的飯菜","如果準備好一切佐料,會做一頓適當的飯菜","會將已做好的飯菜加熱","需要家人把飯菜煮好、擺好"};
    private String[] listwash={"自己清洗所有衣物","只能清洗小件衣物","需完全依賴他人清洗"};
    private String[] listmedicine={"能自己負責在正確的時間用正確的藥物","需要提醒或一些協助","需事先準備好服用的藥物份量,可自行服用","無法自己服用藥物"};
    private String[] listmoney={"可獨立處理財務","可以處理日常的購買,需要別人的協助與銀行的往來或大宗買賣","不能自行處理財務"};
    private String[] listshopping={"獨立完成所有購物需求","獨立購買日常生活用品","上街購物需要有人陪伴","無法上街購物"};
    private String[] listhome={"能做較繁重的家事或需偶爾家事協助","能做較簡單的家事,如洗碗、鋪床、折棉被","能做家事,但無法達到乾淨的程度","所有的家事都需要家人協助","完全無法做家事"};
    private String[] listoutdoor={"能夠自己搭乘大眾運輸工具或自己開車、騎車","可搭計程車或大眾運輸工具","能夠自己搭乘計程車但不會搭乘大眾運輸工具","需有人陪同搭計程車或大眾運輸工具","完全無法自己出門"};
    private ArrayAdapter<String> listAdapter;
    public int cookscore=0;
    public int washscore=0;
    public int medscore=0;
    public int moneyscore=0;
    public int shoppingscore=0;
    public int homescroe=0;
    public int outdoorscore=0;
    public int phonescore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iadlsurface);

        //打電話
        phonespinner=(Spinner)findViewById(R.id.phone); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listphone);
        //將adapter 添加到spinner中
        phonespinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        phonespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+phonespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //烹煮
        cookspinner=(Spinner)findViewById(R.id.cook); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listcook);
        cookspinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        cookspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+cookspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //洗衣服
        washspinner=(Spinner)findViewById(R.id.wash); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listwash);
        washspinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        washspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+washspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //吃藥
        medicinespinner=(Spinner)findViewById(R.id.medicine); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listmedicine);
        medicinespinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        medicinespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+medicinespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //財務管理
        moneyspinner=(Spinner)findViewById(R.id.money); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listmoney);
        moneyspinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        moneyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+moneyspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //上街購物
        shoppingspinner=(Spinner)findViewById(R.id.shopping); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listshopping);
        shoppingspinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        shoppingspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+shoppingspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //家務維持
        homespinner=(Spinner)findViewById(R.id.home); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listhome);
        homespinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        homespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+homespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //外出
        outdoorspinner=(Spinner)findViewById(R.id.outdoor); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listoutdoor);
        outdoorspinner.setAdapter(listAdapter);

        //設定項目被選取之後的動作
        outdoorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(iadl.this, "你選的是"+outdoorspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView arg0) {

                Toast.makeText(iadl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        //提交
        caculate=(Button)findViewById(R.id.caculate);
        score=(TextView)findViewById(R.id.score);
        caculate.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                //傳送data
                Thread t=new Thread(new sendPostRunnable());
                t.start();

                //計算電話
                phonestr = String.valueOf(phonespinner.getSelectedItem());
                if(phonestr=="獨立使用電話,含查電話簿、撥號等"){
                    s=s+3;
                    phonescore=3;}
                else if(phonestr=="僅可撥熟悉的電話號碼"){
                    s=s+2;
                    phonescore=2;}
                else if(phonestr=="僅會接電話,不會撥電話") {
                    s = s + 1;
                    phonescore=1;
                }
                else{
                    s=s+0;
                phonescore=0;}
                //計算烹煮
                cookstr = String.valueOf(cookspinner.getSelectedItem());
                if(cookstr=="能獨立計畫、烹煮和擺設一頓適當的飯菜"){
                    s=s+3;
                cookscore=3;}
                else if(cookstr=="如果準備好一切佐料,會做一頓適當的飯菜"){
                    s=s+2;
                    cookscore=2;}
                else if(cookstr=="會將已做好的飯菜加熱"){
                    s=s+1;
               cookscore=1;}
                else
                {   s=s+0;
                    lost++;
                cookscore=0;}
                //計算洗衣
                washstr = String.valueOf(washspinner.getSelectedItem());
                if(washstr=="自己清洗所有衣物") {
                    s = s + 2;
                washscore=2;}
                else if(washstr=="只能清洗小件衣物"){
                    s=s+1;
                    washscore=1;}
                else
                {   s=s+0;
                    lost++;
                washscore=0;}
                //計算吃藥
                medicinestr = String.valueOf(medicinespinner.getSelectedItem());
                if(medicinestr=="能自己負責在正確的時間用正確的藥物"){
                    s=s+3;
                medscore=3;}
                else if(medicinestr=="需要提醒或一些協助"){
                    s=s+2;
                medscore=2;}
                else if(medicinestr=="需事先準備好服用的藥物份量,可自行服用"){
                    s=s+1;
                medscore=1;}
                else{
                    s=s+0;
                    medscore=0;}

                //計算財務
                moneystr = String.valueOf(moneyspinner.getSelectedItem());
                if(moneystr=="可獨立處理財務"){
                    s=s+2;
                moneyscore=2;}
                else if(moneystr=="可以處理日常的購買,需要別人的協助與銀行的往來或大宗買賣"){
                    s=s+1;
                moneyscore=1;}
                else{
                    s=s+0;
                moneyscore=0;}

                //計算購物
                shoppingstr = String.valueOf(shoppingspinner.getSelectedItem());
                if(shoppingstr=="獨立完成所有購物需求"){
                    s=s+3;
                shoppingscore=3;}
                else if(shoppingstr=="獨立購買日常生活用品")
                {   s=s+2;
                    shoppingscore=2;}
                else if(shoppingstr=="上街購物需要有人陪伴"){
                    s=s+1;
                shoppingscore=1;}
                else
                {   s=s+0;
                    lost++;
                shoppingscore=0;}

                //計算家務
                homestr = String.valueOf(homespinner.getSelectedItem());
                if(homestr=="能做較繁重的家事或需偶爾家事協助"){
                    s=s+4;
                homescroe=4;}
                else if(homestr=="能做較簡單的家事,如洗碗、鋪床、折棉被"){
                    s=s+3;
                homescroe=3;}
                else if(homestr=="能做家事,但無法達到乾淨的程度"){
                    s=s+2;
                homescroe=2;}
                else if(homestr=="所有的家事都需要家人協助")
                {   s=s+1;
                    lost++;
                homescroe=1;}
                else
                {   s=s+0;
                    lost++;
                homescroe=0;}

                //計算出門
                outdoorstr = String.valueOf(outdoorspinner.getSelectedItem());
                if(outdoorstr=="能夠自己搭乘大眾運輸工具或自己開車、騎車"){
                    s=s+4;
                    outdoorscore=4;}
                else if(outdoorstr=="可搭計程車或大眾運輸工具"){
                    s=s+3;
                    outdoorscore=3;}
                else if(outdoorstr=="能夠自己搭乘計程車但不會搭乘大眾運輸工具"){
                    s=s+2;
                    outdoorscore=2;}
                else if(outdoorstr=="需有人陪同搭計程車或大眾運輸工具")
                {   s=s+1;
                    lost++;
                outdoorscore=1;}
                else
                {   s=s+0;
                    lost++;
                outdoorscore=0;}

                score.setText("您的分數："+s);
                score.setTextSize(25);
                score.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                Toast.makeText(iadl.this, "您的分數是"+s, Toast.LENGTH_SHORT).show();


                if(lost>=3||s<15)
                {
                    score.setTextColor(Color.RED);
                }
                else if(s<=18 && s>=16)
                {
                    score.setTextColor(Color.BLUE);
                }
                else if(s>18)
                {
                    score.setTextColor(Color.GREEN);
                }


                s=0;
            }

        });




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private String uriAPI = "http://192.168.137.154/test/GOGO.php";
    protected static final int REFRESH_DATA = 0x00000001;
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null)
                        // 印出網路回傳的文字
                        // Toast.makeText(TUG_MainActivity.this, result, Toast.LENGTH_LONG).show();
                        break;
            }
        }
    };

    private String sendPostDataToInternet()
    {
      /* 建立HTTP Post連線 */
        HttpPost httpRequest = new HttpPost(uriAPI);
      /*
       * Post運作傳送變數必須用NameValuePair[]陣列儲存
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
      /*  List<HashMap<String, Float>> params = new ArrayList<HashMap<String, Float>>();
        HashMap<String, Float> map = new HashMap<String, Float>();*/

        params.add(new BasicNameValuePair("username","ha"));
        params.add(new BasicNameValuePair("Cookscore", String.valueOf(cookscore)));
        params.add(new BasicNameValuePair("Washscore", String.valueOf(washscore)));
        params.add(new BasicNameValuePair("Medscore", String.valueOf(medscore)));
        params.add(new BasicNameValuePair("Moneyscore", String.valueOf(moneyscore)));
        params.add(new BasicNameValuePair("Shoppingscore", String.valueOf(shoppingscore)));
        params.add(new BasicNameValuePair("Homescore", String.valueOf(homescroe)));
        params.add(new BasicNameValuePair("Outdoorscore", String.valueOf(outdoorscore)));
        params.add(new BasicNameValuePair("Phonescore", String.valueOf(phonescore)));

        Log.v("params",params.toString());

        try
        {
          /* 發出HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity( params, HTTP.UTF_8));
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
                return strResult;

            }
        } catch (Exception e)

        {

            e.printStackTrace();

        }

        return null;

    }

    class sendPostRunnable implements Runnable
    {

        @Override
        public void run()
        {
            String result = sendPostDataToInternet();
            mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        }

    }


}

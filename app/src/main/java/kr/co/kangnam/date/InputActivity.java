package kr.co.kangnam.date;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

/**
 * Created by aks56 on 2018-05-07.
 */

public class InputActivity extends Activity {
    TextView tvDate;
    Date date;

    SimpleDateFormat curYearFormat;
    SimpleDateFormat curMonthFormat;
    SimpleDateFormat curDayFormat;

    String Year, Month;
    String Date;
    String phoneNum;
    String id;

    IpAddress ipAddress;
    int InputMoney = 0;
    int SpendMoney = 0;

    String sex;
    String other;

    CustomDialog mCustomDialog;

    ListViewAdapter2 listViewAdapter;
    ArrayList<MyItem2> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_layout);

        tvDate = (TextView)findViewById(R.id.tv_date);

        Intent intent = getIntent();
        if(intent!=null) {
            curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            Year = intent.getStringExtra("Year");
            Month = intent.getStringExtra("Month");
            Date = intent.getStringExtra("Date");

            date = new Date(parseInt(Year), parseInt(Month), parseInt(Date));

            Date = curDayFormat.format(date);
            Month = curMonthFormat.format(date);
            Year = curYearFormat.format(date);

            tvDate.setText(curYearFormat.format(date) + "/" + parseInt(curMonthFormat.format(date)) + "/" + parseInt(curDayFormat.format(date)));
        }

        ipAddress = new IpAddress();

        phoneNum = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            phoneNum = telephonyManager.getLine1Number();
            if (phoneNum.startsWith("+82")) {
                phoneNum = phoneNum.replace("+82", "0");
            }
        }

        final ListView listView = (ListView) findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter2(items, this);
        listView.setAdapter(listViewAdapter);

        new HttpTask(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    if (jObj.getString("msg").equals("ok")) {
                        id = jObj.getString("id");
                        new HttpTask(new OnCompletionListener() {
                            @Override
                            public void onComplete(String result2) {
                                try {
                                    JSONObject jObj2 = new JSONObject(result2);
                                    JSONArray msg = jObj2.getJSONArray("msg");
                                    for(int i = 0; i < msg.length(); i++){
                                        items.add(new MyItem2(Integer.parseInt(msg.getJSONObject(i).getString("money")), msg.getJSONObject(i).getString("sex"), Integer.parseInt(msg.getJSONObject(i).getString("other")), Integer.parseInt(msg.getJSONObject(i).getString("num"))));
                                    }
                                    listViewAdapter.notifyDataSetInvalidated();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int totalSpend = 0, totalSave = 0;
                                for(int i = 0; i < items.size(); i++){
                                    if(items.get(i).getOther() == 0)
                                        totalSpend += items.get(i).getMoney();
                                    if(items.get(i).getOther() == 1)
                                        totalSave += items.get(i).getMoney();
                                }
                                MyView myView = (MyView)findViewById(R.id.myview);
                                if(totalSpend+totalSave != 0)
                                    myView.ratio = (float)(totalSpend) / (totalSave + totalSpend);
                                else
                                    myView.ratio = 0.0f;
                                myView.invalidate();
                            }
                        }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=dayReturn&id=" + id + "&year=" + Year + "&month=" + Month + "&date=" + Date);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);

        ImageView leftArrow = (ImageView)findViewById(R.id.leftarrow);
        ImageView rightArrow = (ImageView)findViewById(R.id.rightarrow);
        ImageView add = (ImageView)findViewById(R.id.add);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMoney = 0;
                SpendMoney = 0;

                date.setDate(date.getDate() - 1);
                Date = curDayFormat.format(date);
                Month = curMonthFormat.format(date);
                Year = curYearFormat.format(date);

                tvDate.setText(curYearFormat.format(date) + "/" + parseInt(curMonthFormat.format(date)) + "/" + parseInt(curDayFormat.format(date)));
                items.clear();
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                id = jObj.getString("id");
                                new HttpTask(new OnCompletionListener() {
                                    @Override
                                    public void onComplete(String result2) {
                                        try {
                                            JSONObject jObj2 = new JSONObject(result2);
                                            JSONArray msg = jObj2.getJSONArray("msg");
                                            for(int i = 0; i < msg.length(); i++){
                                                items.add(new MyItem2(Integer.parseInt(msg.getJSONObject(i).getString("money")), msg.getJSONObject(i).getString("sex"), Integer.parseInt(msg.getJSONObject(i).getString("other")), Integer.parseInt(msg.getJSONObject(i).getString("num"))));
                                            }
                                            listViewAdapter.notifyDataSetInvalidated();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        int totalSpend = 0, totalSave = 0;
                                        for(int i = 0; i < items.size(); i++){
                                            if(items.get(i).getOther() == 0)
                                                totalSpend += items.get(i).getMoney();
                                            if(items.get(i).getOther() == 1)
                                                totalSave += items.get(i).getMoney();
                                        }
                                        MyView myView = (MyView)findViewById(R.id.myview);
                                        if(totalSpend+totalSave != 0)
                                            myView.ratio = (float)(totalSpend) / (totalSave + totalSpend);
                                        else
                                            myView.ratio = 0.0f;
                                        myView.invalidate();
                                    }
                                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=dayReturn&id=" + id + "&year=" + Year + "&month=" + Month + "&date=" + Date);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMoney = 0;
                SpendMoney = 0;

                date.setDate(date.getDate() + 1);
                Date = curDayFormat.format(date);
                Month = curMonthFormat.format(date);
                Year = curYearFormat.format(date);

                tvDate.setText(curYearFormat.format(date) + "/" + parseInt(curMonthFormat.format(date)) + "/" + parseInt(curDayFormat.format(date)));
                items.clear();
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                id = jObj.getString("id");
                                new HttpTask(new OnCompletionListener() {
                                    @Override
                                    public void onComplete(String result2) {
                                        try {
                                            JSONObject jObj2 = new JSONObject(result2);
                                            JSONArray msg = jObj2.getJSONArray("msg");
                                            for(int i = 0; i < msg.length(); i++){
                                                items.add(new MyItem2(Integer.parseInt(msg.getJSONObject(i).getString("money")), msg.getJSONObject(i).getString("sex"), Integer.parseInt(msg.getJSONObject(i).getString("other")), Integer.parseInt(msg.getJSONObject(i).getString("num"))));
                                            }
                                            listViewAdapter.notifyDataSetInvalidated();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        int totalSpend = 0, totalSave = 0;
                                        for(int i = 0; i < items.size(); i++){
                                            if(items.get(i).getOther() == 0)
                                                totalSpend += items.get(i).getMoney();
                                            if(items.get(i).getOther() == 1)
                                                totalSave += items.get(i).getMoney();
                                        }
                                        MyView myView = (MyView)findViewById(R.id.myview);
                                        if(totalSpend+totalSave != 0)
                                            myView.ratio = (float)(totalSpend) / (totalSave + totalSpend);
                                        else
                                            myView.ratio = 0.0f;
                                        myView.invalidate();
                                    }
                                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=dayReturn&id=" + id + "&year=" + Year + "&month=" + Month + "&date=" + Date);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
            }
        });

        final View.OnClickListener leftListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mCustomDialog.spend.isChecked() && !mCustomDialog.save.isChecked())
                    Toast.makeText(getApplicationContext(), "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                else if(mCustomDialog.money.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "0원은 입력이 불가합니다..", Toast.LENGTH_SHORT).show();
                }
                else {
                    id = null;
                    new HttpTask(new OnCompletionListener() {
                        @Override
                        public void onComplete(String result) {
                            try {
                                JSONObject jObj = new JSONObject(result);
                                if (jObj.getString("msg").equals("ok")) {
                                    String money = mCustomDialog.money.getText().toString();
                                    sex = null;
                                    other = null;
                                    if(mCustomDialog.spend.isChecked())
                                        other = "0";
                                    if(mCustomDialog.save.isChecked())
                                        other = "1";
                                    id = jObj.getString("id");
                                    if(jObj.getString("user1").equals(phoneNum))
                                        sex = jObj.getString("user1sex");
                                    if(jObj.getString("user2").equals(phoneNum))
                                        sex = jObj.getString("user2sex");
                                    new HttpTask(new OnCompletionListener() {
                                        @Override
                                        public void onComplete(String result2) {
                                            try {
                                                JSONObject jObj2 = new JSONObject(result2);
                                                if (jObj2.getString("msg").equals("ok")) {
                                                    items.clear();
                                                    new HttpTask(new OnCompletionListener() {
                                                        @Override
                                                        public void onComplete(String result) {
                                                            try {
                                                                JSONObject jObj = new JSONObject(result);
                                                                if (jObj.getString("msg").equals("ok")) {
                                                                    id = jObj.getString("id");
                                                                    new HttpTask(new OnCompletionListener() {
                                                                        @Override
                                                                        public void onComplete(String result2) {
                                                                            try {
                                                                                JSONObject jObj2 = new JSONObject(result2);
                                                                                JSONArray msg = jObj2.getJSONArray("msg");
                                                                                for(int i = 0; i < msg.length(); i++){
                                                                                    items.add(new MyItem2(Integer.parseInt(msg.getJSONObject(i).getString("money")), msg.getJSONObject(i).getString("sex"), Integer.parseInt(msg.getJSONObject(i).getString("other")), Integer.parseInt(msg.getJSONObject(i).getString("num"))));
                                                                                }
                                                                                listViewAdapter.notifyDataSetInvalidated();
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            int totalSpend = 0, totalSave = 0;
                                                                            for(int i = 0; i < items.size(); i++){
                                                                                if(items.get(i).getOther() == 0)
                                                                                    totalSpend += items.get(i).getMoney();
                                                                                if(items.get(i).getOther() == 1)
                                                                                    totalSave += items.get(i).getMoney();
                                                                            }
                                                                            MyView myView = (MyView)findViewById(R.id.myview);
                                                                            if(totalSpend+totalSave != 0)
                                                                                myView.ratio = (float)(totalSpend) / (totalSave + totalSpend);
                                                                            else
                                                                                myView.ratio = 0.0f;
                                                                            myView.invalidate();
                                                                        }
                                                                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=dayReturn&id=" + id + "&year=" + Year + "&month=" + Month + "&date=" + Date);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
                                                    listViewAdapter.notifyDataSetInvalidated();
                                                    mCustomDialog.dismiss();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=inputCalendar&id=" + id + "&sex=" + sex + "&other=" + other +
                                    "&money=" + money + "&category=RR&content=RRR&year=" + Year + "&month=" + Month + "&date=" + Date);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
                }
            }
        };

        final View.OnClickListener rightListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomDialog.dismiss();
            }
        };
        mCustomDialog = new CustomDialog(this,
                "내용 추가",
                "내요오오오옹",
                leftListener,
                rightListener);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomDialog.show();
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}

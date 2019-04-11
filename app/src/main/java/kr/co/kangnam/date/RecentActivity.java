package kr.co.kangnam.date;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aks56 on 2018-05-07.
 */

public class RecentActivity extends Activity {
    ArrayList<MyItem> items;
    ListViewAdapter listViewAdapter;
    IpAddress ipAddress;
    String phoneNum;
    SharedPreferences SettingInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_layout);

        SettingInformation = getSharedPreferences("setting", 0);
        ipAddress = new IpAddress();
        items = new ArrayList<>();

        View toolform = (View) findViewById(R.id.layout_Form);
        TextView mainText = (TextView) toolform.findViewById(R.id.Activity_Title_Name);
        mainText.setText("연동 신청 내역");
        ImageView mainImage = (ImageView) toolform.findViewById(R.id.Tool);
        mainImage.setVisibility(View.INVISIBLE);

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
        }else{
            phoneNum = telephonyManager.getLine1Number();
            if(phoneNum.startsWith("+82")){
                phoneNum = phoneNum.replace("+82", "0");
            }
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter(items, this);
        listView.setAdapter(listViewAdapter);

        new HttpTask(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray msg = jObj.getJSONArray("msg");
                    for(int i = 0; i < msg.length(); i++){
                        items.add(new MyItem(msg.getJSONObject(i).getString("user1sex"), msg.getJSONObject(i).getString("user1")));
                    }
                    listViewAdapter.notifyDataSetInvalidated();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=recent&user2=" + phoneNum + "&other=1");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                SharedPreferences.Editor editor = SettingInformation.edit();
                                editor.putString("index", "1");
                                editor.commit();

                                Toast.makeText(getApplicationContext(), "연동에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=otherUpdate&user1=" + items.get(i).getNum() + "&user2=" + phoneNum);
            }
        });
    }
}

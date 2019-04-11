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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by aks56 on 2018-05-03.
 */

public class LoginActivity extends Activity {
    String phoneNum;
    IpAddress ipAddress;
    boolean flag;
    SharedPreferences SettingInformation;

    // 뒤로 가기 버튼 눌린 시간 측정
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        SettingInformation = getSharedPreferences("setting", 0);

        if(!SettingInformation.getString("index","").equals("")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }else {
            //editor.putString("index", "0"); // 0 혼자사용 1 연동 대기중 2 연동 완료

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

            flag = false;
            new HttpTask(new OnCompletionListener() {
                @Override
                public void onComplete(String result) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        JSONArray msg = jObj.getJSONArray("msg");
                        for (int i = 0; i < msg.length(); i++) {
                            if (msg.getJSONObject(i).getString("user1").equals(phoneNum) || msg.getJSONObject(i).getString("user2").equals(phoneNum)) {
                                flag = true;
                                break;
                            }
                        }

                        if (flag == true) {
                            SharedPreferences.Editor editor = SettingInformation.edit();
                            editor.putString("index", "1");
                            editor.commit();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=userReturn");

        }

        View toolform = (View) findViewById(R.id.layout_Form);
        TextView mainText = (TextView) toolform.findViewById(R.id.Activity_Title_Name);
        mainText.setText("로그인");
        ImageView mainImage = (ImageView) toolform.findViewById(R.id.Tool);
        mainImage.setVisibility(View.INVISIBLE);

        Button aloneBtn = (Button)findViewById(R.id.use_alone);
        Button togetherBtn = (Button) findViewById(R.id.use_together);
        Button recentBtn = (Button) findViewById(R.id.use_together_recent);

        aloneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNum != null) {
                    new HttpTask(new OnCompletionListener() {
                        @Override
                        public void onComplete(String result) {
                            try {
                                JSONObject jObj = new JSONObject(result);
                                if (jObj.getString("msg").equals("ok")) {
                                    SharedPreferences.Editor editor = SettingInformation.edit();
                                    editor.putString("index", "1");
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=add&user1=" + phoneNum + "&other=0");
                }else{
                    Toast.makeText(getApplicationContext(), "권한을 승락해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        togetherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNum != null) {
                    Intent intent = new Intent(getApplicationContext(), ToghterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else{
                    Toast.makeText(getApplicationContext(), "권한을 승락해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNum != null) {
                    Intent intent = new Intent(getApplicationContext(), RecentActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else{
                    Toast.makeText(getApplicationContext(), "권한을 승락해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //뒤로가기를 두번 누르면 꺼지는 설정
    public void onBackPressed(){
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}

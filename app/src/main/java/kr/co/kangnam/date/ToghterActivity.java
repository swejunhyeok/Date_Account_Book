package kr.co.kangnam.date;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by aks56 on 2018-05-03.
 */

public class ToghterActivity extends Activity {
    IpAddress ipAddress;
    String user1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toghter_layout);

        ipAddress = new IpAddress();

        View toolform = (View) findViewById(R.id.layout_Form);
        TextView mainText = (TextView) toolform.findViewById(R.id.Activity_Title_Name);
        mainText.setText("연동 신청");
        ImageView mainImage = (ImageView) toolform.findViewById(R.id.Tool);
        mainImage.setVisibility(View.INVISIBLE);

        final RadioButton man = (RadioButton)findViewById(R.id.man);
        final RadioButton woman = (RadioButton)findViewById(R.id.woman);
        final EditText phoneNum = (EditText)findViewById(R.id.phoneNum);
        Button sendBtn = (Button)findViewById(R.id.sendBtn);

        user1 = null;
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
            user1 = telephonyManager.getLine1Number();
            if(user1.startsWith("+82")){
                user1 = user1.replace("+82", "0");
            }
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((man.isChecked() || woman.isChecked()) && !phoneNum.getText().toString().equals("")){
                    String user1sex = null;
                    String user2sex = null;
                    if(man.isChecked()) {
                        user1sex = "M";
                        user2sex = "W";
                    }
                    else if(woman.isChecked()) {
                        user1sex = "W";
                        user2sex = "M";
                    }
                    new HttpTask(new OnCompletionListener() {
                        @Override
                        public void onComplete(String result) {
                            try {
                                JSONObject jObj = new JSONObject(result);
                                if (jObj.getString("msg").equals("ok")) {
                                    Toast.makeText(getApplicationContext() ,"연동 신청 완료", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=add&user1=" + user1 + "&user2=" + phoneNum.getText().toString() + "&user1sex=" + user1sex + "&user2sex=" + user2sex + "&other=1");
                }else{
                    Toast.makeText(getApplicationContext() ,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package kr.co.kangnam.date;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by aks56 on 2018-05-05.
 */

public class LogoActivity extends Activity {
    int val  = 0;
    Handler handler;
    boolean HandlerStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);

        //사용자에게 권한 받아오기..
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            // 승낙 상태
        }else {
            // 거절 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
                // 다시보지 않기 체크 x
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            // 승낙 상태
        }else {
            // 거절 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
                // 다시보지 않기 체크 x
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            // 승낙 상태
        }else {
            // 거절 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)){
                // 다시보지 않기 체크 x
            }
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS},0);

        // 3초후에 화면전환되기 위함..
        handler = new Handler(){
            public void handleMessage(Message msg){
                if(HandlerStart) {
                    val++;
                }
                if (val < 3)
                    handler.sendEmptyMessageDelayed(0, 1000);
                else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    // 화면 전환효과 없애기....
                    overridePendingTransition(0, 0);
                }
            }
        };
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            //만약 권한 설정에 대한 응답이 끝나면 그로부터 3초지나고 화면 전환이 일어나기 위한 플래그 변수
            HandlerStart = true;
        }
    }

    // 뒤도가기를 눌러도 꺼지지 않도록 함..
    public void onBackPressed(){
        return;
    }
}

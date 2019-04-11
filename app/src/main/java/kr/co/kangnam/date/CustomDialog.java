package kr.co.kangnam.date;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by aks56 on 2018-05-08.
 */

public class CustomDialog extends Dialog {
    String mTitle;
    String mCotent;
    EditText money;
    RadioButton spend;
    RadioButton save;
    View.OnClickListener mLeftClickListener;
    View.OnClickListener mRightClickListener;
    public CustomDialog(@NonNull Context context, String title, View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
    }

    public CustomDialog(@NonNull Context context, String title, String content, View.OnClickListener leftListener, View.OnClickListener rightListener){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mCotent = content;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_layout);

        View toolform = (View) findViewById(R.id.layout_Form);
        TextView mainText = (TextView) toolform.findViewById(R.id.Activity_Title_Name);
        mainText.setText(mTitle);
        ImageView mainImage = (ImageView) toolform.findViewById(R.id.Tool);
        mainImage.setVisibility(View.INVISIBLE);

        money = (EditText) findViewById(R.id.money);
        spend = (RadioButton) findViewById(R.id.spend);
        save = (RadioButton) findViewById(R.id.save);

        Button ok = (Button) findViewById(R.id.ok);
        Button cancle = (Button) findViewById(R.id.cancle);
        cancle.setOnClickListener(mRightClickListener);
        ok.setOnClickListener(mLeftClickListener);
    }
}

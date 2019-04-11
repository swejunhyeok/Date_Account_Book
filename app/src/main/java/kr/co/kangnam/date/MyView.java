package kr.co.kangnam.date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by aks56 on 2018-05-08.
 */

public class MyView extends View {
    public float ratio;
    public MyView(Context context) {
        super(context);
        ratio = 0.0f;
    }
    public MyView(Context context, AttributeSet att){
        super(context, att);
        ratio = 0.0f;
    }
    @Override
    protected void onDraw(Canvas canvas){
        Paint paint = new Paint();
        if(ratio != 0.0f) {
            paint.setColor(Color.RED);
            canvas.drawRect(0, 0, ratio * canvas.getWidth(), canvas.getHeight(), paint);
            paint.setColor(Color.BLUE);
            canvas.drawRect(ratio * canvas.getWidth(), 0, canvas.getWidth(), canvas.getHeight(), paint);
        }else{
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        }
    }
}

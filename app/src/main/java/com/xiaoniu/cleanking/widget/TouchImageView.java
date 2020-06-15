package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author zhengzhihao
 * @date 2020/6/11 17
 * @mail：zhengzhihao@hellogeek.com
 */
public class TouchImageView extends AppCompatImageView {
    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.animate().scaleX((float) 0.92).setDuration(100);
                this.animate().scaleY((float) 0.92).setDuration(100);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.animate().scaleX((float) 1.0).setDuration(30);
                this.animate().scaleY((float) 1.0).setDuration(30);
                break;
        }
        return super.onTouchEvent(event);
    }


}

package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

    private boolean isIntercept;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && isIntercept) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否阻塞事件
     * @param intercept
     */
    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }
}

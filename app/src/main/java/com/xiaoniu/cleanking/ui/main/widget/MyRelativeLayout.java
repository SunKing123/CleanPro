package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {

    private boolean isIntercept;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/11
 */
public class OperatorNestedScrollView extends JudgeNestedScrollView {

    private List<OnTouchDelegate> onTouchDelegates = new ArrayList<>();

    public OperatorNestedScrollView(Context context) {
        super(context);
    }

    public OperatorNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OperatorNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!onTouchDelegates.isEmpty()) {
            for (OnTouchDelegate delegate : onTouchDelegates) {
                delegate.onTouchEvent(ev);
            }
        }
        return super.onTouchEvent(ev);
    }

    public void attachTouchListener(OnTouchDelegate delegate) {
        if (!onTouchDelegates.contains(delegate)) {
            onTouchDelegates.add(delegate);
        }
    }

    public void detachTouchListener(OnTouchDelegate delegate) {
        if (!onTouchDelegates.contains(delegate)) {
            onTouchDelegates.remove(delegate);
        }
    }

    public interface OnTouchDelegate {
        void onTouchEvent(MotionEvent ev);
    }
}
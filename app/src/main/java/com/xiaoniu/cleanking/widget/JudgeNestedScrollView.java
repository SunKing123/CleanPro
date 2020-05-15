package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/9
 */
public class JudgeNestedScrollView extends NestedScrollView {

    private boolean isNeedScroll = true;
    private float xDistance, yDistance, xLast, yLast;
    private int scaledTouchSlop;

    public JudgeNestedScrollView(Context context) {
        super(context, null);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isNeedScroll) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                return !(xDistance >= yDistance || yDistance < scaledTouchSlop) && isNeedScroll;
            default:
                break;
        }
//        Log.d("JudgeNestedScrollView", "!-->onInterceptTouchEvent----isNeedScroll:"+isNeedScroll);
        return super.onInterceptTouchEvent(ev);  //
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isNeedScroll) {
            return false;
        }
        return isNeedScroll && super.onTouchEvent(ev);
    }

    /*
        该方法用来处理NestedScrollView是否拦截滑动事件
         */
    public void setNeedScroll(boolean isNeedScroll) {
//        Log.d("JudgeNestedScrollView", "!--->setNeedScroll--isNeedScroll:"+isNeedScroll);
        this.isNeedScroll = isNeedScroll;
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isNeedScroll) {
            return false;
        }
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (!isNeedScroll) {
            return false;
        }
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isNeedScroll() {
        return isNeedScroll;
    }

    @Override
    public void requestLayout() {
        if (getParent() != null) {
            boolean layoutRequested = getParent().isLayoutRequested();
//            Log.d("JudgeNestedScrollView", "!--->requestLayout---:" + layoutRequested);
        }
        super.requestLayout();
    }
}

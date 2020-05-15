package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/9
 */
public class MeasureViewPager extends ViewPager {

    private static final String TAG = "MeasureViewPager";

    private boolean isNeedScroll = true;
    private boolean isClickTab = false;
    private float xDistance, yDistance, xLast, yLast;
    private int scaledTouchSlop;
    private boolean isVectral = false;

//    private int height;
//    private boolean isZgjiemeng = false;
//    private float topHeight;

    public MeasureViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        int navigationBarHeight = 0;
//        String brand = Build.BRAND;
//        if (DeviceUtil.hasNavBar(this.getContext()) && DeviceUtil.hasNavigationBar(this.getContext()) && !"SMARTISAN".equals(brand)) {
//            navigationBarHeight = DeviceUtil.getNavigationBarHeight(this.getContext());
//        }
//        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MeasureViewPager);
//        try {
//            topHeight = typedArray.getDimension(R.styleable.MeasureViewPager_topHeightDp, 0);
//            int statusBarHeight = ScreenUtil.getStatusBarHeight(context);
//            height = (int) (ScreenUtil.getScreenHeightPx(getContext()) - navigationBarHeight - topHeight) - statusBarHeight;
//        } catch (Exception e) {
//
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (isZgjiemeng) {
//            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isVectral = false;
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
                if (!isNeedScroll) {
                    return super.dispatchTouchEvent(ev);
                }
                boolean  b = (xDistance < yDistance && yDistance >= scaledTouchSlop);
                isVectral = !(xDistance >= yDistance || yDistance < scaledTouchSlop) && isNeedScroll;
//                Log.w("MeasureViewPager", "!--->dispatchTouchEvent---MOVE--xDistance ：" + xDistance + "---yDistance:" + yDistance + "; isVectral:"+isVectral +"; b:"+b);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d("MeasureViewPager", "!--->onInterceptTouchEvent--isVectral:"+isVectral);
        return isVectral || super.onInterceptTouchEvent(ev);
    }

    public boolean isNeedScroll() {
        return isNeedScroll;
    }

    public void setNeedScroll(boolean needScroll) {
        isNeedScroll = needScroll;
    }

    public boolean isClickTab() {
        return isClickTab;
    }

    public void setClickTab(boolean clickTab) {
        isClickTab = clickTab;
    }
}
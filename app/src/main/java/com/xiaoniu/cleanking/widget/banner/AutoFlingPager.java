package com.xiaoniu.cleanking.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;

public class AutoFlingPager extends ViewPager {

    private int mAutoFlingTime = 4000;
    private final int HANDLE_FLING = 0;
    private Handler mHandler;

    public AutoFlingPager(Context context) {
        super(context);
    }

    public AutoFlingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoFlingTime(int autoFlingTime) {
        mAutoFlingTime = autoFlingTime;
    }

    public void setDuration(int duration) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller fixedSpeedScroller = new FixedSpeedScroller(getContext());
            fixedSpeedScroller.setDuration(duration);
            field.set(this, fixedSpeedScroller);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        stop();
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    next();
                    start();
                }
            };
        }
        Message message = mHandler.obtainMessage(HANDLE_FLING);
        mHandler.sendMessageDelayed(message, mAutoFlingTime);
    }

    public void stop() {
        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_FLING);
        }
    }

    private void next() {
        AutoFlingPagerAdapter<?> adapter = (AutoFlingPagerAdapter<?>) getAdapter();
        if (adapter.getRealCount() > 0) {
            int nextIndex = getCurrentItem() + 1 % adapter.getRealCount();
            this.setCurrentItem(nextIndex);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            start();
        } else {
            stop();
        }
        return super.onTouchEvent(event);
    }

}

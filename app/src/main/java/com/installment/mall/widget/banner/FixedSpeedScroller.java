package com.installment.mall.widget.banner;

import android.content.Context;
import android.widget.Scroller;

public class FixedSpeedScroller extends Scroller {

    private int mDuration = 2000;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getmDuration() {
        return mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

}

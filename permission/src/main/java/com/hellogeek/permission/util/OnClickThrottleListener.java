package com.hellogeek.permission.util;

import android.view.View;


/**
 * deprecation:防止双点监听
 * author:ayb
 * time:2017/7/17
 */
public abstract class OnClickThrottleListener implements View.OnClickListener {
    protected abstract void onThrottleClick(View v);

    @Override
    public void onClick(View v) {
        onThrottleClick(v);
    }

}

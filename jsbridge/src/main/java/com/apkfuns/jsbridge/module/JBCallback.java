package com.apkfuns.jsbridge.module;


import androidx.annotation.UiThread;

/**
 * Created by pengwei on 2017/6/11.
 */

public interface JBCallback {
    @UiThread
    void apply(Object... args);
}

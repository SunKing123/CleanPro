package com.xiaoniu.cleanking.lifecyler;

import android.app.Activity;

/**
 * @author: docking
 * @date: 2019/9/6 11:46
 * @description: todo ...
 **/
public interface LifecycleListener {
    void onBecameForeground(Activity activity);

    void onBecameBackground(Activity activity);
}

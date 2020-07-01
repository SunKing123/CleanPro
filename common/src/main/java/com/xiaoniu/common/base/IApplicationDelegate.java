package com.xiaoniu.common.base;

import android.app.Application;

import androidx.annotation.Keep;

/**
 * Manifest文件中配置如下信息，必须继承BaseApplication
 * <meta-data
 * android:name="com.example.xxxApplication"
 * android:value="IModuleConfig" />
 */
@Keep
public interface IApplicationDelegate {

    void onCreate(Application application);

    void onTerminate();

    void onLowMemory();

    void onTrimMemory(int level);

}

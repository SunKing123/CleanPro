package com.xiaoniu.common.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.RequestParamInterceptor;
import com.xiaoniu.common.http.model.CommonResult;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.ManifestParser;

import java.util.List;

public class BaseApplication extends Application {

    private List<IApplicationDelegate> mAppDelegateList;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ContextUtils.init(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppDelegateList = new ManifestParser(this).parse();
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onCreate(this);
        }
        initHttp();
    }

    private void initHttp() {
        //全局设置，所有请求起作用
        EHttp.Builder builder = new EHttp.Builder();
        builder.apiResult(CommonResult.class);
        builder.addInterceptor(new RequestParamInterceptor());
        EHttp.init(builder);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onTerminate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onTrimMemory(level);
        }
    }
}

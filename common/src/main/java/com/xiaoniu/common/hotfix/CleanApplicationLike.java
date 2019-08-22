package com.xiaoniu.common.hotfix;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.xiaoniu.common.base.IApplicationDelegate;
import com.xiaoniu.common.hotfix.log.MyLogImp;
import com.xiaoniu.common.hotfix.utils.ApplicationContext;
import com.xiaoniu.common.hotfix.utils.TinkerManager;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.model.CommonResult;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.ManifestParser;

import java.util.List;


/**
 * Created by admin on 2017/6/8.
 */

public class CleanApplicationLike extends DefaultApplicationLike {

    private static final String TAG = "Tinker.CleanApplicationLike";
    private static CleanApplicationLike sInstance;
    private List<IApplicationDelegate> mAppDelegateList;

    public CleanApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                                long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        sInstance = this;
        ContextUtils.init(base);
        Log.i("123", "onBaseContextAttached" + getApplication());
        MultiDex.install(base);
        ApplicationContext.application = getApplication();
        ApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    /**
     * @return App 全局上下文
     */
    public static CleanApplicationLike getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("123", "onCreate" + getApplication());
        mAppDelegateList = new ManifestParser(getApplication()).parse();
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onCreate(getApplication());
        }
        initHttp();
    }

    private void initHttp() {
        //全局设置，所有请求起作用
        EHttp.Builder builder = new EHttp.Builder();
        builder.apiResult(CommonResult.class);
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

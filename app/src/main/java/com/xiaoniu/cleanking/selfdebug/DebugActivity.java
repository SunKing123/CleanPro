package com.xiaoniu.cleanking.selfdebug;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

import static android.view.View.VISIBLE;

/**
 * deprecation:调试页面
 * author:ayb
 * time:2018/11/28
 */
public class DebugActivity extends BaseActivity {
    private TextView tv_hide_icon;
    private FrameLayout frame_layout;
    private static final String TAG = "DebugActivity";
    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initView() {
        frame_layout = findViewById(R.id.frame_layout);
        tv_hide_icon = findViewById(R.id.tv_hide_icon);
        tv_hide_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideIcon();
//                startFullInsertPage(DebugActivity.this);
//                enableOtherComponent();
            }
        });

    }

    public void close(View view) {
        finish();
    }

    public void toHomeClean(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=0"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=4";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeTools(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=1"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=1";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeNews(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=2"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=2";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeMine(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=3"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=3";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toH5(View view) {
        //jump 协议
//        "cleanking://com.xiaoniu.cleanking/jump?url=XXXX"
        String url = BuildConfig.Base_H5_Host + "/userAgreement.html";
        String jump = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.JUMP + "?url=";
        String jumpParams = "&is_no_title=0&h5_title=协议";
        String scheme = jump + url + jumpParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toWeChatClean(View view) {
        //原生不带参数 native_no_params协议
//        "cleanking://com.xiaoniu.cleanking/native_no_params?a_name=包名.ui.后面的路径"

//        SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "tool.notify.activity.NotifyCleanGuideActivity";
        String packagePath = "tool.wechat.activity.WechatCleanHomeActivity";
        String schemeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE_NO_PARAMS +
                "?" + SchemeConstant.ANDROID_NAME + "=";
        String scheme = schemeHeader + packagePath;
        SchemeProxy.openScheme(this, scheme);
    }


    public void getImei() {
        //有没有传过imei
        String imei = DeviceUtils.getIMEI();
        LogUtils.i("--zzh-" + imei);
    }

    /**
     * Hide app's icon
     */
    public void hideIcon() {
        try {
//            ComponentName  orange =  new ComponentName(getApplication(),
//                    "com.xiaoniu.cleanking.splash");
//            ComponentName  orange2 =  new ComponentName(getApplication(),
//                    "com.xiaoniu.cleanking.other");
//            enableComponent(orange2);
//            disableComponent(orange);
            Intent shortcutInfoIntent = new Intent(this, SplashADActivity.class);
            shortcutInfoIntent.setAction(Intent.ACTION_VIEW);
//            QuickUtils.getInstant(this).addShortcut( getString(R.string.app_quick_name), AppUtils.getAppIcon(this,this.getPackageName()),shortcutInfoIntent);


//            PackageManager p = getPackageManager();
//            ComponentName componentName = new ComponentName(this, SplashADActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("zzh", "--" + e.getMessage(), "");
        }
    }

    /**
     * back the app's icon.
     */
    public void backIcon() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, SplashADActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    private PackageManager mPm;
    private void enableComponent(ComponentName componentName){
        if (mPm==null){
            mPm=getPackageManager();
        }
        mPm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    private void disableComponent( ComponentName componentName) {
        PackageManager pm = getPackageManager();
        int state = pm.getComponentEnabledSetting(componentName);
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state) {
            //已经禁用
            return;
        }
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    /**
     * 添加桌面快捷方式
     *
     * @param cx
     * @param name 快捷方式名称
     *             调用示例：      ShortCutUtils.addShortcut(MainActivity.this, name.getText() != null ? name.getText().toString() : "1");
     */
    public static void addShortcut(Activity cx, String name) {
        // TODO: 2017/6/25  创建快捷方式的intent广播
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // TODO: 2017/6/25 添加快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //  快捷图标是允许重复
        shortcut.putExtra("duplicate", false);
        // 快捷图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(cx, R.mipmap.applogo);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // TODO: 2017/6/25 我们下次启动要用的Intent信息
        Intent carryIntent = new Intent(Intent.ACTION_MAIN);
        carryIntent.putExtra("name", name);
        carryIntent.setClassName(cx.getPackageName(), cx.getClass().getName());
        carryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //添加携带的Intent
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, carryIntent);
        // TODO: 2017/6/25  发送广播
        cx.sendBroadcast(shortcut);
    }


    //全局跳转全屏插屏页面
    private void startFullInsertPage(Context context) {
        if (ActivityCollector.isActivityExist(FullPopLayerActivity.class))
            return;
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_02);
        context.startActivity(screenIntent);
    }




    private void enableOtherComponent() {
        ComponentName apple = new ComponentName(getApplication(),
                "com.xiaoniu.cleanking.other");
        PackageManager mPm = getPackageManager();
        mPm.setComponentEnabledSetting(apple,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    //获取广告配置
    public void getAdConfig(View view) {
        startActivity(new Intent(mContext,AdConfigActivity.class));
    }



    //获取广告配置
    public void gotoFullAd(View view) {
        startFullInsertPage(this);
    }


    //获取广告配置
    public void gotoPop(View view) {
//        startPop(this);
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, "newlist_1_1", new AdListener() { //暂时这样
            @Override
            public void adSuccess(AdInfo info) {
                if (null != info) {
                    Log.d(TAG, "adSuccess---home--top =" + info.toString());
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "home_page", "home_page");
                    if (null != frame_layout && null != info.getAdView()) {

                        frame_layout.setVisibility(VISIBLE);
                        frame_layout.removeAllViews();
                        frame_layout.addView(info.getAdView());
                    }
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                if (null == info) return;
                Log.d(TAG, "adExposed---home--top");
          
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "home_page", "home_page", info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "adClicked---home--top");
                if (null == info) return;

            }

            @Override
            public void adClose(AdInfo info) {
                if (null == info) return;

                frame_layout.setVisibility(View.GONE);
                frame_layout.removeAllViews();
//                StatisticsUtils.clickAD("close_click", "病毒查杀激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_end_page", info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                if (null == info) return;
                Log.d(TAG, "adError---home--top=" + info.toString());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "home_page", "home_page");
            }
        });
    }




    //清除冷启动十分逻辑
    public void cleanCodeTime(View view) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.COOL_START_TIME,0).commit();
        ToastUtils.showShort("清除成功！");
    }

    //全局跳转全屏插屏页面
    private void startPop(Context context) {
        if (ActivityCollector.isActivityExist(PopLayerActivity.class))
            return;
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_POPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_02);
        context.startActivity(screenIntent);
    }



}

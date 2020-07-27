package com.xiaoniu.cleanking.selfdebug;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.comm.jksdk.bean.ConfigBean;
import com.comm.jksdk.config.AdsConfig;
import com.comm.jksdk.utils.JsonUtils;
import com.google.gson.Gson;
import com.jess.arms.utils.FileUtils;
import com.orhanobut.logger.Logger;
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod;
import com.xiaoniu.clean.deviceinfo.EasyCpuMod;
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod;
import com.xiaoniu.clean.deviceinfo.EasyNetworkMod;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.deskpop.BatteryPopActivity;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.activity.ExternalSceneActivity;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.OneKeyCircleButtonView;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;
import java.util.Locale;

/**
 * deprecation:调试页面
 * author:ayb
 * time:2018/11/28
 */
public class DebugActivity extends BaseActivity {
    private TextView tv_hide_icon;
    private FrameLayout frame_layout;
    private static final String TAG = "DebugActivity";
    private TextView tv_lottie;
    private LottieAnimationView lottieAnimationView;
    private ImageView icon_app;
    private TextView deviceTempcontent;
    private OneKeyCircleButtonView oneKeyCircleButtonView;

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
        deviceTempcontent = findViewById(R.id.tv_debug_content);
        icon_app = findViewById(R.id.icon_app);
        oneKeyCircleButtonView = findViewById(R.id.view_top);
        tv_lottie = findViewById(R.id.tv_lottie);
        frame_layout = findViewById(R.id.frame_layout);
        tv_hide_icon = findViewById(R.id.tv_hide_icon);
        lottieAnimationView = findViewById(R.id.view_lottie_bottom);
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


    public void goto_midas(View view) {
        startActivity(new Intent(this, MidasDebugPanelActivity.class));
    }

    public void playLottie(View view) {
//        String test = "cleankingmajor://com.hellogeek.cleanking/jump?isfullscreen=1&need_login=&url=http%3A%2F%2F192.168.85.61%3A9999%2Fhtml%2FactivitiesHtml%2FscratchCards%2Fscratch.html%3Fid%3D22%26rondaId%3D3%26awardType%3D1%26hitCode%3D1%26num%3D1222222%26remark%3D%26cardType%3D12312312%26goldSectionNum%3D51%26actRdNum%3D20%3A00%26needRefresh%3D1%26currentPageId%3Dscratch_card_activity_page";
////        String test02 ="cleankingmajor://com.hellogeek.cleanking/jump?isfullscreen=1&amp;need_login=&amp;url=http%3A%2F%2F192.168.85.61";
//        SchemeProxy.openScheme(this, test);
//        oneKeyCircleButtonView.startLottie();
        lottieAnimationView.setAnimation("charging/charging_state01/data.json");
        lottieAnimationView.setImageAssetsFolder("charging/charging_state01/images");
        lottieAnimationView.playAnimation();
        lottieAnimationView.setVisibility(View.VISIBLE);
    }

    public void toHomeClean(View view) {
        //原生带参数 native协议
//        "cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=2"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=4";
        String scheme = nativeHeader + nativeName + nativeParams;
        String mainNav = "cleankingmajor://com.hellogeek.cleanking/native?name=main&main_index=2";
        SchemeProxy.openScheme(this, mainNav);
    }

    public void toHomeTools(View view) {
        //原生带参数 native协议
//        "cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=1"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=1";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeNews(View view) {
        //原生带参数 native协议
//        "cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=2"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=2";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeMine(View view) {
        //原生带参数 native协议
//        "cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=3"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=3";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toH5(View view) {
        //jump 协议
//        "cleankingmajor://com.xiaoniu.cleanking/jump?url=XXXX"
        String url = BuildConfig.Base_H5_Host + "/userAgreement.html";
        String jump = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.JUMP + "?url=";
        String jumpParams = "&is_no_title=0&h5_title=协议";
        String scheme = jump + url + jumpParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toWeChatClean(View view) {
        //原生不带参数 native_no_params协议
//        "cleankingmajor://com.xiaoniu.cleanking/native_no_params?a_name=包名.ui.后面的路径"

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

    private void enableComponent(ComponentName componentName) {
        if (mPm == null) {
            mPm = getPackageManager();
        }
        mPm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableComponent(ComponentName componentName) {
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
//        context.startActivity(screenIntent);
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
        startActivity(new Intent(mContext, AdConfigActivity.class));
    }


    //获取广告配置
    public void gotoFullAd(View view) {
        startFullInsertPage(this);
    }


    //获取广告配置
    public void gotoPop(View view) {
        String cFileName = "ad_config_gj_1.4.5_c1.json";
        ConfigBean assetConfig = new Gson().fromJson(JsonUtils.readJSONFromAsset(DebugActivity.this, cFileName), ConfigBean.class);
        AdsConfig.setAdsInfoslist(assetConfig);
    }


    //清除冷启动十分逻辑
    public void cleanCodeTime(View view) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.COOL_START_TIME, 0).commit();
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


    public void getIcon(View view) {
        FileQueryUtils fileQueryUtils = new FileQueryUtils();
        ApplicationInfo applicationInfo = fileQueryUtils.installedAppList.get(0);
        try {
            Resources resources = mContext.getPackageManager().getResourcesForApplication(applicationInfo);
            LogUtils.i("zz---" + applicationInfo.icon);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon_app.setImageDrawable(resources.getDrawable(applicationInfo.icon, null));
            } else {
                icon_app.setImageDrawable(resources.getDrawable(applicationInfo.icon));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    /*  */

    /**
     * 获取App图标
     *
     * @return
     *//*
    private Drawable getAppIcon(ApplicationInfo applicationInfo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return applicationInfo.loadIcon(mContext.getPackageManager());
        }
        LogUtils.i("zz---icon--"+applicationInfo.icon);
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        applicationInfo.icon;
        return pm.getApplicationIcon(applicationInfo);
    }*/

    StringBuffer deviceinfo = new StringBuffer();

    /**
     * 电池温度
     *
     * @param view
     */
    public void deviceTemp(View view) {

        long time = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BatteryManager mBatteryManager = (BatteryManager) this.getSystemService(Context.BATTERY_SERVICE);
            time = mBatteryManager.computeChargeTimeRemaining();
            // TODO something good with time
        }
        EasyBatteryMod easyBatteryMod = new EasyBatteryMod(this);
        deviceinfo = new StringBuffer();
        deviceinfo.append("电量:" + String.valueOf(easyBatteryMod.getBatteryPercentage()) + "\n");
        deviceinfo.append("剩余充电时间:" + easyBatteryMod.getFullTime() + "\n");
        deviceinfo.append("电池容量：" + String.valueOf(easyBatteryMod.getCapacity()) + "\n");
        deviceinfo.append("电池电压：" + String.valueOf(easyBatteryMod.getBatteryVoltage()) + "\n");
        deviceinfo.append("充电温度：" + String.valueOf(easyBatteryMod.getBatteryTemperature()) + "\n");
        deviceinfo.append("耗电应用：" + NumberUtils.mathRandomInt(5, 15) + "\n\n\n");

        EasyNetworkMod easyNetworkMod = new EasyNetworkMod(this);
        deviceinfo.append("wifi强弱：" + easyNetworkMod.checkWifiState() + "\n");
        deviceinfo.append("wifi名称：" + easyNetworkMod.getWifiSSID() + "\n");
        deviceinfo.append("wifi密码：" + "\n");//todo
        deviceinfo.append("wifi链接设备数量：" + "\n");//todo
        deviceinfo.append("wifi下载速度：" + easyNetworkMod.getWifiLinkSpeed() + "\n\n");


        EasyMemoryMod easyMemoryMod = new EasyMemoryMod(this);

        long b = easyMemoryMod.getTotalRAM();
        deviceinfo.append("总计Ram：" + getUnit(b) + "\n");
        deviceinfo.append("可用Ram：" + getUnit(easyMemoryMod.getAvailableRAM()) + "\n");

        b = easyMemoryMod.getTotalInternalMemorySize();
        deviceinfo.append("内部存储总：" + getUnit(b) + "\n");

        b = easyMemoryMod.getAvailableInternalMemorySize();
        deviceinfo.append("内部存储可用：" + getUnit(b) + "\n");

        EasyCpuMod easyCpuMod = new EasyCpuMod();
        deviceinfo.append("电池温度cpu温度：" + String.valueOf(easyBatteryMod.getBatteryTemperature()) + "\n");
        deviceinfo.append("电量:" + String.valueOf(easyBatteryMod.getBatteryPercentage()) + "\n");
        deviceinfo.append("剩余时间:" + "\n");//todo

//        deviceinfo.append("wifi名称：" + easyNetworkMod.+ "\n");

        deviceTempcontent.setText(deviceinfo);

    }

    public void powerClick(View view){
        Intent screenIntent = new Intent(this, BatteryPopActivity.class);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra(ExternalSceneActivity.SCENE,ExternalSceneActivity.SCENE_WIFI);
        startActivity(screenIntent);
    }




    private String[] units = {"B", "KB", "MB", "GB"};

    /**
     * 单位转换
     */
    private String getUnit(float size) {
        int index = 0;
        while (size > 1024 && index < units.length) {
            size = size / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), " %.1f %s", size, units[index]);
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取手机外部总空间大小
     *
     * @return 总大小，字节为单位
     */
    static public long getTotalExternalMemorySize() {
        if (isSDCardEnable()) {
            //获取SDCard根目录
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }
}

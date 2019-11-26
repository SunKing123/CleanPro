package com.xiaoniu.cleanking.ui.lockscreen;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.VirusKillActivity;
import com.xiaoniu.cleanking.ui.main.bean.LocationInfo;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.ViewUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.lockview.TouchToUnLockView;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.common.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * 锁屏信息流广告页面
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private TouchToUnLockView mUnlockView;
    private ImageView lockDial;
    private ImageView lockCamera;
    private LockExitDialog lockExitDialog;
    private ImageView batteryIcon;
    private RelativeLayout relAd;
    private TextView mLockTime, mLockDate,tv_weather_temp;
    private RelativeLayout rel_clean_file, rel_clean_ram, rel_clean_virus;
    private ImageView iv_file_btn,iv_ram_btn,iv_virus_btn;
    private TextView tv_file_size,tv_ram_size,tv_virus_size;
    private LinearLayout lin_tem_top,lin_tem_bottom;
    private SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    private RxPermissions rxPermissions;
    private LinearLayout linAdLayout;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TextView tv_weather_state,tv_city;
/*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        try {
            ViewGroup layout = (ViewGroup) getWindow().getDecorView();
            layout.removeAllViews();
        } catch (Exception e) {
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        setContentView(R.layout.activity_lock);
        ActivityCollector.addActivity(this, LockActivity.class);
        initView();
        //注册订阅者
        EventBus.getDefault().register(this);
    }

    private void initView() {
        rel_clean_file = ViewUtils.get(this, R.id.rel_clean_file);
        rel_clean_ram = ViewUtils.get(this, R.id.rel_clean_ram);
        rel_clean_virus = ViewUtils.get(this, R.id.rel_clean_virus);

        lin_tem_top = ViewUtils.get(this,R.id.lin_tem_top);
        lin_tem_bottom = ViewUtils.get(this,R.id.lin_tem_bottom);
        tv_weather_state = ViewUtils.get(this,R.id.tv_weather_state);
        tv_city = ViewUtils.get(this,R.id.tv_city);

        rel_clean_file.setOnClickListener(this::onClick);
        rel_clean_ram.setOnClickListener(this::onClick);
        rel_clean_virus.setOnClickListener(this::onClick);
        tv_weather_temp = ViewUtils.get(this,R.id.tv_weather_temp);

        mLockTime = ViewUtils.get(this, R.id.lock_time_txt);
        mLockTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));
        tv_weather_temp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));

        tv_file_size = ViewUtils.get(this,R.id.tv_file_size);
        tv_ram_size = ViewUtils.get(this,R.id.tv_ram_size);
        tv_virus_size = ViewUtils.get(this,R.id.tv_virus_size);

        iv_file_btn = ViewUtils.get(this,R.id.iv_file_btn);
        iv_ram_btn = ViewUtils.get(this,R.id.iv_ram_btn);
        iv_virus_btn = ViewUtils.get(this,R.id.iv_virus_btn);

        mLockDate = ViewUtils.get(this, R.id.lock_date_txt);
        mUnlockView = ViewUtils.get(this, R.id.lock_unlock_view);
        linAdLayout = ViewUtils.get(this, R.id.lock_ad_container);
        relAd = ViewUtils.get(this, R.id.rel_ad);
        mUnlockView.setOnTouchToUnlockListener(new TouchToUnLockView.OnTouchToUnlockListener() {
            @Override
            public void onTouchLockArea() {
            }

            @Override
            public void onSlidePercent(float percent) {

            }

            @Override
            public void onSlideToUnlock() {
                //todo_zzh
                //todo 兼容性优化——重新打开保活service
                Intent i = new Intent(LockActivity.this, LocalService.class);
                i.putExtra("action", "unlock_screen");
                LockActivity.this.startService(i);
                finish();
            }

            @Override
            public void onSlideAbort() {
            }
        });
        registerLockerReceiver();
        setBtnState();

        if(PreferenceUtil.getInstants().getInt("isGetWeatherInfo") ==1){
            lin_tem_top.setVisibility(View.VISIBLE);
            lin_tem_bottom.setVisibility(View.VISIBLE);
            tv_weather_temp.setText( PreferenceUtil.getInstants().get("temperature")+"°");
            tv_weather_state.setText(PreferenceUtil.getInstants().get("skycon"));
            if(TextUtils.isEmpty(PreferenceUtil.getInstants().get("city")))return;
            tv_city.setText(PreferenceUtil.getInstants().get("city"));
        }else{
            lin_tem_top.setVisibility(View.GONE);
            lin_tem_bottom.setVisibility(View.GONE);

        }

    }

    public void setBtnState(){
        if(null!= PreferenceUtil.getInstants().get("lock_pos01")&&  PreferenceUtil.getInstants().get("lock_pos01").length()>2){
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos01"),LockScreenBtnInfo.class);
            if(btnInfo.isNormal()){
                tv_file_size.setVisibility(View.GONE);
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
            }else{
                tv_file_size.setVisibility(View.VISIBLE);
                tv_file_size.setText(btnInfo.getCheckResult()+"MB");
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        }else{
            setRandState(1);
        }

        if(null!= PreferenceUtil.getInstants().get("lock_pos02") &&  PreferenceUtil.getInstants().get("lock_pos02").length()>2){
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos02"),LockScreenBtnInfo.class);
            if(btnInfo.isNormal()){
                tv_ram_size.setVisibility(View.GONE);
                iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
            }else{
                tv_ram_size.setVisibility(View.VISIBLE);
                tv_ram_size.setText(btnInfo.getCheckResult()+"MB");
                iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        }else{
            setRandState(2);
        }

        if(null!= PreferenceUtil.getInstants().get("lock_pos03") &&  PreferenceUtil.getInstants().get("lock_pos03").length()>2){
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos03"),LockScreenBtnInfo.class);
            if(btnInfo.isNormal()){
                if(System.currentTimeMillis()>btnInfo.getReShowTime()){
                    tv_virus_size.setText("检测");
                    tv_virus_size.setVisibility(View.VISIBLE);
                    iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                }else{
                    tv_virus_size.setText("防御");
                    tv_virus_size.setVisibility(View.VISIBLE);
                    iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                }
            }else{
                tv_virus_size.setText("检测");
                tv_virus_size.setVisibility(View.VISIBLE);
                iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        }else{
            setRandState(3);
        }

    }
    //设置随机值
    public void setRandState(int index){
        switch (index){
            case  1:
                tv_file_size.setText(NumberUtils.mathRandom(800,1800)+"MB");
                tv_file_size.setVisibility(View.VISIBLE);
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                break;

            case 2:
                tv_ram_size.setText(NumberUtils.mathRandom(800,1000)+"MB");
                tv_ram_size.setVisibility(View.VISIBLE);
                iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                break;
            case 3:
                tv_virus_size.setText("检测");
                tv_virus_size.setVisibility(View.VISIBLE);
                iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                break;
        }
    }


    public void adInit() {

        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, "lock_screen_advertising", new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "lock_screen", "lock_screen");
                View adView = adManager.getAdView();
                if (adView != null) {
                    relAd.removeAllViews();
                    relAd.addView(adView);
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "lock_screen", "lock_screen", info.getAdTitle());
                LogUtils.e("adExposed");
            }

            @Override
            public void adClicked(AdInfo info) {
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "lock_screen", "lock_screen", info.getAdTitle());
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", "", "", "fail", "lock_screen", "lock_screen");

            }
        });
    }

    private String content = null;

   /* private void checkLockState() {
        if (rxPermissions == null) {
            return;
        }
        if (rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            executorService.execute(() -> {
                content = FileUtils.readTextFile();
                runOnUiThread(() -> {
                    if (!TextUtils.isEmpty(content) && content.contains("com.geek.lw")) {
                        finish();
                    }
                });
                FileUtils.writeTextFile(getPackageName());
            });
        } else {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe((isGranted) -> {
                if (isGranted) {
                    executorService.execute(() -> {
                        content = FileUtils.readTextFile();
                        runOnUiThread(() -> {
                            if (!TextUtils.isEmpty(content) && content.contains("com.geek.qukan.video")) {
                                finish();
                            }
                        });
                        FileUtils.writeTextFile(getPackageName());
                    });
                } else {
                    LogUtils.i("permission dined");
                }
            });
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mUnlockView.startAnim();
        updateTimeUI();

        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_LOCK_SCREEN.equals(switchInfoList.getConfigKey())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (!isOpen) return;
        adInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnlockView.stopAnim();

    }

    private void updateTimeUI() {
        mLockTime.setText(DateUtils.getHourString(this, System.currentTimeMillis()));
        mLockDate.setText(monthFormat.format(GregorianCalendar.getInstance().getTime()) + "  " + weekFormat.format(GregorianCalendar.getInstance().getTime()));
    }

    private void setLockerWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT > 18) {
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(lp);
        window.getDecorView().setSystemUiVisibility(0x0);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_clean_file: //清理
                StatisticsUtils.trackClick("junk_file_click", "垃圾文件点击", "lock_screen", "lock_screen");
                Intent intentClean = new Intent(this, NowCleanActivity.class);
                intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                PreferenceUtil.getInstants().save("lock_action","file");
                startActivity(intentClean);

                break;
            case R.id.rel_clean_ram://一键加速
                StatisticsUtils.trackClick("memory_usage_click", "内存使用点击", "lock_screen", "lock_screen");
                Intent phoneAccessIntent = new Intent(this, PhoneAccessActivity.class);
                phoneAccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                PreferenceUtil.getInstants().save("lock_action","ram");
                startActivity(phoneAccessIntent);
                break;
            case R.id.rel_clean_virus://病毒查杀
                StatisticsUtils.trackClick("virus_killing_click", "病毒查杀点击", "lock_screen", "lock_screen");
                Intent virusIntent = new Intent(this, VirusKillActivity.class);
                virusIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                virusIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                virusIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                virusIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                virusIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                PreferenceUtil.getInstants().save("lock_action","virus");
                startActivity(virusIntent);
                break;

        }
    }

    protected UIChangingReceiver mUIChangingReceiver;

    public void registerLockerReceiver() {
        if (mUIChangingReceiver != null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        mUIChangingReceiver = new UIChangingReceiver();
        registerReceiver(mUIChangingReceiver, filter);
    }

    public void unregisterLockerReceiver() {
        if (mUIChangingReceiver == null) {
            return;
        }
        unregisterReceiver(mUIChangingReceiver);
        mUIChangingReceiver = null;
    }


    private class UIChangingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (!TextUtils.isEmpty(action)) {
                    if (action.equals(Intent.ACTION_TIME_TICK)) {
                        updateTimeUI();
                    }
                    /*else if (action.equals(Intent.ACTION_POWER_CONNECTED) || action.equals(Intent.ACTION_BATTERY_CHANGED)
                            || action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                        batteryIcon.setImageResource(PowerUtil.isCharging(intent) ? R.drawable.lock_battery_charging : R.drawable.lock_battery_normal);
                    }*/
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLockerReceiver();
        EventBus.getDefault().unregister(this);
//
     /*   executorService.execute(() -> {
            FileUtils.writeTextFile("stop");
        });*/
    }

    private void emergencyDial() {
        try {
            Intent intent = new Intent();
            intent.setAction("com.android.phone.EmergencyDialer.DIAL");
            startActivity(intent);
        } catch (Exception e) {
            LogUtils.e("dial error:" + e.getMessage());
        }
    }

    private void openCamera() {
        try {
            if (!rxPermissions.isGranted(Manifest.permission.CAMERA) || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe((isGranted) -> {
                    if (isGranted) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivity(intent);
                    } else {
                        ToastUtils.showShort("相机权限拒绝");
                    }
                });
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        } catch (Exception e) {
            LogUtils.e("open camera error:" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {

    }


    public static void startActivity(Context context) {
        Intent screenIntent = getIntent(context);
        context.startActivity(screenIntent);
    }


    @NonNull
    private static Intent getIntent(Context context) {
        Intent screenIntent = new Intent();
        screenIntent.setClass(context, LockActivity.class);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        return screenIntent;
    }


    /**
     * 重新扫描文件
     */
    @Subscribe
    public void onEventLock(LockScreenBtnInfo btnInfo) {
        if (null != btnInfo) {
            int pos = btnInfo.getPost();
            if (pos == 0) {
                if (btnInfo.isNormal()) {
                    tv_file_size.setVisibility(View.GONE);
                    iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                } else {
                    tv_file_size.setVisibility(View.VISIBLE);
                    tv_file_size.setText(btnInfo.getCheckResult() + "MB");
                    iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                }
            } else if (pos == 1) {
                if (btnInfo.isNormal()) {
                    tv_ram_size.setVisibility(View.GONE);
                    iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                } else {
                    tv_ram_size.setVisibility(View.VISIBLE);
                    tv_ram_size.setText(btnInfo.getCheckResult() + "MB");
                    iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                }
            } else if (pos == 2) {
                if (btnInfo.isNormal()) {
                    if (System.currentTimeMillis() > btnInfo.getReShowTime()) {
                        tv_virus_size.setText("检测");
                        tv_virus_size.setVisibility(View.VISIBLE);
                        iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                    } else {
                        tv_virus_size.setText("防御");
                        tv_virus_size.setVisibility(View.VISIBLE);
                        iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                    }
                } else {
                    tv_virus_size.setText("检测");
                    tv_virus_size.setVisibility(View.VISIBLE);
                    iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                }
            }

        } else {
            setRandState(1);
        }
    }

}

package com.xiaoniu.cleanking.ui.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.AdPreloadingListener;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.VirusKillActivity;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.ViewUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.lockview.TouchToUnLockView;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 锁屏信息流广告页面
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private TouchToUnLockView mUnlockView;
    private RelativeLayout relAd;
    private TextView mLockTime, mLockDate, tv_weather_temp;
    private RelativeLayout rel_clean_file, rel_clean_ram, rel_clean_virus;
    private ImageView iv_file_btn, iv_ram_btn, iv_virus_btn, mErrorAdIv;
    private TextView tv_file_size, tv_ram_size, tv_virus_size;
    private LinearLayout lin_tem_top, lin_tem_bottom, linAdLayout;
    private SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    private TextView tv_weather_state, tv_city;
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
        StatusBarUtil.setTransparentForWindow(this);
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
        mErrorAdIv = ViewUtils.get(this, R.id.error_ad_iv);

        lin_tem_top = ViewUtils.get(this, R.id.lin_tem_top);
        lin_tem_bottom = ViewUtils.get(this, R.id.lin_tem_bottom);
        tv_weather_state = ViewUtils.get(this, R.id.tv_weather_state);
        tv_city = ViewUtils.get(this, R.id.tv_city);

        rel_clean_file.setOnClickListener(this::onClick);
        rel_clean_ram.setOnClickListener(this::onClick);
        rel_clean_virus.setOnClickListener(this::onClick);
        tv_weather_temp = ViewUtils.get(this, R.id.tv_weather_temp);

        mLockTime = ViewUtils.get(this, R.id.lock_time_txt);
        mLockTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));
        tv_weather_temp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));

        tv_file_size = ViewUtils.get(this, R.id.tv_file_size);
        tv_ram_size = ViewUtils.get(this, R.id.tv_ram_size);
        tv_virus_size = ViewUtils.get(this, R.id.tv_virus_size);

        iv_file_btn = ViewUtils.get(this, R.id.iv_file_btn);
        iv_ram_btn = ViewUtils.get(this, R.id.iv_ram_btn);
        iv_virus_btn = ViewUtils.get(this, R.id.iv_virus_btn);

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
                Intent i = new Intent(LockActivity.this, LocalService.class);
                i.putExtra("action", "unlock_screen");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LockActivity.this.startForegroundService(i);
                } else {
                    LockActivity.this.startService(i);
                }
                finish();
            }

            @Override
            public void onSlideAbort() {
            }
        });
        registerLockerReceiver();
        setBtnState();

        if (PreferenceUtil.getInstants().getInt("isGetWeatherInfo") == 1) {
            lin_tem_top.setVisibility(View.VISIBLE);
            lin_tem_bottom.setVisibility(View.VISIBLE);
            String temp = PreferenceUtil.getInstants().get("temperature");
            Double tempDoub = Double.valueOf(temp);
            if (tempDoub >= 0) {
                tv_weather_temp.setText(String.valueOf(tempDoub.intValue()) + "°");
            } else {
                tv_weather_temp.setText("-" + String.valueOf(tempDoub.intValue()) + "°");
            }

            tv_weather_state.setText(PreferenceUtil.getInstants().get("skycon"));
            if (TextUtils.isEmpty(PreferenceUtil.getInstants().get("city"))) return;
            tv_city.setText(PreferenceUtil.getInstants().get("city"));
        } else {
            lin_tem_top.setVisibility(View.GONE);
            lin_tem_bottom.setVisibility(View.GONE);

        }


    }

    public void setBtnState() {
        if (null != PreferenceUtil.getInstants().get("lock_pos01") && PreferenceUtil.getInstants().get("lock_pos01").length() > 2) {
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos01"), LockScreenBtnInfo.class);
            if (btnInfo.isNormal()) {
                tv_file_size.setVisibility(View.GONE);
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
            } else {
                tv_file_size.setVisibility(View.VISIBLE);
                tv_file_size.setText(btnInfo.getCheckResult() + "MB");
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        } else {
            setRandState(1);
        }

        if (null != PreferenceUtil.getInstants().get("lock_pos02") && PreferenceUtil.getInstants().get("lock_pos02").length() > 2) {
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos02"), LockScreenBtnInfo.class);
            if (btnInfo.isNormal()) {
                tv_ram_size.setVisibility(View.VISIBLE);
                tv_ram_size.setText(NumberUtils.mathRandom(15, 30) + "%");
                iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
            } else {
                tv_ram_size.setVisibility(View.VISIBLE);
                tv_ram_size.setText(NumberUtils.mathRandom(70, 85) + "%");
                iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        } else {
            setRandState(2);
        }

        if (null != PreferenceUtil.getInstants().get("lock_pos03") && PreferenceUtil.getInstants().get("lock_pos03").length() > 2) {
            LockScreenBtnInfo btnInfo = new Gson().fromJson(PreferenceUtil.getInstants().get("lock_pos03"), LockScreenBtnInfo.class);
            if (btnInfo.isNormal()) {
                if (System.currentTimeMillis() > btnInfo.getReShowTime()) {
                    tv_virus_size.setText("检测");
                    tv_virus_size.setVisibility(View.VISIBLE);
                    iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                } else {
                    tv_virus_size.setText("防御中");
                    tv_virus_size.setVisibility(View.VISIBLE);
                    iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                }
            } else {
                tv_virus_size.setText("检测");
                tv_virus_size.setVisibility(View.VISIBLE);
                iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
            }
        } else {
            setRandState(3);
        }

    }

    //设置随机值
    public void setRandState(int index) {
        switch (index) {
            case 1:
                tv_file_size.setText(NumberUtils.mathRandom(800, 1800) + "MB");
                tv_file_size.setVisibility(View.VISIBLE);
                iv_file_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                break;
            case 2:
                tv_ram_size.setText(NumberUtils.mathRandom(70, 85) + "%");
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
        adManager.loadAd(this, PositionId.AD_LOCK_SCREEN_ADVERTISING, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (null == info) return;
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "lock_screen", "lock_screen");
                if (info.getAdView() != null && null != relAd) {
                    relAd.removeAllViews();
                    relAd.addView(info.getAdView());
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "lock_screen", "lock_screen", info.getAdTitle());
                LogUtils.e("adExposed");
            }

            @Override
            public void adClicked(AdInfo info) {
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "lock_screen", "lock_screen", info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "lock_screen", "lock_screen");
                }
                Logger.d("zz--" + errorMsg);
                showBottomAd();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null == mUnlockView)
            return;
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
        if (null != mLockDate)
            mLockTime.setText(DateUtils.getHourString(this, System.currentTimeMillis()));
        if (null != mLockDate)
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
                PreferenceUtil.getInstants().save("lock_action", "file");//埋点区分逻辑
                if (PreferenceUtil.getNowCleanTime()) {//是否超过三分钟
                    Intent intentClean = new Intent(this, NowCleanActivity.class);
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraConstant.ACTION_NAME, "lock");
                    intentClean.putExtras(bundle);
                    startActivity(intentClean);
                } else {
                    Intent intentClean = new Intent(this, CleanFinishAdvertisementActivity.class);
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "立即清理");
                    bundle.putString("action", "lock");
                    bundle.putString("num", "");
                    bundle.putString("unit", "");
                    bundle.putString("home", "");
                    intentClean.putExtras(bundle);
                    startActivity(intentClean);
                }

                break;
            case R.id.rel_clean_ram://一键加速
                StatisticsUtils.trackClick("memory_usage_click", "内存使用点击", "lock_screen", "lock_screen");
                PreferenceUtil.getInstants().save("lock_action", "ram");
                if (PreferenceUtil.getCleanTime()) {
                    Intent phoneAccessIntent = new Intent(this, PhoneAccessActivity.class);
                    phoneAccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    phoneAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraConstant.ACTION_NAME, "lock");
                    phoneAccessIntent.putExtras(bundle);
                    startActivity(phoneAccessIntent);
                } else {
                    PreferenceUtil.getInstants().save("lock_action", "ram");//埋点区分逻辑
                    Intent intentClean = new Intent(this, CleanFinishAdvertisementActivity.class);
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "一键加速");
                    bundle.putString("action", "lock");
                    intentClean.putExtras(bundle);
                    startActivity(intentClean);
                }

                break;
            case R.id.rel_clean_virus://病毒查杀
                StatisticsUtils.trackClick("virus_killing_click", "病毒查杀点击", "lock_screen", "lock_screen");
                PreferenceUtil.getInstants().save("lock_action", "virus");//埋点区分逻辑

                if (PreferenceUtil.getVirusKillTime()) {
                    if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getSwitchInfoList()
                            || null == AppHolder.getInstance().getSwitchInfoList().getData()
                            || AppHolder.getInstance().getSwitchInfoList().getData().size() <= 0) {
                        startVirUsKill();
                    } else {
                        for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                            if (PositionId.KEY_VIRUS_JILI.equals(switchInfoList.getConfigKey())) {
                                if (switchInfoList.isOpen()) {
                                    loadGeekAd();
                                } else {
                                    startVirUsKill();
                                }
                            }
                        }
                    }
                } else {
                    Intent intentClean = new Intent(this, CleanFinishAdvertisementActivity.class);
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "病毒查杀");
                    bundle.putString("action", "lock");
                    intentClean.putExtras(bundle);
                    startActivity(intentClean);

                }

                break;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        GeekAdSdk.getAdsManger().preloadingAd(this,PositionId.AD_LOCK_SCREEN_ADVERTISING, new AdPreloadingListener() {
            @Override
            public void adSuccess(AdInfo info) {
//                LogUtils.e(TAG, "DEMO>>>adSuccess， "+ info.toString());
                if(!BuildConfig.SYSTEM_EN.contains("prod"))
                Toast.makeText(getApplicationContext(), "预加载成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
//                LogUtils.e(TAG, "DEMO>>>adError： "+errorMsg);
                if(!BuildConfig.SYSTEM_EN.contains("prod"))
                Toast.makeText(getApplicationContext(), "预加载失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startVirUsKill() {
        Intent virusIntent = new Intent(this, VirusKillActivity.class);
        virusIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        virusIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        virusIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        virusIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        virusIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(ExtraConstant.ACTION_NAME, "lock");
        virusIntent.putExtras(bundle);
        PreferenceUtil.getInstants().save("lock_action", "virus");//埋点逻辑
        startActivity(virusIntent);
    }


    /**
     * 病毒查杀激励视频
     */
    private void loadGeekAd() {
        AdManager mAdManager = GeekAdSdk.getAdsManger();
        if (null == mAdManager) return;
        NiuDataAPI.onPageStart("view_page", "病毒查杀激励视频页浏览");
        NiuDataAPIUtil.onPageEnd("lock_screen", "virus_killing_video_page", "view_page", "病毒查杀激励视频页浏览");
        mAdManager.loadRewardVideoAd(this, "click_virus_killing_ad", "user123", 1, new VideoAdListener() {
            @Override
            public void onVideoResume(AdInfo info) {

            }

            @Override
            public void onVideoRewardVerify(AdInfo info, boolean rewardVerify, int rewardAmount, String rewardName) {

            }

            @Override
            public void onVideoComplete(AdInfo info) {
                NiuDataAPI.onPageStart("view_page", "病毒查杀激励视频结束页浏览");
            }

            @Override
            public void adSuccess(AdInfo info) {

                if (null == info) return;
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "lock_screen", "virus_killing_video_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "lock_screen", "virus_killing_video_page", " ");
            }

            @Override
            public void adClicked(AdInfo info) {
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "lock_screen", "virus_killing_video_page", " ");
            }

            @Override
            public void adClose(AdInfo info) {
                NiuDataAPIUtil.onPageEnd("lock_screen", "virus_killing_video_end_page", "view_page", "病毒查杀激励视频结束页浏览");
                if (null != info) {
                    StatisticsUtils.clickAD("close_click", "病毒查杀激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_page", " ");
                }
                startVirUsKill();
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "home_page", "virus_killing_video_page");
                }
                startVirUsKill();
            }
        });
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
                    tv_ram_size.setVisibility(View.VISIBLE);
                    tv_ram_size.setText(NumberUtils.mathRandom(15, 30) + "%");
                    iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_normal));
                } else {
                    //btnInfo.getCheckResult()
                    tv_ram_size.setVisibility(View.VISIBLE);
                    tv_ram_size.setText(NumberUtils.mathRandom(70, 85) + "%");
                    iv_ram_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                }
            } else if (pos == 2) {
                if (btnInfo.isNormal()) {
                    if (System.currentTimeMillis() > btnInfo.getReShowTime()) {
                        tv_virus_size.setText("检测");
                        tv_virus_size.setVisibility(View.VISIBLE);
                        iv_virus_btn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_lock_btn_hot));
                    } else {
                        tv_virus_size.setText("防御中");
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


    /**
     * 锁屏打底广告
     */
    private int mBottomAdShowCount = 0;

    private void showBottomAd() {
        List<BottoomAdList.DataBean> mBottoomAdList = AppHolder.getInstance().getBottomAdList();
        if (null != mBottoomAdList && mBottoomAdList.size() > 0) {
            for (BottoomAdList.DataBean dataBean : AppHolder.getInstance().getBottomAdList()) {
                if (dataBean.getSwitcherKey().equals(PositionId.KEY_LOCK_SCREEN)) {
                    Logger.d("zz--1" + dataBean.getAdvertPosition());
                    if (dataBean.getShowType() == 1) { //循环
                        mBottomAdShowCount = PreferenceUtil.getBottomLockAdCount();
                        if (mBottomAdShowCount >= dataBean.getAdvBottomPicsDTOS().size() - 1) {
                            PreferenceUtil.saveBottomLockAdCount(0);
                        } else {
                            PreferenceUtil.saveBottomLockAdCount(PreferenceUtil.getBottomLockAdCount() + 1);
                        }
                    } else { //随机
                        if (dataBean.getAdvBottomPicsDTOS().size() == 1) {
                            mBottomAdShowCount = 0;
                        } else {
                            mBottomAdShowCount = new Random().nextInt(dataBean.getAdvBottomPicsDTOS().size() - 1);
                        }
                    }
                    GlideUtils.loadImage(LockActivity.this, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getImgUrl(), mErrorAdIv);
                    mErrorAdIv.setOnClickListener(v -> {
                        AppHolder.getInstance().setCleanFinishSourcePageId("lock_screen");
                        startActivity(new Intent(this, AgentWebViewActivity.class)
                                .putExtra(ExtraConstant.WEB_URL, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getLinkUrl())
                                .putExtra(ExtraConstant.WEB_FROM, "LockActivity"));

                    });
                }
            }
        }
    }

}

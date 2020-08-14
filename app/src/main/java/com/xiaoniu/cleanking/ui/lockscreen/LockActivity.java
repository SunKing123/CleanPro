package com.xiaoniu.cleanking.ui.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppLifecyclesImpl;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.keeplive.service.LocalService;

import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.selfdebug.DebugActivity;
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel;
import com.xiaoniu.cleanking.ui.viruskill.VirusKillActivity;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.ViewUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.lockview.TouchToUnLockView;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.global.UnionConstants;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * 锁屏信息流广告页面
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private TouchToUnLockView mUnlockView;
    private FrameLayout lockAdFrameLayout;
    private TextView mLockTime, mLockDate, tv_weather_temp;
    private RelativeLayout rel_clean_file, rel_clean_ram, rel_clean_virus, rel_interactive;
    private ImageView iv_file_btn, iv_ram_btn, iv_virus_btn, mErrorAdIv, iv_interactive;
    private TextView tv_file_size, tv_ram_size, tv_virus_size;
    private LinearLayout lin_tem_top, lin_tem_bottom, linAdLayout;
    private SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    private TextView tv_weather_state, tv_city;
    private String TAG = "GeekSdk";
    private List<SwitchInfoList.DataBean> dataBeans = new ArrayList<>();
    private List<BottoomAdList.DataBean> bottomAds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        StatusBarUtil.setTransparentForWindow(this);
        setContentView(R.layout.activity_lock);

        //获取总开关数据
        String switchString = MmkvUtil.getSwitchInfo();
        if (!TextUtils.isEmpty(switchString)) {
            SwitchInfoList switchInfoList = new Gson().fromJson(MmkvUtil.getSwitchInfo(), SwitchInfoList.class);
            if (null != switchInfoList)
                dataBeans.addAll(switchInfoList.getData());
        }
        //获取打底广告数据
        String botomAdString = MmkvUtil.getBottoomAdInfo();
        if (!TextUtils.isEmpty(botomAdString)) {
            List<BottoomAdList.DataBean> bottomBeans = new Gson().fromJson(MmkvUtil.getBottoomAdInfo(), new TypeToken<List<BottoomAdList.DataBean>>() {
            }.getType());
            if (null != bottomBeans && bottomBeans.size() > 0) {
                bottomAds.addAll(bottomBeans);
            }
        }

        initView();
        //注册订阅者
        EventBus.getDefault().register(this);
        StatisticsUtils.customTrackEvent("lock_screen_page_custom", "锁屏页面创建时", "lock_screen_page", "lock_screen_page");

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
        lockAdFrameLayout = ViewUtils.get(this, R.id.f_ad_container);

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
                AppLifecyclesImpl.postDelay(() -> EventBus.getDefault().post(new PopEventModel("desktopPop")), 3000);
                finish();
            }

            @Override
            public void onSlideAbort() {
            }
        });
        registerLockerReceiver();
        setBtnState();

       /* if (PreferenceUtil.getInstants().getInt("isGetWeatherInfo") == 1) {
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

        }*/

    }


    /**
     * 设置功能按钮状态
     */
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

    /**
     * 广告加载
     */
    private long mLastTime = 0;


    @Override
    protected void onResume() {
        super.onResume();

        if (null == mUnlockView)
            return;
        try {
            mUnlockView.startAnim();
            updateTimeUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean lock_sw = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_LOCK_SCREEN, PositionId.KEY_ADVERT_LOCK_SCREEN);//锁屏开关
        if (lock_sw) {
            adInit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null == mUnlockView)
            return;
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
                    Intent intentClean = new Intent();
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraConstant.TITLE, "立即清理");
                    bundle.putString("action", "lock");
                    bundle.putString("num", "");
                    bundle.putString("unit", "");
                    bundle.putString("home", "");
                    intentClean.putExtras(bundle);
                    NewCleanFinishPlusActivity.Companion.start(this,intentClean,"立即清理",false);

                }
//                959
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
                    Intent intentClean = new Intent();
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraConstant.TITLE, "一键加速");
                    bundle.putString("action", "lock");
                    intentClean.putExtras(bundle);
                    NewCleanFinishPlusActivity.Companion.start(this,intentClean,"一键加速",false);
                }

                break;
            case R.id.rel_clean_virus://病毒查杀
                StatisticsUtils.trackClick("virus_killing_click", "病毒查杀点击", "lock_screen", "lock_screen");
                PreferenceUtil.getInstants().save("lock_action", "virus");//埋点区分逻辑
                if (PreferenceUtil.getVirusKillTime()) {
                    startVirUsKill();
                } else {
                    Intent intentClean = new Intent();
                    intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intentClean.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraConstant.TITLE, "病毒查杀");
                    bundle.putString("action", "lock");
                    intentClean.putExtras(bundle);
                    NewCleanFinishPlusActivity.Companion.start(this,intentClean,"病毒查杀",false);
                }
                break;

        }
    }

    //广告预加载
  /*  public void adPredLoad() {
        ADUtilsKt.preloadingAd(this, PositionId.AD_LOCK_SCREEN_ADVERTISING, "外部锁屏");
    }*/

    @Override
    protected void onStop() {
        super.onStop();
//        onDestroy();

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
     * 加载广告
     */
    public void adInit() {
        //避免重复加载
        if (SystemClock.elapsedRealtime() - mLastTime < 500) {
            return;
        }
        mLastTime = SystemClock.elapsedRealtime();

        StatisticsUtils.customTrackEvent("ad_request_sdk", "锁屏页广告发起请求", "lock_screen_page", "lock_screen_page");

        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_LOCK_SCREEN, PositionId.KEY_ADVERT_LOCK_SCREEN), new SimpleViewCallBack(lockAdFrameLayout){
            @Override
            public void onAdLoaded(AdInfoModel adInfoModel) {
                if (TextUtils.equals(UnionConstants.AD_SOURCE_FROM_CSJ,adInfoModel.adUnion)){
                    if (lockAdFrameLayout != null){
                        lockAdFrameLayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                super.onAdLoaded(adInfoModel);
            }
        });

    }
}

package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.MainThread;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashHotPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.WeakHandler;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 热启动开屏广告
 */
public class SplashADHotActivity extends BaseActivity<SplashHotPresenter> implements SplashADListener, WeakHandler.IHandler {
    private static final String SKIP_TEXT = "跳过 %d";
    public boolean canJump = false;
    private SplashAD splashAD;
    private ViewGroup container;
    private RoundProgressBar skipView;
    private ImageView mBigLogo, mCleanLogo;
    private boolean needStartDemoList = true;

    /**
     * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
     * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
     * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值（单位ms）
     **/
    private int minSplashTimeWhenNoAD = 2000;
    /**
     * 记录拉取广告的时间
     */
    private long fetchSplashADTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Disposable mSubscription;
    private String mAdvertId = ""; //热启动广告id
    private String mSecondAdvertId = ""; //热启动广告备用id

    //穿山甲相关 begin
    private TTAdNative mTTAdNative;
    //开屏广告加载发生超时但是SDK没有及时回调结果的时候，做的一层保护。
    private final WeakHandler mHandler = new WeakHandler(this);
    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3000;
    private static final int MSG_GO_MAIN = 1;
    //开屏广告是否已经加载
    private boolean mHasLoaded;
    private final String TAG = "ChuanShanJia";
    //穿山甲相关 end

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
    }

    /**
     * 延迟跳转
     */
    public void skip() {
        loadSplashAd();
    }

    public void jumpActivity() {
        finish();
    }

   /* private String getPosId() {
        String posId = getIntent().getStringExtra("pos_id");
        return TextUtils.isEmpty(posId) ? mAdvertId : posId;
    }*/

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么建议动态申请相关权限，再调用广点通SDK
     * <p>
     * SDK不强制校验下列权限（即:无下面权限sdk也可正常工作），但建议开发者申请下面权限，尤其是READ_PHONE_STATE权限
     * <p>
     * READ_PHONE_STATE权限用于允许SDK获取用户标识,
     * 针对单媒体的用户，允许获取权限的，投放定向广告；不允许获取权限的用户，投放通投广告，媒体可以选择是否把用户标识数据提供给优量汇，并承担相应广告填充和eCPM单价下降损失的结果。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 如果需要的权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
            fetchSplashAD(SplashADHotActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADHotActivity.this, 0);
        } else {
            // 否则，建议请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
            fetchSplashAD(SplashADHotActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADHotActivity.this, 0);
        } else {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId         应用ID
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer, String appId, String posId, SplashADListener adListener, int fetchDelay) {
        fetchSplashADTime = System.currentTimeMillis();

        //后台控制是否显示开关
        if (AppHolder.getInstance() == null || AppHolder.getInstance().getSwitchInfoList() == null || AppHolder.getInstance().getSwitchInfoList().getData() == null) {
            jumpActivity();
        } else {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition())) {
                    if (switchInfoList.isOpen()) {
                        splashAD = new SplashAD(activity, skipContainer, appId, posId, adListener, fetchDelay);
                        splashAD.fetchAndShowIn(adContainer);
                        return;
                    } else {
                        jumpActivity();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
    }

    @Override
    public void onADClicked() {
        StatisticsUtils.clickAD("ad_click", "广告点击", "1", mSecondAdvertId, "优量汇", "hot_splash_page", "hot_splash_page", "");
        Log.i("AD_DEMO", "SplashADClicked clickUrl: " + (splashAD.getExt() != null ? splashAD.getExt().get("clickUrl") : ""));
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        if (Math.round(millisUntilFinished / 1000f) > 4) {
            skipView.startAnimation(millisUntilFinished, new LinearInterpolator(), new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
            skipView.setVisibility(View.VISIBLE);
        }
//        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADExposure() {
        Log.i("AD_DEMO", "SplashADExposure");
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "success", "hot_splash_page", "hot_splash_page");
        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mSecondAdvertId, "优量汇", "hot_splash_page", "hot_splash_page", "");
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(AdError error) {
        mBigLogo.setVisibility(View.VISIBLE);
        mCleanLogo.setVisibility(View.VISIBLE);
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "fail", "hot_splash_page", "hot_splash_page");
        Log.i("AD_DEMO", String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(), error.getErrorMsg()));
        /**
         * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
         * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
         * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值
         **/
        long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;//从拉广告开始到onNoAD已经消耗了多少时间
        long shouldDelayMills = alreadyDelayMills > minSplashTimeWhenNoAD ? 0 : minSplashTimeWhenNoAD - alreadyDelayMills;//为防止加载广告失败后立刻跳离开屏可能造成的视觉上类似于"闪退"的情况，根据设置的minSplashTimeWhenNoAD
        // 计算出还需要延时多久
        handler.postDelayed(() -> {
            if (needStartDemoList) {
                jumpActivity();
            }
            finish();
        }, shouldDelayMills);
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            if (needStartDemoList) {
                jumpActivity();
            }
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void initView() {
        mPresenter.getSwitchInfoList();
        mBigLogo = this.findViewById(R.id.iv_biglogo);
        mCleanLogo = this.findViewById(R.id.iv_splash);
        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        boolean needLogo = getIntent().getBooleanExtra("need_logo", true);
        needStartDemoList = getIntent().getBooleanExtra("need_start_demo_list", true);
        if (!needLogo) {
            findViewById(R.id.app_logo).setVisibility(View.GONE);
        }
        initChuanShanJia();
        skipView.setOnClickListener(v -> {
            skipView.clearAnimation();
            JSONObject extension = new JSONObject();
            try {
                extension.put("ad_id", mSecondAdvertId);
                extension.put("ad_agency", "优量汇");
            } catch (Exception e) {
                e.printStackTrace();
            }
            StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "hot_splash_page", "hot_splash_page");
            jumpActivity();
        });


        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("hot_splash_page_custom", "热启动页创建时", "hot_splash_page", "hot_splash_page");
    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null != list && null != list.getData() && list.getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
                if (PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            }
        }
        skip();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        if (null != this.mSubscription) {
            this.mSubscription.dispose();
            this.mSubscription = null;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia() {
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        //定时，AD_TIME_OUT时间到时执行，如果开屏广告没有加载则跳转到主页面
        mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, AD_TIME_OUT);
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 加载穿山甲开屏广告
     */
    private void loadSplashAd() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdvertId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, "穿山甲加载失败=" + message);
                mHasLoaded = true;
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "hot_splash_page", "hot_splash_page");
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestPermission();
                } else {
                    // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                    fetchSplashAD(SplashADHotActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADHotActivity.this, 0);
                }
            }

            @Override
            @MainThread
            public void onTimeout() {
                mHasLoaded = true;
                Log.d(TAG, "穿山甲----开屏广告加载超时");
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "hot_splash_page", "hot_splash_page");
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestPermission();
                } else {
                    // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                    fetchSplashAD(SplashADHotActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADHotActivity.this, 0);
                }
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "穿山甲----开屏广告请求成功");
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "success", "hot_splash_page", "hot_splash_page");
                mHasLoaded = true;
                mHandler.removeCallbacksAndMessages(null);
                if (ad == null) {
                    return;
                }
                showProgressBar();
                //获取SplashView
                View view = ad.getSplashView();
                if (view != null) {
                    container.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                    container.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    ad.setNotAllowSdkCountdown();
                } else {
                    jumpActivity();
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "穿山甲----onAdClicked");
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", "hot_splash_page", "hot_splash_page", "");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "穿山甲----onAdShow");
                        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mAdvertId, "穿山甲", "hot_splash_page", "hot_splash_page", "");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "穿山甲----onAdSkip");
                        JSONObject extension = new JSONObject();
                        try {
                            extension.put("ad_id", mAdvertId);
                            extension.put("ad_agency", "穿山甲");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "hot_splash_page", "hot_splash_page");
                        jumpActivity();
                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "穿山甲----onAdTimeOver");
                        jumpActivity();
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {

                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                Log.d(TAG, "穿山甲下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "穿山甲下载暂停...");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "穿山甲下载失败...");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {

                        }
                    });
                }
            }
        }, AD_TIME_OUT);

    }

    @Override
    public void handleMsg(Message msg) {
        if (msg.what == MSG_GO_MAIN) {
            if (!mHasLoaded) {
                Log.d(TAG, "穿山甲广告已超时，跳到主页面");
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "hot_splash_page", "hot_splash_page");
                jumpActivity();
            }
        }
    }

    private void showProgressBar() {
        skipView.setVisibility(View.VISIBLE);
        skipView.startAnimation(3000, new LinearInterpolator(), new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                jumpActivity();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void netError() {

    }
}

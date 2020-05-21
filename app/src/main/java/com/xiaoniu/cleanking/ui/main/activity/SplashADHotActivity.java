package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.presenter.AdPresenter;
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
public class SplashADHotActivity extends BaseActivity<SplashHotPresenter> {
    public boolean canJump = false;
    private ViewGroup container;
    private RoundProgressBar skipView;
    private ImageView mBigLogo, mCleanLogo;
    private boolean needStartDemoList = true;

    private LinearLayout skipLayout;
    private final int DEFAULT_TIME=5000;
    /**
     * 记录拉取广告的时间
     */
    private long fetchSplashADTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Disposable mSubscription;

    private static final int MSG_GO_MAIN = 1;
    private final String TAG = "ChuanShanJia";
    //穿山甲相关 end

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
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
            loadAd();
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
            loadAd();
        } else {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }


    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            jumpActivity();
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
        mBigLogo = this.findViewById(R.id.iv_biglogo);
        mCleanLogo = this.findViewById(R.id.iv_splash);
        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        skipLayout=findViewById(R.id.skip_layout);

        boolean needLogo = getIntent().getBooleanExtra("need_logo", true);
        needStartDemoList = getIntent().getBooleanExtra("need_start_demo_list", true);
        if (!needLogo) {
            findViewById(R.id.app_logo).setVisibility(View.GONE);
        }
        skipView.setOnClickListener(v -> {
            skipView.clearAnimation();
            jumpActivity();
            StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "cold_splash_page", "cold_splash_page");
        });

        loadAd();
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("hot_splash_page_custom", "热启动页创建时", "hot_splash_page", "hot_splash_page");
    }


    /**
     * 请求广告
     */
    private void loadAd() {
        fetchSplashADTime = System.currentTimeMillis();
        AdRequestParamentersBean adRequestParamentersBean=new AdRequestParamentersBean(
                this,
                container,
                skipLayout,
                PositionId.SPLASH_ID,
                PositionId.COLD_CODE,
                AdType.Splash,
                6000,
                "hot_splash_page",
                "hot_splash_page");
        new AdPresenter().requestAd(adRequestParamentersBean, new AdShowCallBack() {

            @Override
            public void onAdShowCallBack(View view) {
                long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;
                long shouldDelayMills = alreadyDelayMills > DEFAULT_TIME ? 0 : (DEFAULT_TIME-alreadyDelayMills);
                if(skipView!=null){
                    showProgressBar(DEFAULT_TIME);
                }
            }

            @Override
            public void onFailure(String message) {
                jumpActivity();
            }
        });
    }

    /**
     * 跳过进度条
     * @param animTime
     */
    private void showProgressBar(int animTime) {
        skipView.setVisibility(View.VISIBLE);
        if(animTime==0){
            jumpActivity();
            return;
        }
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(1, animTime);
        mValueAnimator.setDuration(animTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(skipView!=null){
                    int progress=((int)valueAnimator.getAnimatedValue()*100)/animTime;
                    skipView.setProgress(progress);
                    if(progress==100){
                        next();
                    }
                }

            }
        });
        mValueAnimator.start();
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
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void netError() {

    }
}

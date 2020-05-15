package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.ConfirmDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MessageDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
import com.xiaoniu.cleanking.utils.WeakHandler;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 这是demo工程的入口Activity，在这里会首次调用广点通的SDK。
 * <p>
 * 在调用SDK之前，如果您的App的targetSDKVersion >= 23，那么建议动态申请相关权限。
 */
public class SplashADActivity extends BaseActivity<SplashPresenter> implements SplashADListener, WeakHandler.IHandler {
    private static final String SKIP_TEXT = "跳过 %d";
    public boolean canJump = false;
    @Inject
    NoClearSPHelper mSPHelper;
    private SplashAD splashAD;
    private ViewGroup container;
    private RoundProgressBar skipView;
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

    private String mAdvertId = ""; //冷启动广告id
    private String mSecondAdvertId = ""; //冷启动广告备用id
    private boolean mIsOpen; //冷启动广告开关
    private static int mStart;

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
    private TextView skipTv;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad;
    }

    //初始sd根目录关联关系
    void initFileRelation() {
        SPUtil.setString(mContext, "path_data", FileUtils.readJSONFromAsset(mContext, "sdstorage.json"));
    }

    /**
     * 延迟跳转
     */
    public void skip() {
        this.mSubscription = Observable.timer(800, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            if (PreferenceUtil.isNoFirstOpenApp()) {
                if (mIsOpen) {
                    loadSplashAd();
                }
            }
        });
    }

    /**
     * 获取过审开关成功
     *
     * @param auditSwitch
     */
    public void getAuditSwitch(AuditSwitch auditSwitch) {
        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, "1");
        } else {
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, auditSwitch.getData());
        }
        if (!PreferenceUtil.isNoFirstOpenApp()) {
            PreferenceUtil.saveFirstOpenApp();
            jumpActivity();
        } else if (auditSwitch.getData().equals("0")) {
            this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                jumpActivity();
            });
        } else if (auditSwitch.getData().equals("1")) {
            mPresenter.getSwitchInfoList();
        }
    }

    /**
     * 获取过审开关失败
     */
    public void getAuditSwitchFail() {
//        this.mSubscription = Observable.timer(800, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpActivity();
            }
        }, 100);

//        });
    }

    public void jumpActivity() {
        final boolean isFirst = SPUtil.getFirstIn(SplashADActivity.this, "isfirst", true);
        if (isFirst) {
            startActivity(new Intent(SplashADActivity.this, NavigationActivity.class));
        } else {
            startActivity(new Intent(SplashADActivity.this, MainActivity.class));
        }
        finish();
    }


    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mSPHelper.isUploadImei()) {
            //有没有传过imei
            String imei = PhoneInfoUtils.getIMEI(mContext);
            if (TextUtils.isEmpty(imei)) {
                if (!mSPHelper.isUploadEmpImei()) { //空imei只上报一次
                    NiuDataAPI.setIMEI("");
                    mSPHelper.setUploadEmpImeiStatus(true);
                }
                mSPHelper.setUploadImeiStatus(false);
            } else {
                NiuDataAPI.setIMEI(imei);
                mSPHelper.setUploadImeiStatus(true);
            }
        }
    }

    /*private String getPosId() {
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
            if (mIsOpen) {
                // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                fetchSplashAD(SplashADActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADActivity.this, 0);
            } else {
                jumpActivity();
            }
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
            if (mIsOpen) {
                // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                fetchSplashAD(SplashADActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADActivity.this, 0);
            }
        } else {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    /**
     * 优量汇开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
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
        if (mIsOpen) {
            splashAD = new SplashAD(activity, skipContainer, appId, posId, adListener, fetchDelay);
            splashAD.fetchAndShowIn(adContainer);
            return;
        } else {
            jumpActivity();
            return;
        }

        /*if (AppHolder.getInstance() == null || AppHolder.getInstance().getSwitchInfoList() == null || AppHolder.getInstance().getSwitchInfoList().getData() == null) {
            jumpActivity();
        } else {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.COLD_CODE.equals(switchInfoList.getAdvertPosition())) {
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
        }*/
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
    }

    @Override
    public void onADClicked() {
        StatisticsUtils.clickAD("ad_click", "广告点击", "1", mSecondAdvertId, "优量汇", "clod_splash_page", "clod_splash_page", "");
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
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "success", "clod_splash_page", "clod_splash_page");
        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mSecondAdvertId, "优量汇", "clod_splash_page", "clod_splash_page", "");
    }

    @Override
    public void onADLoaded(long l) {

    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(AdError error) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "fail", "clod_splash_page", "clod_splash_page");
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

        PreferenceUtil.saveCleanAllUsed(false);
        PreferenceUtil.saveCleanJiaSuUsed(false);
        PreferenceUtil.saveCleanPowerUsed(false);
        PreferenceUtil.saveCleanNotifyUsed(false);
        PreferenceUtil.saveCleanWechatUsed(false);
        PreferenceUtil.saveCleanCoolUsed(false);
        PreferenceUtil.saveCleanGameUsed(false);
        skipTv = findViewById(R.id.tv_skip);
        skipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, 0);
            }
        });
//
        final boolean isFirst = SPUtil.getFirstIn(SplashADActivity.this, "isfirst", true);
        if (isFirst) {
            showConfirmDialog();
        } else {
            boolean isAllopen = false;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                isAllopen = !ExternalInterface.getInstance(this).isOpenNecessaryPermission(this);
            } else {
                isAllopen = !ExternalInterface.getInstance(this).isOpenAllPermission(this);
            }
            if (PermissionUtils.checkPermission(this, permissions) && isAllopen) {
                // 已获取读写文件权限
                //  新用户二次冷启动app，先判断是否授予读取存储文件权限，若已授予，
                //  开屏页显示权限引导页（不展示开屏广告），
                //  右上角显示5s倒计时，5s结束后，右上角显示【跳过】按钮，
                //  点击跳过进入首页。
                // 显示立即修复
                findViewById(R.id.rl_open_new).setVisibility(View.VISIBLE);
                countDown(5);
                // initChuanShanJia();
                findViewById(R.id.btn_repair_now).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 立即修复
                        SPUtil.setRepair(SplashADActivity.this, "isRepair", true);
                    }
                });
            }
        }

        initChuanShanJia();
        if (NetworkUtils.isNetConnected()) {
            mPresenter.getAuditSwitch();
        } else {
            getAuditSwitchFail();
        }

        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        boolean needLogo = getIntent().getBooleanExtra("need_logo", true);
        needStartDemoList = getIntent().getBooleanExtra("need_start_demo_list", true);
        if (!needLogo) {
            findViewById(R.id.app_logo).setVisibility(View.GONE);
        }

        initNiuData();
        initFileRelation();
        skipView.setOnClickListener(v -> {
            skipView.clearAnimation();
            JSONObject extension = new JSONObject();
            try {
                extension.put("ad_id", mSecondAdvertId);
                extension.put("ad_agency", "优量汇");
            } catch (Exception e) {
                e.printStackTrace();
            }
            StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "clod_splash_page", "clod_splash_page", extension);
            jumpActivity();
        });
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("clod_splash_page_custom", "冷启动创建时", "clod_splash_page", "clod_splash_page");
    }

    private void showConfirmDialog() {

        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", "温馨提示");
        bundle.putString("content", "欢迎使用悟空清理！我们依据最新的法律要求，更新了隐私政策，" +
                "特此向您说明。作为互联网安全公司，我们在为用户提供隐私保护的同时，" +
                "对自身的安全产品提出了更高级别的标准。在使用悟空清理前，请务必仔细阅读并了解");
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getFragmentManager(), "");
        confirmDialogFragment.setOnClickListener(new ConfirmDialogFragment.OnClickListener() {
            @Override
            public void onConfirm() {
                initChuanShanJia();
                if (NetworkUtils.isNetConnected()) {
                    mPresenter.getAuditSwitch();
                } else {
                    getAuditSwitchFail();
                }
            }

            @Override
            public void onCancel() {
                confirmDialogFragment.dismiss();
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.show(getFragmentManager(), "");
                messageDialogFragment.setOnClickListener(new MessageDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        confirmDialogFragment.show(getFragmentManager(), "");
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }


    public void countDown(int start) {
        final Handler mHandler = new Handler();
        mStart = start;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //do something
                //每隔1s循环执行run方法
                mStart--;

                if (0 == mStart) {
                    // 可以跳过
                    skipTv.setText("跳过");
                    skipTv.setClickable(true);
                    return;
                }
                skipTv.setText(String.format(SKIP_TEXT, mStart));
                mHandler.postDelayed(this, 1000);
            }
        };
        //主线程中调用：
        mHandler.postDelayed(r, 1000);//延时100毫秒
    }


    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null != list && null != list.getData() && list.getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
                if (PositionId.COLD_CODE.equals(switchInfoList.getAdvertPosition()) && PositionId.SPLASH_ID.equals(switchInfoList.getConfigKey())) {
                    mIsOpen = switchInfoList.isOpen();
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            }
        }
        skip();
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {
        this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, "1");
            PreferenceUtil.saveFirstOpenApp();
            jumpActivity();
        });
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

    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia() {
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
//        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //定时，AD_TIME_OUT时间到时执行，如果开屏广告没有加载则跳转到主页面
        mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, AD_TIME_OUT);
    }

    /**
     * 加载穿山甲开屏广告
     */
    private void loadSplashAd() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "success", "clod_splash_page", "clod_splash_page");
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
                mHasLoaded = true;
                Log.d(TAG, "穿山甲加载失败=" + message);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "clod_splash_page", "clod_splash_page");
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestPermission();
                } else {
                    // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                    fetchSplashAD(SplashADActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADActivity.this, 0);
                }
            }

            @Override
            @MainThread
            public void onTimeout() {
                mHasLoaded = true;
                Log.d(TAG, "穿山甲----开屏广告加载超时");
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "clod_splash_page", "clod_splash_page");
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestPermission();
                } else {
                    // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
                    fetchSplashAD(SplashADActivity.this, container, skipView, PositionId.APPID, mSecondAdvertId, SplashADActivity.this, 0);
                }
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                mHasLoaded = true;
                Log.d(TAG, "穿山甲----开屏广告请求成功");
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
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", "clod_splash_page", "clod_splash_page", "");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "穿山甲----onAdShow");
                        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mAdvertId, "穿山甲", "clod_splash_page", "clod_splash_page", "");
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
                        StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "clod_splash_page", "clod_splash_page", extension);
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
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", "clod_splash_page", "clod_splash_page");
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
}

package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.presenter.AdPresenter;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.ConfirmDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MessageDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
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
public class SplashADActivity extends BaseActivity<SplashPresenter> {
    private static final String SKIP_TEXT = "跳过 %d";
    public boolean canJump = false;
    @Inject
    NoClearSPHelper mSPHelper;
    private ViewGroup container;
    private RoundProgressBar skipView;
    private LinearLayout skipLayout;
    private boolean needStartDemoList = true;
    private final int DEFAULT_TIME = 5000;
    private final int SECONDARY_STARTUP = 2;
    private ConfirmDialogFragment confirmDialogFragment;

    /**
     * 记录拉取广告的时间
     */
    private long fetchSplashADTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Disposable mSubscription;

    private String mSecondAdvertId = ""; //冷启动广告备用id
    private static int mStart;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int MSG_GO_MAIN = 1;
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
                jumpActivity();
            }
        });

        final boolean isFirst = SPUtil.getFirstIn(SplashADActivity.this, "isfirst", true);
        boolean consentAgreement = SPUtil.getBoolean(this, "consentAgreement", false);
        if (!consentAgreement) {
            showConfirmDialog();
        }
        int startsNumber = SPUtil.getStartsNumber(SplashADActivity.this, "startsNumber", 1);
        if (startsNumber == SECONDARY_STARTUP) {   // 第二次冷启动
            boolean isAllopen = false;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                isAllopen = !ExternalInterface.getInstance(this).isOpenNecessaryPermission(this);
            } else {
                isAllopen = !ExternalInterface.getInstance(this).isOpenAllPermission(this);
            }
            if (PermissionUtils.checkPermission(this, permissions) && isAllopen) {
                //  已获取读写文件权限
                //  新用户二次冷启动app，先判断是否授予读取存储文件权限，若已授予，
                //  开屏页显示权限引导页（不展示开屏广告），
                //  右上角显示5s倒计时，5s结束后，右上角显示【跳过】按钮，
                //  点击跳过进入首页。
                // 显示立即修复
                findViewById(R.id.rl_open_new).setVisibility(View.VISIBLE);
                startCountDown(5);
                findViewById(R.id.btn_repair_now).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 立即修复
                        SPUtil.setRepair(SplashADActivity.this, "isRepair", true);
                    }
                });
            }
        }
        if (startsNumber != SECONDARY_STARTUP || !PermissionUtils.checkPermission(this, permissions)) {
            if (NetworkUtils.isNetConnected()) {
                mPresenter.getAuditSwitch();
            } else {
                getAuditSwitchFail();
            }
        }

        // initChuanShanJia();
        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        skipLayout = findViewById(R.id.skip_layout);

        boolean needLogo = getIntent().getBooleanExtra("need_logo", true);
        needStartDemoList = getIntent().getBooleanExtra("need_start_demo_list", true);
        if (!needLogo) {
            findViewById(R.id.app_logo).setVisibility(View.GONE);
        }
        initNiuData();
        initFileRelation();
        skipView.setOnClickListener(v ->
        {
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
        // mPresenter.getAuditSwitch();
        if (startsNumber < 2 * SECONDARY_STARTUP) {
            SPUtil.setStartsNumber(SplashADActivity.this, "startsNumber", ++startsNumber);
        }
    }

    /**
     * 获取过审开关成功
     *
     * @param auditSwitch
     */
    public void getAuditSwitch(AuditSwitch auditSwitch) {
        Log.d("===============", " getAuditSwitch");

        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, "1");
        } else {
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, auditSwitch.getData());
        }
        if (!PreferenceUtil.isNoFirstOpenApp()) {
            // PreferenceUtil.saveFirstOpenApp();
            // jumpActivity();
        } else if (auditSwitch.getData().equals("0")) {
            this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                jumpActivity();
            });
        } else if (auditSwitch.getData().equals("1")) {
            loadAd();
        }
    }


    private void showConfirmDialog() {


        String s1 = "欢迎使用悟空清理！我们依据最新的法律要求，更新了隐私政策，";
        s1 += "特此向您说明。作为互联网安全公司，";
        s1 += "我们在为用户提供隐私保护的同时，对自身的安全产品提出了更高级别的标准。";
        s1 += "在使用悟空清理前，请务必仔细阅读并了解";

        s1 += "<font color='#06C581'>设置红色</font><br>";
        s1 += "<a href=\"https://www.baidu.com\" color='#06C581'><font color='#06C581'>百度链接</font></a>";


        String html = "欢迎使用悟空清理！我们依据最新的法律要求，更新了隐私政策，" +
                "特此向您说明。作为互联网安全公司，" +
                "我们在为用户提供隐私保护的同时，对自身的安全产品提出了更高级别的标准。" +
                "在使用悟空清理前，请务必仔细阅读并了解<font color='#06C581'><a href=\"https://www.baidu.com\">《隐私政策》</a></font>和" +
                "<font color='#06C581'><a href=\"https://www.baidu.com\">《用户协议》</a></font>" +
                "全部条款，如您同意并接收全部条款，请点击同意开始使用我们的产品和服务。";


        confirmDialogFragment = ConfirmDialogFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.reminder));
//        bundle.putString("content", getString(R.string.html));
        bundle.putString("content", html);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getFragmentManager(), "");

        confirmDialogFragment.setOnClickListener(new ConfirmDialogFragment.OnClickListener() {
            @Override
            public void onConfirm() {
                SPUtil.setBoolean(SplashADActivity.this, "consentAgreement", true);
                PreferenceUtil.saveFirstOpenApp();
                jumpActivity();
            }

            @Override
            public void onCancel() {
                confirmDialogFragment.dismiss();
                showMessageDialog();
            }
        });
    }

    private void showMessageDialog() {
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

    /**
     * 倒计时
     *
     * @param start
     */
    public void startCountDown(int start) {
        mStart = start;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //每隔1s循环执行run方法
                mStart--;
                if (0 == mStart) {
                    // 跳过
                    skipTv.setText(getString(R.string.skip));
                    skipTv.setClickable(true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            jumpActivity();
                        }
                    }, 2000); // 延时1秒
                    return;
                }
                skipTv.setText(mStart + "");
                handler.postDelayed(this, 1000);
            }
        };
        //主线程中调用：
        handler.postDelayed(r, 1000); // 延时1秒
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
     * 请求广告
     */
    private void loadAd() {
        fetchSplashADTime = System.currentTimeMillis();
        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(
                this,
                container,
                skipLayout,
                PositionId.SPLASH_ID,
                PositionId.COLD_CODE,
                AdType.Splash,
                3000,
                "clod_splash_page",
                "clod_splash_page");
        new AdPresenter().requestAd(adRequestParamentersBean, new AdShowCallBack() {

            @Override
            public void onAdShowCallBack(View view) {
                long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;
                long shouldDelayMills = alreadyDelayMills > DEFAULT_TIME ? 0 : (DEFAULT_TIME - alreadyDelayMills);
                if (skipView != null) {
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
     *
     * @param animTime
     */
    private void showProgressBar(int animTime) {
        skipView.setVisibility(View.VISIBLE);
        if (animTime == 0) {
            jumpActivity();
            return;
        }
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(1, animTime);
        mValueAnimator.setDuration(animTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (skipView != null) {
                    int progress = ((int) valueAnimator.getAnimatedValue() * 100) / animTime;
                    skipView.setProgress(progress);
                    if (progress == 100) {
                        next();
                    }
                }

            }
        });
        mValueAnimator.start();
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
}

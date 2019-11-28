package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 这是demo工程的入口Activity，在这里会首次调用广点通的SDK。
 * <p>
 * 在调用SDK之前，如果您的App的targetSDKVersion >= 23，那么建议动态申请相关权限。
 */
public class SplashADActivity extends BaseActivity<SplashPresenter> implements View.OnClickListener {

    @BindView(R.id.v_start)
    View mStartView;
    @BindView(R.id.v_content)
    View mContentView;
    @BindView(R.id.tv_delete)
    TextView mBtn;
    @BindView(R.id.tv_qx)
    TextView mAgreement;

    @Inject
    NoClearSPHelper mSPHelper;
    private ViewGroup container;
    private RoundProgressBar skipView;
    private Disposable mSubscription;
    private AdManager mAdManager;
    private boolean mIsOpen; //冷启动广告开关
    private String mAdTitle = " "; //广告标题
    private String mAdSourse = " "; //广告来源

    private final String TAG = "GeekSdk";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad;
    }

    //初始sd根目录关联关系
    void initFileRelation() {
        SPUtil.setString(mContext, "path_data", FileUtils.readJSONFromAsset(mContext, "sdstorage.json"));
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
        if (!PreferenceUtil.isNotFirstOpenApp()) {
            mStartView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            mPresenter.getSwitchInfoList();
        } else if (auditSwitch.getData().equals("0")) {
            mStartView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
            this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                jumpActivity();
            });
        } else if (auditSwitch.getData().equals("1")) {
            mStartView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
            mPresenter.getSwitchInfoList();
        }
    }

    /**
     * 获取过审开关失败
     */
    public void getAuditSwitchFail() {
        this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpActivity();
                }
            }, 100);
        });
    }

    public void jumpActivity() {
        startActivity(new Intent(SplashADActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mSPHelper.isUploadImei()) {
            //有没有传过imei
            String imei = DeviceUtils.getIMEI();
            if (TextUtils.isEmpty(imei)) {
                NiuDataAPI.setIMEI("");
                mSPHelper.setUploadImeiStatus(false);
            } else {
                NiuDataAPI.setIMEI(imei);
                mSPHelper.setUploadImeiStatus(true);
            }
        }
    }

    @Override
    protected void initView() {
        /*        StatusBarUtil.setStatusBarState(this,mStartView,false,-1);*/
        mBtn.setOnClickListener(this);
        mAgreement.setOnClickListener(this);
        findViewById(R.id.tv_xy).setOnClickListener(this);
        PreferenceUtil.saveCleanAllUsed(false);
        PreferenceUtil.saveCleanJiaSuUsed(false);
        PreferenceUtil.saveCleanPowerUsed(false);
        PreferenceUtil.saveCleanNotifyUsed(false);
        PreferenceUtil.saveCleanWechatUsed(false);
        PreferenceUtil.saveCleanCoolUsed(false);
        PreferenceUtil.saveCleanGameUsed(false);
        PreferenceUtil.saveRedPacketShowCount(PreferenceUtil.getRedPacketShowCount() + 1);

        if (!NetworkUtils.isNetConnected()) {
            if (!PreferenceUtil.isNotFirstOpenApp()) {
                mStartView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(View.GONE);
            } else {
                getAuditSwitchFail();
            }
        } else {
            mPresenter.geekAdSDKConfig();//加载广告配置
            mPresenter.getAuditSwitch();
        }
        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);

        initNiuData();
        initFileRelation();
        skipView.setOnClickListener(v -> {
            PreferenceUtil.saveShowAD(false);
            skipView.clearAnimation();
            JSONObject extension = new JSONObject();
            try {
                extension.put("ad_id", mAdTitle);
                extension.put("ad_agency", mAdSourse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "clod_splash_page", "clod_splash_page", extension);
        });
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("clod_splash_page_custom", "冷启动创建时", "clod_splash_page", "clod_splash_page");
        if (PreferenceUtil.getInstants().getInt(Constant.CLEAN_DB_SAVE) != 1) {
            readyExternalDb();
        }

    }


    /**
     * 拷贝数据库表
     */
    public void readyExternalDb() {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                FileUtils.copyDbFile(ContextUtils.getApplication(), Constant.CLEAN_DB_NAME);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);


            }
        }.execute();
    }


    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (!PreferenceUtil.isNotFirstOpenApp()) return;
        if (null != list && null != list.getData() && list.getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
                if (PositionId.COLD_CODE.equals(switchInfoList.getAdvertPosition()) && PositionId.SPLASH_ID.equals(switchInfoList.getConfigKey())) {
                    mIsOpen = switchInfoList.isOpen();
                }
            }
        }
        if (PreferenceUtil.isNotFirstOpenApp() && mIsOpen) {
            initGeekSdkAD();
        } else {
            jumpActivity();
        }
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
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        if (null != this.mSubscription) {
            this.mSubscription.dispose();
            this.mSubscription = null;
        }
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

    private void initGeekSdkAD() {
        mAdManager = GeekAdSdk.getAdsManger();
        mAdManager.loadSplashAd(this, "cold_kp", new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
                if (null != info) {
                    mAdTitle = info.getAdTitle();
                    mAdSourse = info.getAdSource();
                }
                if (null == info) return;
                showProgressBar();
                container.addView(mAdManager.getAdView());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "clod_splash_page", "clod_splash_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "clod_splash_page", "clod_splash_page", "");
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "clod_splash_page", "clod_splash_page", "");
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                Log.e(TAG, "-----adError-----" + errorMsg);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "fail", "clod_splash_page", "clod_splash_page");
                jumpActivity();
            }
        });
    }

    /**
     * 自定义倒计时进度条
     */
    private void showProgressBar() {
        skipView.setVisibility(View.VISIBLE);
        skipView.startAnimation(3000, new LinearInterpolator(), new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                PreferenceUtil.saveShowAD(false);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                PreferenceUtil.saveFirstOpenApp();
                startActivity(MainActivity.class);
                finish();
                break;
            case R.id.tv_qx:
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/agree.html");
                } else {
                    jumpXieyiActivity(AppConstants.Base_H5_Host + "/agree.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "隐私政策", "mine_page", "about_page");
                break;
            case R.id.tv_xy:
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/userAgreement.html");
                } else {
                    jumpXieyiActivity(AppConstants.Base_H5_Host + "/userAgreement.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "用户协议", "mine_page", "about_page");
                break;
        }
    }

    public void jumpXieyiActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "服务协议");
        bundle.putBoolean(Constant.NoTitle, false);
        startActivity(UserLoadH5Activity.class, bundle);
    }
}

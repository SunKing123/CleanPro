package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.rxjava.RxTimer;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 冷启动开屏页面
 * <p>
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
    @BindView(R.id.error_ad_iv)
    ImageView mErrorAdIv;
    @Inject
    NoClearSPHelper mSPHelper;
    private ViewGroup container;
    private Disposable mSubscription;
    private boolean mIsOpen; //冷启动广告开关
    private String pushData = null;
    private boolean mCanJump;
    RxTimer rxTimer;
    private static final int SP_SHOW_OUT_TIME = 9 * 1000;//开屏总超时时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        long home = MmkvUtil.getLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, 0L);
        if (home == 0L) {
            MmkvUtil.saveLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, System.currentTimeMillis());
        }
        getDataFromPush();
        //用户/token校验
        RequestUserInfoUtil.checkUserToken(this);
    }

    private void getDataFromPush() {
        //获取华为平台附带的jpush信息
        if (getIntent().getData() != null) {
            pushData = getIntent().getData().toString();
        }
        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(pushData) && getIntent().getExtras() != null) {
            pushData = getIntent().getExtras().getString("JMessageExtra");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);
        container = this.findViewById(R.id.splash_container);
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
        if (PreferenceUtil.getScreenInsideTime()) {
            PreferenceUtil.saveRedPacketShowCount(1);
            PreferenceUtil.saveScreenInsideTime();
        } else {
            PreferenceUtil.saveRedPacketShowCount(PreferenceUtil.getRedPacketShowCount() + 1);
        }
        /*保存冷、热启动的次数*/
        InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
        if (DateUtils.isSameDay(inside.getTime(), System.currentTimeMillis())) {
            inside.setCount(inside.getCount() + 1);
        } else {
            inside.setCount(1);
        }
        inside.setTime(System.currentTimeMillis());
        PreferenceUtil.saveColdAndHotStartCount(inside);
        if (NetworkUtils.isNetConnected()) {//正常网络
            if (PreferenceUtil.getCoolStartTime()) {
                mPresenter.getAuditSwitch();
            } else { //小于10分钟不获取开关直接请求广告
                //        状态（0=隐藏，1=显示）
                String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "1");
                if (auditSwitch.equals("0")) {
                    mStartView.setVisibility(View.GONE);
                    mContentView.setVisibility(View.VISIBLE);
                    this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                        jumpActivity();
                    });
                } else if (auditSwitch.equals("1")) {
                    mStartView.setVisibility(View.GONE);
                    mContentView.setVisibility(View.VISIBLE);
                    if (PreferenceUtil.isNotFirstOpenApp()) {
                        if (PreferenceUtil.getCoolStartADStatus()) {
                            initGeekSdkAD();
                        } else {
                            jumpActivity();
                        }
                        String switchInfo = PreferenceUtil.getInstants().get(Constant.SWITCH_INFO);
                        if (!TextUtils.isEmpty(switchInfo)) {
                            SwitchInfoList switchInfoList = new Gson().fromJson(switchInfo, SwitchInfoList.class);
                            if (null != switchInfoList) {
                                AppHolder.getInstance().setSwitchInfoList(switchInfoList);
                            } else {
                                mPresenter.getSwitchInfoListNew();
                            }
                        } else {
                            mPresenter.getSwitchInfoListNew();
                        }

                    } else {
                        jumpActivity();
                    }
                }
            }
            PreferenceUtil.saveCoolStartTime();
        } else {//无网络状态
            if (!PreferenceUtil.isNotFirstOpenApp()) {//第一次冷启动
                mStartView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(View.GONE);
                //默认过审状态
                SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
                MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
            } else {
                getAuditSwitchFail();
            }
        }

        initNiuData();
        initFileRelation();

        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("clod_splash_page_custom", "冷启动创建时", "clod_splash_page", "clod_splash_page");
        if (PreferenceUtil.getInstants().getInt(Constant.CLEAN_DB_SAVE) != 1) {
            readyWeatherExternalDb();
        }
        //超时定时器
        rxTimer = new RxTimer();
        rxTimer.timer(SP_SHOW_OUT_TIME, number -> {
            if(PreferenceUtil.isNotFirstOpenApp()){//非第一次冷启动总超时逻辑；
                mCanJump = true;
                jumpActivity();
            }
        });

    }


    //初始sd根目录关联关系
    void initFileRelation() {
        SPUtil.setString(mContext, "path_data", FileUtils.readJSONFromAsset(mContext, "sdstorage.json"));
    }

    /**
     * 获取过审开关成功//
     *
     * @param auditSwitch
     */
    public void getAuditSwitch(AuditSwitch auditSwitch) {
        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）；
            //0715戚雯确认，接口异常默认审核状态；
            SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
            MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
        } else {
            SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, auditSwitch.getData());
            MmkvUtil.saveString(SpCacheConfig.AuditSwitch, auditSwitch.getData());
        }
        if (!PreferenceUtil.isNotFirstOpenApp()) { //第一次冷启动
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
        //0715戚雯确认，接口异常默认审核状态；
        SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
        MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
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
        if (mCanJump) {
            Intent intent = new Intent(SplashADActivity.this, MainActivity.class);
            if (!TextUtils.isEmpty(pushData)) {
                intent.putExtra("push_uri", pushData);
            }
            startActivity(intent);
            finish();
            mCanJump = false;
        } else {
            mCanJump = true;
        }

    }

    /**
     * 拷贝数据库表
     */
    public void readyWeatherExternalDb() {
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
                FileUtils.copyDbFile(ContextUtils.getApplication(), Constant.WEATHER_DB_NAME);
                PreferenceUtil.getInstants().saveInt(Constant.CLEAN_DB_SAVE, 1);
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
        if (null != list && null != list.getData() && list.getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
                if (PositionId.COLD_CODE.equals(switchInfoList.getAdvertPosition()) && PositionId.SPLASH_ID.equals(switchInfoList.getConfigKey())) {
                    mIsOpen = switchInfoList.isOpen();
                    PreferenceUtil.saveCoolStartADStatus(mIsOpen);
                }
            }
        }
        if (!PreferenceUtil.isNotFirstOpenApp()) return;
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
        if (null != rxTimer) {
            rxTimer.cancel();
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
        StatisticsUtils.customTrackEvent("ad_request_sdk", "冷启动页广告发起请求", "clod_page", "clod_page");

        AdRequestParams params = new AdRequestParams.Builder().setAdId(MidasConstants.SP_CODE_START_ID)
                .setActivity(this).setViewContainer(container).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdLoadSuccess(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdLoadSuccess(adInfo);
            }

            @Override
            public void onShowError(int i, String s) {
                jumpActivity();
            }

            @Override
            public void onAdShow(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdShow(adInfo);
            }

//            @Override
//            public void onAdClicked(com.xnad.sdk.ad.entity.AdInfo adInfo) {
//                jumpActivity();
//            }

            @Override
            public void onAdClose(AdInfo adInfo) {
                jumpActivity();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                jumpActivity();
                break;
        }
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
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/agree.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "隐私政策", "mine_page", "about_page");
                break;
            case R.id.tv_xy:
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/userAgreement.html");
                } else {
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/userAgreement.html");
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

    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mSPHelper.isUploadImei()) {
            //有没有传过imei
            String imei = PhoneInfoUtils.getIMEI(mContext);
            LogUtils.i("--zzh--" + imei);
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
    protected void onResume() {
        super.onResume();
        if (mCanJump) {
            jumpActivity();
        }
        mCanJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCanJump = false;
    }


}

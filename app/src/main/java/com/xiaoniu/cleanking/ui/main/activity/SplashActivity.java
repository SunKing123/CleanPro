package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity<SplashPresenter> {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @Inject
    NoClearSPHelper mSPHelper;
    private Disposable mSubscription;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void initView() {

        initNiuData();

        skip();
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

    /**
     * 延迟跳转
     */
    public void skip() {
        this.mSubscription = Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> mPresenter.getAuditSwitch());
    }

    public void jumpActivity() {
        final boolean isFirst = SPUtil.getFirstIn(SplashActivity.this, "isfirst", true);
        if (isFirst) {
            startActivity(new Intent(SplashActivity.this, NavigationActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    public void getAuditSwitch(AuditSwitch auditSwitch) {
        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）
            SPUtil.setString(SplashActivity.this, AppApplication.AuditSwitch, "1");
        }else{
            SPUtil.setString(SplashActivity.this, AppApplication.AuditSwitch, auditSwitch.getData());
        }
        jumpActivity();
    }

    @Override
    public void netError() {

    }

    @Override
    protected void onDestroy() {
        if (null != this.mSubscription) {
            this.mSubscription.dispose();
            this.mSubscription = null;
        }
        super.onDestroy();
    }
}

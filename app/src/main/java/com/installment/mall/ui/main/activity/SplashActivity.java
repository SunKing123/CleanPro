package com.installment.mall.ui.main.activity;

import android.widget.ImageView;

import com.installment.mall.R;
import com.installment.mall.app.RouteConstants;
import com.installment.mall.app.injector.component.ActivityComponent;
import com.installment.mall.base.BaseActivity;
import com.installment.mall.ui.main.presenter.SplashPresenter;
import com.installment.mall.utils.DeviceUtils;
import com.installment.mall.utils.prefs.NoClearSPHelper;

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
        if (DeviceUtils.getScreenHeight() > 1920) {
            this.ivSplash.setImageResource(R.mipmap.splash_1620x3360);
        }
        skip();
    }

    /**
     * 延迟跳转
     */
    public void skip() {
        this.mSubscription = Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> this.jumpActivity());
    }

    public void jumpActivity() {
//        if (TextUtils.isEmpty(AndroidUtil.getToken())) {
//            startActivity(RouteConstants.CHECK_LOGIN_ACTIVITY, true);
//        } else {
            startActivity(RouteConstants.MAIN_ACTIVITY, true);
//        }
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

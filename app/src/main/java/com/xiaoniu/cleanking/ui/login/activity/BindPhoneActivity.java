package com.xiaoniu.cleanking.ui.login.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;
import com.xiaoniu.cleanking.ui.login.di.component.DaggerBindPhoneComponent;
import com.xiaoniu.cleanking.ui.login.presenter.BindPhonePresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.user.ShanYanManager;
import com.xiaoniu.cleanking.widget.CommonTitleLayout;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.xiaoniu.cleanking.utils.user.UserHelper.BIND_PHONE_SUCCESS;


/**
 * ================================================
 * Description: 绑定手机号==一键登录
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:03
 * ================================================
 */
public class BindPhoneActivity extends BaseActivity<BindPhonePresenter> implements BindPhoneContract.View {
    @BindView(R.id.titleLayout)
    CommonTitleLayout titleLayout;
    @BindView(R.id.iv_bind)
    TextView ivBind;
    @BindView(R.id.rl_bind)
    RelativeLayout rlBind;
    @BindView(R.id.tv_manual)
    TextView tvManual;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerBindPhoneComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_bind_phone; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
        titleLayout.setMiddleTitle("绑定手机号").setLeftBackColor(R.color.color_666666).isShowBottomLine(true);
        requestPhonePermission();
        //设置授权的样式
        OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanYanManager.getCJSConfig(this));

        StatisticsUtils.customTrackEvent("binding_mobile_phone_number_page_custom", "绑定手机号页面曝光", "binding_mobile_phone_number_page", "binding_mobile_phone_number_page");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String eventMsg) {
        if (BIND_PHONE_SUCCESS.equals(eventMsg)) {//绑定手机号成功关闭页面
            finish();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void killMyself() {
        finish();
    }

    @OnClick({R.id.rl_bind, R.id.tv_manual})
    public void onViewClicked(View view) {
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.rl_bind:
                StatisticsUtils.trackClick("one_click_secure_binding_click", "一键登录点击", "binding_mobile_phone_number_page", "binding_mobile_phone_number_page");
                oneBindingOption();
                break;
            case R.id.tv_manual:
                StatisticsUtils.trackClick("captcha_binding_click", "验证码绑定点击", "binding_mobile_phone_number_page", "binding_mobile_phone_number_page");
                goToShouDongBinding();
                break;
        }
    }

    private void oneBindingOption() {
        ShanYanManager.getPhoneInfo((type, code, message) -> {
            if (code == 1022) {
                openLoginAuth();
            } else {
                goToShouDongBinding();
            }
        });
    }

    private void openLoginAuth() {
        ShanYanManager.openLoginAuth((typeL, codeL, messageL) -> {
            if (typeL == 102) {//拉取页面结果
                if (codeL != 1000) {
                    goToShouDongBinding();
                }
            } else if (typeL == 103) {//登录结果
                if (codeL == 1000) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(messageL);
                        String token = jsonObject.optString("token");
                        if (!TextUtils.isEmpty(token)) {
                            mPresenter.getPhoneNumFromShanYan(token);
                        } else {
                            startActivity(new Intent(BindPhoneActivity.this, BindPhoneManualActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 检验权限
     */
    private void requestPhonePermission() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        new RxPermissions(this).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

            }
        });
    }

    private void goToShouDongBinding() {
        startActivity(new Intent(this, BindPhoneManualActivity.class));
    }

    /**
     * 通过闪验绑定手机号成功
     */
    @Override
    public void bindPhoneSuccess() {
        finish();
    }
}

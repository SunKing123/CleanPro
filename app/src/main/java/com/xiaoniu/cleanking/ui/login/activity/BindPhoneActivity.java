package com.xiaoniu.cleanking.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;
import com.xiaoniu.cleanking.ui.login.di.component.DaggerBindPhoneComponent;
import com.xiaoniu.cleanking.ui.login.presenter.BindPhonePresenter;
import com.xiaoniu.cleanking.widget.CommonTitleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


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
    ImageView ivBind;
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
        titleLayout.setLeftTitle("登录2").setBgColor(R.color.common_white);
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
    public void killMyself() {
        finish();
    }

    @OnClick({R.id.rl_bind, R.id.tv_manual})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_bind:
                break;
            case R.id.tv_manual:
                startActivity(new Intent(this, BindPhoneManualActivity.class));
                break;
        }
    }
}

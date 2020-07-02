package com.xiaoniu.cleanking.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;
import com.xiaoniu.cleanking.ui.login.di.component.DaggerLoginWeiChatComponent;
import com.xiaoniu.cleanking.ui.login.presenter.LoginWeiChatPresenter;
import com.xiaoniu.cleanking.widget.CommonTitleLayout;
import com.xiaoniu.payshare.AuthorizeLoginListener;
import com.xiaoniu.payshare.AuthorizedLogin;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:微信登录
 * * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
public class LoginWeiChatActivity extends BaseActivity<LoginWeiChatPresenter> implements LoginWeiChatContract.View {

    @BindView(R.id.titleLayout)
    CommonTitleLayout titleLayout;
    @BindView(R.id.vx_login_ll)
    LinearLayout vxLoginLl;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerLoginWeiChatComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_login_wei_chat; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleLayout.setLeftTitle("登录").setBgColor(R.color.common_white);
        initListener();
    }

    private void initListener() {
        AuthorizedLogin.getInstance().setAuthorizedLoginListener(new AuthorizeLoginListener() {
            @Override
            public void authorizeCancel() {

            }

            @Override
            public void authorizeSuccess(SHARE_MEDIA platform, Map<String, String> data) {

            }
        });
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

    @OnClick({R.id.img_back, R.id.vx_login_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                killMyself();
                break;
            case R.id.vx_login_ll:
                startActivity(new Intent(this, BindPhoneActivity.class));
//                AuthorizedLogin.getInstance().userWeiChatLogin(this);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

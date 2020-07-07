package com.xiaoniu.cleanking.ui.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.login.bean.UserInfoBean;
import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;
import com.xiaoniu.cleanking.ui.login.di.component.DaggerLoginWeiChatComponent;
import com.xiaoniu.cleanking.ui.login.presenter.LoginWeiChatPresenter;
import com.xiaoniu.cleanking.ui.newclean.dialog.CommonDialogUtils;
import com.xiaoniu.cleanking.ui.newclean.interfice.OnBtnClickListener;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.CommonTitleLayout;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.payshare.AuthorizeLoginListener;
import com.xiaoniu.payshare.AuthorizedLogin;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
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
    @BindView(R.id.bottom_btn)
    CheckBox bottomBtn;

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

    HashMap<String, Object> paramsMap;

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
        titleLayout.setLeftTitle("").setLeftBackColor(R.color.color_666666).isShowBottomLine(true);
        initListener();
        setXieYi();
        paramsMap = new HashMap<>();
    }

    private void setXieYi() {
        bottomBtn.setChecked(true);
        String string = "已同意《服务协议》和《隐私条款》";
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                SchemeProxy.openScheme(widget.getContext(), "需要协议地址");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#5CD0FF"));
            }
        }, string.length() - 6, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                SchemeProxy.openScheme(widget.getContext(), "需要协议地址");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#5CD0FF"));
            }
        }, 3, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        bottomBtn.setText(spannableString);
        bottomBtn.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initListener() {
        AuthorizedLogin.getInstance().setAuthorizedLoginListener(new AuthorizeLoginListener() {
            @Override
            public void authorizeCancel() {

            }

            @Override
            public void authorizeSuccess(SHARE_MEDIA platform, Map<String, String> data) {
//                paramsMap.put("openId",  AndroidUtil.getDeviceID());
                paramsMap.put("openId", data.get("openid"));
                paramsMap.put("nickname", data.get("name"));
                paramsMap.put("userAvatar", data.get("iconurl"));
                if (UserHelper.init().isLogin() && !UserHelper.init().isWxLogin()) {//微信登陆什么也不处理 步数不要清理
                    paramsMap.remove("userType");
                    mPresenter.bindingWeiChat(paramsMap);
                } else {//啥也没登陆就去 登陆微信
                    mPresenter.loginWithWeiChat(paramsMap);
                }
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
//                startActivity(new Intent(this, BindPhoneActivity.class));
                AuthorizedLogin.getInstance().userWeiChatLogin(this);
                break;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void dealLoginResult(int flag, LoginDataBean loginDataBean) {
        //flag 1 微信登录  2 绑定微信
        if (flag == 2) {
            if ("2028".equals(loginDataBean.getCode())) {
                String tip = "账号已注册，登录后游客模式账号金币不同步,是否继续登录";
                CommonDialogUtils.showRemindDialogStyle01(this, tip, "确认", new OnBtnClickListener() {
                    @Override
                    public void onClickView(int type) {
                        mPresenter.loginWithWeiChat(paramsMap);
                    }
                });
            } else if ("200".equals(loginDataBean.getCode())) {
                mPresenter.loginWithWeiChat(paramsMap);
            }
        }

        UserInfoBean infoBean = loginDataBean.getData();
        if (infoBean != null) {
            UserHelper.init().saveUserInfo(infoBean);
            if (flag == 1) {
                if (TextUtils.isEmpty(infoBean.phone)) {
                    goToBindPhone();

                }
                finish();
            }
        }
    }

    /**
     * 跳转绑定手机号页面
     */
    private void goToBindPhone() {
        startActivity(new Intent(this, BindPhoneActivity.class));
    }
}

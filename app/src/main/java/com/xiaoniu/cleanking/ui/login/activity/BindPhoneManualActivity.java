package com.xiaoniu.cleanking.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.login.bean.BindPhoneBean;
import com.xiaoniu.cleanking.ui.login.bean.IsPhoneBindBean;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneManualContract;
import com.xiaoniu.cleanking.ui.login.di.component.DaggerBindPhoneManualComponent;
import com.xiaoniu.cleanking.ui.login.presenter.BindPhoneManualPresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.widget.CommonTitleLayout;
import com.xiaoniu.cleanking.widget.rewrite.ClearAbleEditText;
import com.xiaoniu.cleanking.widget.rewrite.CountDownTextView;
import com.xiaoniu.cleanking.widget.rewrite.OnTextListener;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class BindPhoneManualActivity extends BaseActivity<BindPhoneManualPresenter> implements BindPhoneManualContract.View, OnTextListener {
    @BindView(R.id.titleLayout)
    CommonTitleLayout titleLayout;
    @BindView(R.id.bind_phone_et)
    ClearAbleEditText bindPhoneEt;
    @BindView(R.id.tv_verify)
    CountDownTextView tvVerify;
    @BindView(R.id.input_code_et)
    ClearAbleEditText inputCodeEt;
    @BindView(R.id.next_tv)
    TextView nextTv;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerBindPhoneManualComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_bind_phone_manual; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
        titleLayout.setMiddleTitle("绑定手机号").setTitleColor(R.color.color_262626).setBgColor(R.color.common_white).setLeftBackColor(R.color.color_666666);
        bindPhoneEt.setTextListener(this);
        inputCodeEt.setTextListener(this);
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

    @OnClick({R.id.next_tv, R.id.tv_verify, R.id.img_back})
    public void onViewClicked(View view) {
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.next_tv:
                String phone = bindPhoneEt.getText().toString().trim();
                String code = inputCodeEt.getText().toString().trim();
                mPresenter.phoneBind(phone, code);
                break;
            case R.id.tv_verify:
                String phoneNumber = bindPhoneEt.getText().toString().trim();
                if (checkIsPhoneNum(phoneNumber)) {
                    mPresenter.checkPhoneBinded(phoneNumber);
                }
                break;
            case R.id.img_back:
                finish();
                break;
            default:
                break;
        }
    }

    private boolean checkIsPhoneNum(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        if (!phoneNumber.startsWith("1")) {
            return false;
        }
        if (phoneNumber.length() != 11) {
            return false;
        }
        return true;
    }

    @Override
    public void onTextAfter() {
        if (!TextUtils.isEmpty(bindPhoneEt.getText().toString()) && !TextUtils.isEmpty(inputCodeEt.getText().toString())) {
            nextTv.setEnabled(true);
        } else {
            nextTv.setEnabled(false);
        }
    }

    @Override
    public void getBindPhoneSuccess(BindPhoneBean listBean) {
        ToastUtils.showShort("绑定成功");
        finish();
    }

    String isBinded = "-1";

    @Override
    public void getIsPhoneBindedSuccess(IsPhoneBindBean listBean) {
        //绑定
        isBinded = listBean.getData().getIsBinded();
        if ("1".equals(isBinded)) {
            ToastUtils.showShort("该手机号已被绑定");
        } else if ("0".equals(isBinded)) {
            //未綁定 获取验证码
            String phone = bindPhoneEt.getText().toString().trim();
            mPresenter.getCode(phone);
            inputCodeEt.setText("");
        }
    }

    @Override
    public void getCodeSuccess() {
        tvVerify.startDialog();
        ToastUtils.showShort("验证码发送成功");
    }
}

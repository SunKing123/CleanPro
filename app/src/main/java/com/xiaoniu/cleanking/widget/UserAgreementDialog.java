package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import com.lxj.xpopup.core.CenterPopupView;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.SpannableStringUtils;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.ArrayList;
import java.util.List;

public class UserAgreementDialog extends CenterPopupView {

    private TextView txt_content;
    private Button btn_cancel;
    private Button btn_confirm;

    private OnUserAgreementClickListener mOnUserAgreementClickListener;

    public UserAgreementDialog(@NonNull Context context, OnUserAgreementClickListener onUserAgreementClickListener) {
        super(context);
        this.mOnUserAgreementClickListener = onUserAgreementClickListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        StatisticsUtils.onPageStart("reminder_view_page", "温馨提示弹窗浏览");
        initView();
        initContentSpannable();
        initListener();
    }

    @Override
    protected void onDismiss() {
        StatisticsUtils.onPageEnd("reminder_view_page", "温馨提示弹窗浏览",
                "launch_page", "launch_page");
        super.onDismiss();
    }

    private void initListener() {
        btn_cancel.setOnClickListener(v -> {
            dismiss();
            if (mOnUserAgreementClickListener != null) {
                StatisticsUtils.trackClick("reminder_no_agree_click", "温馨提示不同意点击",
                        "launch_page", "launch_page");
                mOnUserAgreementClickListener.onUserRefuse();
            }
        });

        btn_confirm.setOnClickListener(v -> {
            dismiss();
            if (mOnUserAgreementClickListener != null) {
                StatisticsUtils.trackClick("reminder_agree_click", "温馨提示同意点击",
                        "launch_page", "launch_page");
                mOnUserAgreementClickListener.onUserAgree();
            }
        });
    }

    private void initContentSpannable() {
        final List<SpannableStringUtils.SpannableBean> spannableBeans = new ArrayList<>();
        spannableBeans.add(new SpannableStringUtils.SpannableBean(getResources().getString(R.string.user_agreement_label1),
                Color.parseColor("#666666"), null));
        spannableBeans.add(new SpannableStringUtils.SpannableBean(getResources().getString(R.string.user_agreement_label2),
                Color.parseColor("#06C581"), v ->
                SimpleWebActivity.startActivity(getContext(), BuildConfig.PRIVACY_POLICY, "隐私协议")));
        spannableBeans.add(new SpannableStringUtils.SpannableBean(getResources().getString(R.string.user_agreement_label3),
                Color.parseColor("#666666"), null));
        spannableBeans.add(new SpannableStringUtils.SpannableBean(getResources().getString(R.string.user_agreement_label4),
                Color.parseColor("#06C581"), v ->
                SimpleWebActivity.startActivity(getContext(), BuildConfig.USER_AGREEMENT, "用户协议")));
        spannableBeans.add(new SpannableStringUtils.SpannableBean(getResources().getString(R.string.user_agreement_label5),
                Color.parseColor("#666666"), null));
        SpannableStringBuilder builder = SpannableStringUtils.createSpannableString(spannableBeans);
        txt_content.setMovementMethod(LinkMovementMethod.getInstance());
        txt_content.setText(builder);
    }

    private void initView() {
        txt_content = findViewById(R.id.txt_content);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);
    }

    public interface OnUserAgreementClickListener {

        void onUserAgree();

        void onUserRefuse();
    }

}

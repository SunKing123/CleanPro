package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/24
 * Describe:启动也权限说明弹窗
 */
public class LaunchPermissionRemindDialog extends Dialog {
    Context mContext;
    @BindView(R.id.tv_use_agreement)
    TextView tvUseAgreement;
    @BindView(R.id.tv_next)
    TextView tvNext;
    private Handler handler = new Handler();

    public LaunchPermissionRemindDialog(@NonNull Context context) {
        super(context, R.style.dialogDefaultFullScreen);
        this.mContext = context;
        initDialog();
        //设置alterdialog全屏
        if (getWindow() != null) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.height = (int) (DisplayUtils.getScreenHeight()); //设置宽度
            lp.width = (int) (DisplayUtils.getScreenWidth()); //设置宽度
            getWindow().setAttributes(lp);
        }
    }

    private void initDialog() {
        setContentView(R.layout.dialog_launch_permission_remind);
        ButterKnife.bind(this, this);
        setCanceledOnTouchOutside(false);
        initData();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void initData() {
        String content1 = "详情查看";
        String content2 = "《用户协议》";
        String content3 = "和";
        String content4 = "《隐私政策》";
        String content = content1 + content2 + content3 + content4;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        int index2 = content.indexOf(content2);
        int index4 = content.indexOf(content4);
        spannableStringBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/agree.html");
                } else {
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/agree.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "隐私政策", "mine_page", "about_page");

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.color_02D086));       //设置文件颜色
                ds.setUnderlineText(false);     //去除下划线

            }
        }, index2, index2 + content2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/userAgreement.html");
                } else {
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/userAgreement.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "用户协议", "mine_page", "about_page");

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.color_02D086));//设置文件颜色
                ds.setUnderlineText(false);     //去除下划线
            }
        }, index4, index4 + content4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUseAgreement.setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
        tvUseAgreement.setText(spannableStringBuilder);
        tvUseAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public LaunchPermissionRemindDialog setLaunchPermissionListener(LaunchPermissionListener permissionListener) {
        this.permissionListener = permissionListener;
        return this;
    }

    private LaunchPermissionListener permissionListener;

    public interface LaunchPermissionListener {
        void nextOption();
    }

    public void jumpXieyiActivity(String url) {
        Intent intent = new Intent(mContext, UserLoadH5Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "服务协议");
        bundle.putBoolean(Constant.NoTitle, false);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @OnClick(R.id.tv_next)
    public void onViewClicked() {
        if (permissionListener != null) {
            permissionListener.nextOption();
        }
        dismiss();
    }
}

package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.usercenter.di.component.DaggerAboutInfoComponent;
import com.xiaoniu.cleanking.ui.usercenter.contract.AboutInfoContract;
import com.xiaoniu.cleanking.ui.usercenter.presenter.AboutInfoPresenter;

import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;


import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
public class AboutInfoActivity extends BaseActivity<AboutInfoPresenter> implements AboutInfoContract.View {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_newversion)
    TextView tv_newversion;
    @BindView(R.id.line_version)
    LinearLayout line_version;
    @BindView(R.id.line_zc)
    LinearLayout line_zc;
    @BindView(R.id.line_xy)
    LinearLayout line_xy;
    @BindView(R.id.line_share)
    LinearLayout line_share;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAboutInfoComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_about; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        iv_back.setOnClickListener(v -> finish());
        tv_version.setText("当前版本 V" + AppUtils.getVersionName(this, this.getPackageName()));
        //检测版本更新
        mPresenter.queryAppVersion(this,1);
//        line_version.setVisibility(View.GONE);
        line_version.setOnClickListener(v -> {
            StatisticsUtils.trackClick("Check_for_updates_click", "检查更新", "mine_page", "about_page");
            if (tv_newversion.getVisibility() == View.VISIBLE) {
                mPresenter.queryAppVersion(this,2 );
            } else {
                ToastUtils.showShort("当前已是最新版本");
            }

        });

        line_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/agree.html");
                } else {
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/agree.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "隐私政策", "mine_page", "about_page");
            }
        });

        line_xy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO) {
                    jumpXieyiActivity("file:///android_asset/userAgreement.html");
                } else {
                    jumpXieyiActivity(BuildConfig.Base_H5_Host + "/userAgreement.html");
                }
                StatisticsUtils.trackClick("Service_agreement_click", "用户协议", "mine_page", "about_page");
            }
        });

        line_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareContent = "HI，我发现了一款清理手机垃圾神器！推荐给你，帮你清理垃圾，从此再也不怕手机空间不够用来！";
                mPresenter.share("", BuildConfig.Base_H5_Host + "/share.html", getString(R.string.app_name), shareContent, -1);
                StatisticsUtils.trackClick("Sharing_friends_click", "分享好友", "mine_page", "about_page");
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

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("about_view_page", "关于");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("about_view_page", "关于");
    }



    public void jumpXieyiActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "服务协议");
        bundle.putBoolean(Constant.NoTitle, false);
        startActivity(new Intent(this,UserLoadH5Activity.class));
    }

    //显示是否有新版本文字
    public void setShowVersion(AppVersion result) {
        if (result != null && result.getData() != null) {
            //根据版本号判断是否需要更新
            String versionName = AppUtils.getVersionName(this, getPackageName());
            //默认可以下载
            if (!TextUtils.isEmpty(versionName) && !TextUtils.equals(versionName, result.getData().versionNumber) && !TextUtils.isEmpty(result.getData().downloadUrl)) {
                tv_newversion.setVisibility(View.VISIBLE);
            } else {//清空版本信息状态
                tv_newversion.setVisibility(View.GONE);
            }
        } else {
            tv_newversion.setVisibility(View.GONE);
        }
    }


    @Override
    public Activity getActivity() {
        return this;
    }
}

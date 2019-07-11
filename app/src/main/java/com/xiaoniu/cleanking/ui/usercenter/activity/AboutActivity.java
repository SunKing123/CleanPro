package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.usercenter.presenter.AboutPresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.BindView;

/**
 * 清理图片
 */
public class AboutActivity extends BaseActivity<AboutPresenter> {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_newversion)
    TextView tv_newversion;
    @BindView(R.id.line_version)
    LinearLayout line_version;
    @BindView(R.id.line_xy)
    LinearLayout line_xy;
    @BindView(R.id.line_share)
    LinearLayout line_share;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_version.setText("当前版本 V" + AndroidUtil.getAppVersionName());
        //检测版本更新
        mPresenter.queryAppVersion(1,() -> {
        });
        line_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_newversion.getVisibility()==View.VISIBLE){
                    mPresenter.queryAppVersion(2,() -> {
                    });
                }else{
                    ToastUtils.show("当前已是最新版本");
                }

            }
        });

        line_xy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpXieyiActivity("http://www.baidu.com");
            }
        });

        line_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareContent="HI，我发现了一款清理手机垃圾神器！推荐给你，帮你清理垃圾，从此再也不怕手机空间不够用来！";
                mPresenter.share("","","悟空清理",shareContent,-1);
                Log.e("sdds",""+ AppManager.getAppManager().preActivity().getComponentName());
                StatisticsUtils.trackClick("Circle_of_friends_click  ","朋友圈",source_page,"Sharing_page");
            }
        });
    }


    @Override
    public void netError() {

    }

    public void jumpXieyiActivity(String url){
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "服务协议");
        bundle.putBoolean(Constant.NoTitle, false);
        startActivity(UserLoadH5Activity.class, bundle);
    }
    //显示是否有新版本文字
    public void setShowVersion(AppVersion result) {
        if (result != null) {
            //根据版本号判断是否需要更新
            String versionName = AndroidUtil.getAppVersionName();
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
    protected void onDestroy() {
        super.onDestroy();
    }
}

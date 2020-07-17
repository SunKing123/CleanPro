package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.InsertScreenFinishPresenter;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import cn.jzvd.Jzvd;

/**
 * 全屏插屏广告
 * 由功能结束页进入
 */
@Deprecated
public class InsertScreenFinishActivity extends BaseActivity<InsertScreenFinishPresenter> implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);

        findViewById(R.id.linear_finish_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void netError() {

    }
}

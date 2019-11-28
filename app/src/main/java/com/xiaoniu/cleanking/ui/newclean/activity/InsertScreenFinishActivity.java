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
import com.xiaoniu.cleanking.ui.main.presenter.InsertScreenFinishPresenter;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import cn.jzvd.Jzvd;

/**
 * 插屏广告
 * 由功能结束页进入
 */
public class InsertScreenFinishActivity extends BaseActivity<InsertScreenFinishPresenter> implements View.OnClickListener {

    private AdManager mAdManager;
    private String TAG = "GeekSdk";

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
        loadGeekSdk();
    }

    /**
     * 获取插屏广告并加载
     */
    private void loadGeekSdk() {
        mAdManager = GeekAdSdk.getAdsManger();
        mAdManager.loadCustomInsertScreenAd(this, "cp_ad_2", 3, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (null == info) return;
                Log.d(TAG, "-----adSuccess-----=" + info.getAdSource() + "---" + info.getAdAppid());
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", info.getAdId(), info.getAdSource(), "success", NewCleanFinishActivity.currentPage, "screen_advertising");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), NewCleanFinishActivity.currentPage, "screen_advertising", info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), NewCleanFinishActivity.currentPage, "screen_advertising", info.getAdTitle());
            }

            @Override
            public void adClose(AdInfo info) {
                Log.d(TAG, "adClose---");
                PreferenceUtil.saveShowAD(false);
                finish();
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "关闭点击", "1", info.getAdId(), info.getAdSource(), NewCleanFinishActivity.currentPage, "screen_advertising", info.getAdTitle());
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                Log.d(TAG, "-----adError-----");
                finish();
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", " ", " ", "fail", NewCleanFinishActivity.currentPage, "screen_advertising");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        NiuDataAPI.onPageStart("screen_advertising_view_page", "插屏广告浏览");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        NiuDataAPIUtil.onPageEnd(NewCleanFinishActivity.currentPage, "screen_advertising", "screen_advertising_view_page", "插屏广告浏览");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void netError() {

    }

}

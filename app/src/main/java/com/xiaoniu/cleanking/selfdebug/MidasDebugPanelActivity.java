package com.xiaoniu.cleanking.selfdebug;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bytedance.embedapplog.AppLog;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xnad.sdk.MidasAdSdk;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.entity.AdType;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.config.AdParameter;

import androidx.annotation.Nullable;

/**
 * @author zhengzhihao
 * @date 2020/7/2 18
 * @mail：zhengzhihao@hellogeek.com
 */
public class MidasDebugPanelActivity extends BaseActivity implements View.OnClickListener{


    private Button splashType;
    private FrameLayout fragmentSplashType;
    private Button bannerType;
    private FrameLayout fragmentBannerType;
    private Button insertType;
    private FrameLayout fragmentInsertType;
    private Button rewardType;
    private FrameLayout fragmentRewardType;
    private Button nativeTemplateType;
    private FrameLayout fragmentTemplateType;
    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_debug_midas;
    }

    @Override
    protected void initView() {

        splashType = (Button) findViewById(R.id.splash_type);
        fragmentSplashType = (FrameLayout) findViewById(R.id.fragment_splash_type);
        bannerType = (Button) findViewById(R.id.banner_type);
        fragmentBannerType = (FrameLayout) findViewById(R.id.fragment_banner_type);
        insertType = (Button) findViewById(R.id.insert_type);
        fragmentInsertType = (FrameLayout) findViewById(R.id.fragment_insert_type);
        rewardType = (Button) findViewById(R.id.reward_type);
        fragmentRewardType = (FrameLayout) findViewById(R.id.fragment_reward_type);
        nativeTemplateType = (Button) findViewById(R.id.native_template_type);
        fragmentTemplateType = (FrameLayout) findViewById(R.id.fragment_template_type);

        splashType.setOnClickListener(this);
        bannerType.setOnClickListener(this);
        insertType.setOnClickListener(this);
        rewardType.setOnClickListener(this);
        nativeTemplateType.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splash_type:
                //加载展示广告
                //上下文、广告位置ID
                AdParameter adParameter = new AdParameter.Builder(this,"adpos_9193403301")
                        //设置填充父布局
                        .setViewContainer(fragmentSplashType)
                        .build();
                MidasAdSdk.getAdsManger().loadAd(adParameter,mAbsAdCallBack);

                break;
        }

    }


    private AbsAdCallBack mAbsAdCallBack = new AbsAdCallBack() {
        /**
         * 广告显示前回调
         * @param adInfo    广告信息实体
         */
        @Override
        public void onReadyToShow(AdInfo adInfo) {
            super.onReadyToShow(adInfo);
        }

        /**
         * 广告显示错误
         * @param errorCode 错误码
         * @param errorMsg  错误信息
         */
        @Override
        public void onShowError(int errorCode, String errorMsg) {
            LogUtils.e("错误" + errorMsg);
        }
        /**
         * 广告展示成功
         * @param adInfo    广告信息实体
         */
        @Override
        public void onAdShow(AdInfo adInfo) {
            LogUtils.e("广告被展示");
        }
        /**
         * 广告被点击
         * @param adInfo    广告信息实体
         */
        @Override
        public void onAdClicked(AdInfo adInfo) {
            LogUtils.e("广告被点击");
        }
        /**
         * 广告关闭
         * @param adInfo    广告信息实体
         */
        @Override
        public void onAdClose(AdInfo adInfo) {
            LogUtils.e("广告关闭");
            //这里只是示例调试使用，外部可以看情况处理
//            if (TextUtils.equals(adInfo.mAdType, AdType.SPLASH.adType)){
//                mViewContainer.removeAllViews();
//            }
        }

        @Override
        public void onAdVideoComplete(AdInfo adInfo) {
            super.onAdVideoComplete(adInfo);
        }

        /**
         * 广告激励事件
         * @param adInfo    广告信息实体
         */
        @Override
        public void onVideoRewardEvent(AdInfo adInfo) {
            LogUtils.e("广告激励事件");
        }
    };
}

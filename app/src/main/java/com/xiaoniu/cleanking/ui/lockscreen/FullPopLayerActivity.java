package com.xiaoniu.cleanking.ui.lockscreen;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.SystemUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com 全屏插屏弹出层Activity
 */
public class FullPopLayerActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout flayoutAdContainer;
    RelativeLayout full_screen_insert_ad_header_layout;
    private AdManager adManager;
    private TextView adShowTime;
    private ImageView adClose;
    private CountDownTimer countDownTimer;
    AdInfo adInfo;
    String adStyle = PositionId.AD_EXTERNAL_ADVERTISING_03;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this, FullPopLayerActivity.class);
        Log.e("dong","FullPopLayerActivity");
        setContentView(R.layout.activity_full_pop_layer);
        adStyle = getIntent().getStringExtra("ad_style");
        flayoutAdContainer = (RelativeLayout) findViewById(R.id.flayout_ad_container);
        full_screen_insert_ad_header_layout = (RelativeLayout) findViewById(R.id.full_screen_insert_ad_header_layout);
        adShowTime = (TextView) findViewById(R.id.full_insert_ad_show_time_txt);
        adClose = findViewById(R.id.full_insert_ad_close);
        countDownTimer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                adShowTime.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                adShowTime.setVisibility(View.GONE);
                adClose.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
        adClose.setOnClickListener(this);
        if (NetworkUtils.isNetConnected()) {
            adInit();

        } else {
            countDownTimer.cancel();
            finish();
        }
    }


    public void adInit() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page");
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, adStyle, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                Logger.i(SystemUtils.getProcessName(FullPopLayerActivity.this) +"---zz---success---"+info.getAdSource());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page");
                adInfo = info;
                View adView = info.getAdView();
                if (adView != null && flayoutAdContainer != null && full_screen_insert_ad_header_layout != null) {
                    full_screen_insert_ad_header_layout.setVisibility(View.VISIBLE);
                    flayoutAdContainer.removeAllViews();
                    flayoutAdContainer.addView(adView);
                    MmkvUtil.saveFullInsert();
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                Logger.i("zz---adExposed---"+info.getAdSource());
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page", info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                Logger.i("zz---adClicked---"+info.getAdSource());
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page", info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Logger.i("zz---adError"+info.getAdSource());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", "", "", "fail", "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page");
                finish();
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        if (null != adInfo) {
            StatisticsUtils.clickAD("close_click", "关闭点击", "1", adInfo.getAdId(), adInfo.getAdSource(), "external_insert_screen_full_screen_page", "external_insert_screen_full_screen_page", adInfo.getAdTitle());
        }
        finish();
    }
}

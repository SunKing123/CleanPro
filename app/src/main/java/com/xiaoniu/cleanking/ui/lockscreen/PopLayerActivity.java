package com.xiaoniu.cleanking.ui.lockscreen;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com
 * 弹出层Activity
 */
public class PopLayerActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout flayoutAdContainer;
    RelativeLayout full_screen_insert_ad_header_layout;
    private AdManager adManager;
    private TextView adShowTime,progree_tv;
    private ImageView  adClose;
    private int showTimeSecond = 3;
    private CountDownTimer countDownTimer;
    AdInfo adInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this,PopLayerActivity.class);
        setContentView(R.layout.activity_pop_layer);
        adShowTime = findViewById(R.id.full_screen_insert_ad_show_time_txt);
        flayoutAdContainer = (RelativeLayout)findViewById(R.id.flayout_ad_container);
        full_screen_insert_ad_header_layout = (RelativeLayout)findViewById(R.id.full_screen_insert_ad_header_layout);
        adClose = findViewById(R.id.full_screen_insert_ad_close);
        progree_tv = findViewById(R.id.progree_tv);
        adShowTime.setText(showTimeSecond + "s");
        adShowTime.setVisibility(View.VISIBLE);
        progree_tv.setText("已提速" + NumberUtils.mathRandom(25, 50) + "%");
        showTimeSecond = NumberUtils.mathRandomInt(25,50);
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
        if(NetworkUtils.isNetConnected()){
            adInit();
        }else{
            countDownTimer.cancel();
            finish();
        }
    }




    public void adInit() {
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, PositionId.KEY_EXTERNAL_ADVERTISING_AD_1, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "external_advertising_page", "external_advertising_page");
                adInfo = info;
                View adView = adManager.getAdView();
                if (adView != null) {
                    full_screen_insert_ad_header_layout.setVisibility(View.VISIBLE);
                    flayoutAdContainer.removeAllViews();
                    flayoutAdContainer.addView(adView);
                    int number = PreferenceUtil.getInstants().getInt(SpCacheConfig.POP_LAYER_NUMBERS);
                    PreferenceUtil.getInstants().saveInt(SpCacheConfig.POP_LAYER_NUMBERS,number+1);
                    PreferenceUtil.getInstants().save(SpCacheConfig.POP_LAYER_TIME, String.valueOf(System.currentTimeMillis()));
                }
            }

            @Override
            public void adExposed(AdInfo info) {

                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "external_advertising_page", "external_advertising_page", info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "external_advertising_page", "external_advertising_page", info.getAdTitle());
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", "", "", "fail", "external_advertising_page", "external_advertising_page");
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        finish();
        if(null!=adInfo){
            StatisticsUtils.clickAD("ad_close_click", "关闭点击", "1", adInfo.getAdId(), adInfo.getAdSource(), "external_advertising_page", "external_advertising_page", adInfo.getAdTitle());
        }

    }
}

package com.xiaoniu.cleanking.ui.lockscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.comm.jksdk.ad.entity.AdInfo;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com 弹出层Activity
 */
public class PopLayerActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout flayoutAdContainer;
    RelativeLayout full_screen_insert_ad_header_layout;
    //private TextView adShowTime;
    private TextView progree_tv;
    private ImageView adClose;
    private int showTimeSecond = 3;
    AdInfo adInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatisticsUtils.customTrackEvent("lock_screen_page_custom", "锁屏页面创建时", "", "lock_screen_page");
        ActivityCollector.addActivity(this, PopLayerActivity.class);
        setContentView(R.layout.activity_pop_layer);
        //adShowTime = findViewById(R.id.full_screen_insert_ad_show_time_txt);
        flayoutAdContainer = (RelativeLayout) findViewById(R.id.flayout_ad_container);
        full_screen_insert_ad_header_layout = (RelativeLayout) findViewById(R.id.full_screen_insert_ad_header_layout);
        adClose = findViewById(R.id.full_screen_insert_ad_close);
        progree_tv = findViewById(R.id.progree_tv);
        // adShowTime.setText(showTimeSecond + "s");
        // adShowTime.setVisibility(View.VISIBLE);
        progree_tv.setText("已提速" + NumberUtils.mathRandom(25, 50) + "%");
        showTimeSecond = NumberUtils.mathRandomInt(25, 50);
      /*  countDownTimer = new CountDownTimer(5 * 1000, 1000) {
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
        countDownTimer.start();*/
        adClose.setOnClickListener(this);
        if (NetworkUtils.isNetConnected()) {
            adInit();
        } else {
            // countDownTimer.cancel();
            finish();
        }
    }


    public void adInit() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "external_advertising_page", "external_advertising_page");
        AdRequestParams params = new AdRequestParams.Builder().setViewContainer(flayoutAdContainer)
                .setActivity(this).setAdId(MidasConstants.SCREEN_ON_ID).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdShow(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdShow(adInfo);
                LogUtils.e("===========桌面弹窗展示成功");
            }

            @Override
            public void onAdError(com.xnad.sdk.ad.entity.AdInfo adInfo, int i, String s) {
                super.onAdError(adInfo, i, s);
                LogUtils.e("===========桌面弹窗展示失败:" + s);
            }

            @Override
            public void onShowError(int i, String s) {
                super.onShowError(i, s);
                LogUtils.e("===========桌面弹窗展示失败：" + s);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        finish();
        if (null != adInfo) {
            StatisticsUtils.clickAD("ad_close_click", "关闭点击", "1", adInfo.getAdId(), adInfo.getAdSource(), "external_advertising_page", "external_advertising_page", adInfo.getAdTitle());
        }

    }
}

package com.xiaoniu.cleanking.ui.lockscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.base.BaseActivity;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
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
        countDownTimer = new CountDownTimer(3 * 1000, 1000) {
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
        adInit();
    }


    private void setLockerWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT > 18) {
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(lp);
        window.getDecorView().setSystemUiVisibility(0x0);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public void adInit() {
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, "external_advertising_ad_1", new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {

                View adView = adManager.getAdView();
                if (adView != null) {
                    full_screen_insert_ad_header_layout.setVisibility(View.VISIBLE);
                    flayoutAdContainer.removeAllViews();
                    flayoutAdContainer.addView(adView);
                }
            }

            @Override
            public void adExposed(AdInfo info) {


            }

            @Override
            public void adClicked(AdInfo info) {

            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                    finish();
            }
        });
    /*    AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadCustomInsertScreenAd(this, "external_advertising_ad_1", 3, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                LogUtils.d("-----adSuccess-----");
                String adStyle = info.getAdStyle();
                if ("EXTERNAL_CP_01".equals(adStyle)) { //外部插屏广告用addView的形式
                    View adView = adManager.getAdView();
                    if (adView != null) {
                        flayoutAdContainer.removeAllViews();
                        flayoutAdContainer.addView(adView);
                    }
                }
            }

            @Override
            public void adExposed(AdInfo info) {

            }

            @Override
            public void adClicked(AdInfo info) {

            }


            @Override
            public void adError(int errorCode, String errorMsg) {
              finish();
            }
        }, "80");*/
    }

    /**
     * 获取全屏插屏广告并加载
     */
    private void loadCustomInsertScreenAd2(String position) {
        LogUtils.d("position:" + position + " isFullScreen:");
        try {
            adManager.loadCustomInsertScreenAd(this, position, 3, new AdListener() {
                @Override
                public void adClose(AdInfo info) {
                    ActivityCollector.removeActivity(PopLayerActivity.this);
                    PopLayerActivity.this.finish();

                }

                @Override
                public void adError(int errorCode, String errorMsg) {
                    LogUtils.d("-----adError-----" + errorMsg);

                }

                @Override
                public void adSuccess(AdInfo info) {

                }

                @Override
                public void adExposed(AdInfo info) {
                    LogUtils.d("-----adExposed-----");
                }

                @Override
                public void adClicked(AdInfo info) {
                    LogUtils.d("-----adClicked-----");
                }
            }, String.valueOf(NumberUtils.mathRandom(25,50)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}

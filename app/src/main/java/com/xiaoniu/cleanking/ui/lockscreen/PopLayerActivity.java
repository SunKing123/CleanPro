package com.xiaoniu.cleanking.ui.lockscreen;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;

import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com 弹出层Activity
 */
public class PopLayerActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout adContainerFrameLayout;
    RelativeLayout full_screen_insert_ad_header_layout;
    //private TextView adShowTime;
   // private TextView progree_tv;
    private ImageView adClose;
    private int showTimeSecond = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LogUtils.e("==========进入了PopLayerActivity");
        StatisticsUtils.customTrackEvent("ad_request_sdk", "外部插屏发起请求", "", "external_advertising_page");

        setContentView(R.layout.activity_pop_layer);
        //adShowTime = findViewById(R.id.full_screen_insert_ad_show_time_txt);
        adContainerFrameLayout = findViewById(R.id.pop_layer_ff);
        full_screen_insert_ad_header_layout = (RelativeLayout) findViewById(R.id.full_screen_insert_ad_header_layout);
        adClose = findViewById(R.id.full_screen_insert_ad_close);
      //  progree_tv = findViewById(R.id.progree_tv);
        // adShowTime.setText(showTimeSecond + "s");
        // adShowTime.setVisibility(View.VISIBLE);
     //   progree_tv.setText("已提速" + NumberUtils.mathRandom(25, 50) + "%");
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
        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getInsertAdMidasId(PositionId.KEY_PAGE_OUTSIDE_SCREEN),new SimpleViewCallBack(adContainerFrameLayout){
            @Override
            public void onAdLoaded(AdInfoModel adInfoModel) {
                full_screen_insert_ad_header_layout.setVisibility(View.VISIBLE);
//                adClose.setVisibility(View.VISIBLE);
                super.onAdLoaded(adInfoModel);
            }

            @Override
            public void onAdExposure(AdInfoModel adInfoModel) {
                super.onAdExposure(adInfoModel);
                mAdInfoModel = adInfoModel;
            }

            @Override
            public void onAdClose(AdInfoModel adInfoModel) {
                super.onAdClose(adInfoModel);
                finish();
            }

            @Override
            public void onAdLoadError(String errorCode, String errorMsg) {
                super.onAdLoadError(errorCode, errorMsg);
                finish();
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private AdInfoModel mAdInfoModel;

    @Override
    public void onClick(View v) {
        finish();

        if (null != mAdInfoModel) {
            StatisticsUtils.trackClick("ad_close_click", "关闭点击", "external_advertising_page", "external_advertising_page");
        }

    }
}

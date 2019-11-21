package com.xiaoniu.cleanking.ui.lockscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author zhengzhihao
 * @date 2019/11/16 18
 * @mail：zhengzhihao@hellogeek.com
 * 弹出层Activity
 */
public class PopLayerActivity extends AppCompatActivity {
    FrameLayout flayoutAdContainer;
    private AdManager adManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        ActivityCollector.addActivity(this,PopLayerActivity.class);
        setContentView(R.layout.activity_pop_layer);
        flayoutAdContainer = (FrameLayout)findViewById(R.id.flayout_ad_container);
        adManager = GeekAdSdk.getAdsManger();
        //todo_zzh
        loadCustomInsertScreenAd2("external_advertising_ad_1");
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
   /* @Override
    protected int getLayoutResId() {
        return
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }*/


    /**
     * 获取全屏插屏广告并加载
     */
    private void loadCustomInsertScreenAd2(String position) {
        LogUtils.d("position:" + position + " isFullScreen:");
        try {
            adManager.loadCustomInsertScreenAd(this, position, 3, new AdListener() {
                @Override
                public void adSuccess() {
                }

                @Override
                public void adExposed() {
                    LogUtils.d("-----adExposed-----");
                }

                @Override
                public void adClicked() {
                    LogUtils.d("-----adClicked-----");
                }

                @Override
                public void adError(int errorCode, String errorMsg) {
                    LogUtils.d("-----adError-----" + errorMsg);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

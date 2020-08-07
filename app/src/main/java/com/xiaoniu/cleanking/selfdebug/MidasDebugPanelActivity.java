package com.xiaoniu.cleanking.selfdebug;

import android.view.View;
import android.widget.FrameLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

/**
 * @author zhengzhihao
 * @date 2020/7/2 18
 * @mail：zhengzhihao@hellogeek.com
 */
public class MidasDebugPanelActivity extends BaseActivity{


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
        findViewById(R.id.debug_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MidasRequesCenter.requestAndShowAd(MidasDebugPanelActivity.this, MidasConstants.LOCK_PAGE_FEED_ID,
                        new SimpleViewCallBack(findViewById(R.id.ad_container)));
            }
        });
    }


}

package com.xiaoniu.cleanking.ui.deskpop.deviceinfo;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopConfig;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopLogger;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 */
public class ExternalPhoneStateActivity extends BaseActivity {

    @BindView(R.id.scene_close)
    AppCompatImageView sceneClose;
    @BindView(R.id.ad_container)
    FrameLayout adContainer;
    private FragmentManager mManager = getSupportFragmentManager();

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        DeskPopLogger.log("=======================in ExternalPhoneStateActivity create()========================");
        return R.layout.activity_external_phone_state_layout;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
        initFragment();
        initView();
        //当打开时，将弹框数量递减
        DeskPopConfig.getInstance().saveAndDecreaseStatePopNum();
    }

    private void initView() {
        sceneClose.setOnClickListener(v -> close());
    }

    private void close() {
        StatisticsUtils.trackClick(Points.ExternalDevice.CLICK_CLOSE_CODE, Points.ExternalDevice.CLICK_CLOSE_NAME, "", Points.ExternalDevice.PAGE);
        finish();
    }

    private void initFragment() {
        mManager.beginTransaction()
                .add(R.id.frame_layout, DeviceInfoFragment.Companion.getInstance())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeskPopLogger.log("=======================in ExternalPhoneStateActivity onDestroy()========================");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            finish();
        }else{
            initAd();
        }
    }

    /**
     * 广告展示
     */
    public void initAd(){
        if (isFinishing() || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_DESK_DEVICE_STATE_AD))
            return;
//            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "acceleration_page", "acceleration_page");
        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_PAGE_DESK_DEVICE_STATE_AD, PositionId.DRAW_ONE_CODE), new SimpleViewCallBack(adContainer){
            @Override
            public void onAdClick(AdInfoModel adInfoModel) {
                super.onAdClick(adInfoModel);
                ExternalPhoneStateActivity.this.finish();
            }
        });


    }
}

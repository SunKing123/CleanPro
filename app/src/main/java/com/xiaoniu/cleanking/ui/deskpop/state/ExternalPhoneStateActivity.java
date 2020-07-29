package com.xiaoniu.cleanking.ui.deskpop.state;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.deskpop.DeskPopConfig;
import com.xiaoniu.cleanking.ui.newclean.activity.ExternalSceneActivity;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;

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
    private FragmentManager mManager = getSupportFragmentManager();

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        LogUtils.e("=======================pulseTimer   in ExternalPhoneStateActivity create()========================");
        return R.layout.activity_external_phone_state_layout;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
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
                .add(R.id.frame_layout, new ExternalPhoneStateFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("=======================pulseTimer in ExternalPhoneStateActivity onDestroy()========================");
    }
}

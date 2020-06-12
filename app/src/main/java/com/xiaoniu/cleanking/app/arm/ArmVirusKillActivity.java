/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoniu.cleanking.app.arm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.master.cleanking.interfaces.FragmentCallBack;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.COMPLETE;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.PAGE_VIEW;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.SCAN;


/**
 * 病毒查杀
 */
public class ArmVirusKillActivity extends BaseActivity<VirusKillPresenter> implements VirusKillContract.View, FragmentCallBack {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    @Inject
    RxPermissions mRxPermissions;// 当前不和服务端交互。

    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mManager = getSupportFragmentManager();


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerVircusKillComponent
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_arm_virus_kill;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparentForWindow(this);
        initFragments();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    VirusKillOneFragment oneFragment;
    VirusKillTwoFragment twoFragment;

    private void initFragments() {

        oneFragment = VirusKillOneFragment.getIntance();
        twoFragment = VirusKillTwoFragment.getIntance();
        oneFragment.setFragmentCallBack(this);
        mFragments.add(oneFragment);
        mFragments.add(twoFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, oneFragment)
                .add(R.id.frame_layout, twoFragment)
                .hide(twoFragment)
                .commitAllowingStateLoss();

    }

    private int positionIndex = 0;

    private void showHideFragment(int position, int prePosition) {
        positionIndex = position;
        FragmentTransaction ft = mManager.beginTransaction();
        ft.show(mFragments.get(position));
        if (prePosition != -1) {
            ft.hide(mFragments.get(prePosition));
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mRxPermissions = null;
        if (oneFragment != null) {
            oneFragment.onFragmentDestroy();
        }
        if (twoFragment != null) {
            twoFragment.onFragmentDestroy();
        }
    }

    @Override
    public void checkFragment() {
        showHideFragment(1, 0);
    }


    @Override
    public void onBackPressedSupport() {
        switch (VirusKillStatus.code){
            case PAGE_VIEW:
                Log.e("virusKill","用户浏览页面点击返回");
                StatisticsUtils.trackClick("system_return_click","用户在病毒查杀页返回","","virus_killing_scan_page");
                break;
            case SCAN:
                Log.e("virusKill","用户扫描页面点击返回");
                StatisticsUtils.trackClick("system_return_click","病毒查杀动画页返回","virus_killing_scan_page","virus_killing_animation_page");
                break;
            case COMPLETE:
                Log.e("virusKill","用户完成页面点击返回");
                StatisticsUtils.trackClick("system_return_click","病毒查杀动画完成页返回","virus_killing_animation_page","virus_killing_finish_animation_page");
                break;
        }
        super.onBackPressedSupport();
    }
}

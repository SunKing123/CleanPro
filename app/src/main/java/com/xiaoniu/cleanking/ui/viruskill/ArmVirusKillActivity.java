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
package com.xiaoniu.cleanking.ui.viruskill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
import com.xiaoniu.cleanking.ui.viruskill.contract.VirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.di.component.DaggerVircusKillComponent;
import com.xiaoniu.cleanking.ui.viruskill.presenter.VirusKillPresenter;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.xiaoniu.cleanking.ui.viruskill.VirusKillStatus.COMPLETE;
import static com.xiaoniu.cleanking.ui.viruskill.VirusKillStatus.PAGE_VIEW;
import static com.xiaoniu.cleanking.ui.viruskill.VirusKillStatus.SCAN;


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
        switch (VirusKillStatus.code) {
            case PAGE_VIEW:
                StatisticsUtils.trackClick(Points.SYSTEM_RETURN_CLICK_EVENT_CODE, Points.Virus.SCAN_SYSTEM_RETURN_EVENT_NAME, "", Points.Virus.SCAN_PAGE);
                break;
            case SCAN:
                StatisticsUtils.trackClick(Points.SYSTEM_RETURN_CLICK_EVENT_CODE, Points.Virus.ANIMATION_SYSTEM_RETURN_EVENT_NAME, Points.Virus.SCAN_PAGE, Points.Virus.ANIMATION_PAGE);
                break;
            case COMPLETE:
                StatisticsUtils.trackClick(Points.SYSTEM_RETURN_CLICK_EVENT_CODE, Points.Virus.ANIMATION_FINISH_SYSTEM_RETURN_EVENT_NAME, Points.Virus.ANIMATION_PAGE, Points.Virus.ANIMATION_FINISH_PAGE);
                break;
        }
        super.onBackPressedSupport();
    }
}

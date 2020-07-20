package com.xiaoniu.cleanking.ui.viruskill.newversion.fragment;

import android.os.Bundle;

import com.jess.arms.base.SimpleFragment;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class NewVirusScanFragment extends SimpleFragment implements NewVirusKillContract.VirusScanView {

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void startScanLoading() {

    }

    @Override
    public void setScanTitle(String title) {

    }

    @Override
    public void setPrivacyCount(int count) {

    }

    @Override
    public void addScanItem(String text) {

    }

    @Override
    public void showScanVirusIcons() {

    }

    @Override
    public void setScanVirusComplete() {

    }

    @Override
    public void addScanNetWorkItem(String text) {

    }

    @Override
    public void scanComplete() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }
}

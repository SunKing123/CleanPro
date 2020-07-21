package com.xiaoniu.cleanking.ui.viruskill.newversion.presenter;

import com.jess.arms.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.old.contract.VirusKillContract;

import javax.inject.Inject;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusScanPresenter extends BasePresenter<VirusKillContract.Model, NewVirusKillContract.VirusScanView> implements NewVirusKillContract.VirusScanPresenter {

    @Inject
    public VirusScanPresenter(VirusKillContract.Model model, NewVirusKillContract.VirusScanView rootView) {
        super(model, rootView);
    }

    @Override
    public void onScanLoadingProgress(int num) {

    }
}

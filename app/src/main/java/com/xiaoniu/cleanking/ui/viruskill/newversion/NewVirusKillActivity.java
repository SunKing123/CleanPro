package com.xiaoniu.cleanking.ui.viruskill.newversion;

import android.os.Bundle;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.viruskill.newversion.fragment.NewVirusScanFragment;
import com.xiaoniu.cleanking.ui.viruskill.newversion.fragment.VirusScanResultFragment;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.ScanTextItemModel;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class NewVirusKillActivity extends BaseActivity implements ITransferPagePerformer {

    private NewVirusScanFragment scanFragment;
    private FragmentManager mManager = getSupportFragmentManager();

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

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

    private void initFragments() {
        scanFragment = NewVirusScanFragment.getInstance();
        scanFragment.setTransferPagePerformer(this);
        mManager.beginTransaction()
                .add(R.id.frame_layout, scanFragment)
                .commitAllowingStateLoss();
    }


    @Override
    public void onTransferResultPage(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList) {
        VirusScanResultFragment resultFragment = new VirusScanResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(VirusScanResultFragment.IntentKey.P_LIST, pList);
        bundle.putParcelableArrayList(VirusScanResultFragment.IntentKey.N_LIST, nList);
        resultFragment.setArguments(bundle);
        resultFragment.setTransferPagePerformer(this);

        mManager.beginTransaction()
                .replace(R.id.frame_layout, resultFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onTransferCleanPage(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList) {

    }
}

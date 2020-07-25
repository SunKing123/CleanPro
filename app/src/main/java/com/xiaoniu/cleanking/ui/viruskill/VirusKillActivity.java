package com.xiaoniu.cleanking.ui.viruskill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil;
import com.xiaoniu.cleanking.ui.outfield.OutfieldPhoneStateFragment;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.viruskill.fragment.NewVirusScanFragment;
import com.xiaoniu.cleanking.ui.viruskill.fragment.VirusCleanFragment;
import com.xiaoniu.cleanking.ui.viruskill.fragment.VirusScanResultFragment;
import com.xiaoniu.cleanking.ui.viruskill.model.NetworkDataStore;
import com.xiaoniu.cleanking.ui.viruskill.model.PrivacyDataStore;
import com.xiaoniu.cleanking.ui.viruskill.model.ScanTextItemModel;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusKillActivity extends BaseActivity implements ITransferPagePerformer {

    private NewVirusScanFragment scanFragment;
    private FragmentManager mManager = getSupportFragmentManager();
    private boolean isCleaning = false;

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
                .add(R.id.frame_layout, new OutfieldPhoneStateFragment())
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
        isCleaning = true;
        VirusCleanFragment cleanFragment = new VirusCleanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(VirusCleanFragment.IntentKey.P_LIST, pList);
        bundle.putParcelableArrayList(VirusCleanFragment.IntentKey.N_LIST, nList);
        cleanFragment.setArguments(bundle);
        cleanFragment.setTransferPagePerformer(this);

        mManager.beginTransaction()
                .replace(R.id.frame_layout, cleanFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void cleanComplete() {
        //设置锁屏数据
        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(2);
        btnInfo.setNormal(true);
        btnInfo.setCheckResult("500");
        btnInfo.setReShowTime(System.currentTimeMillis() + 1000 * 60 * 5);
        PreferenceUtil.getInstants().save("lock_pos03", new Gson().toJson(btnInfo));
        EventBus.getDefault().post(btnInfo);
        //保存杀毒完成时间
        PreferenceUtil.saveVirusKillTime();

        AppHolder.getInstance().setCleanFinishSourcePageId("virus_killing_animation_page");
        EventBus.getDefault().post(new FunctionCompleteEvent(getString(R.string.virus_kill)));

        Intent mIntent = new Intent();
        mIntent.putExtra(ExtraConstant.TITLE, getString(R.string.virus_kill));
        if (getIntent().hasExtra(ExtraConstant.ACTION_NAME) && !TextUtils.isEmpty(getIntent().getStringExtra(ExtraConstant.ACTION_NAME))) {
            mIntent.putExtra(ExtraConstant.ACTION_NAME, getIntent().getStringExtra(ExtraConstant.ACTION_NAME));
        }
        StartFinishActivityUtil.Companion.gotoFinish(this, mIntent);
        finish();

        PrivacyDataStore.getInstance().removeMarkedIdsInRandomTable();
        NetworkDataStore.getInstance().removeMarkedIdsInRandomTable();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCleaning) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

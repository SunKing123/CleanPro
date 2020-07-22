package com.xiaoniu.cleanking.ui.viruskill.newversion;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.viruskill.newversion.fragment.NewVirusScanFragment;
import com.xiaoniu.cleanking.ui.viruskill.old.VirusKillOneFragment;
import com.xiaoniu.cleanking.ui.viruskill.old.VirusKillTwoFragment;
import com.xiaoniu.cleanking.ui.viruskill.old.presenter.VirusKillPresenter;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class NewVirusKillActivity extends BaseActivity {

    private NewVirusScanFragment scanFragment;
    private List<Fragment> mFragments = new ArrayList<>();
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
        mFragments.add(scanFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, scanFragment)
                .commitAllowingStateLoss();

    }

}

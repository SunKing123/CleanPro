package com.xiaoniu.cleanking.ui.main.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CommonFragmentPageAdapter;
import com.xiaoniu.cleanking.ui.main.fragment.QQImgFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * qq图片清理
 * Created by lang.chen on 2019/8/6
 */
public class QQCleanImgActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<Fragment> mFragments = new ArrayList<>();
    private CommonFragmentPageAdapter mAdapter;


    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.qq_clean_img_main;
    }

    @Override
    protected void initView() {
        mFragments.add(QQImgFragment.newInstance());
        mAdapter = new CommonFragmentPageAdapter(getSupportFragmentManager());
        mAdapter.modifyList(mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }




    @OnClick({R.id.img_back})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }
    }
}

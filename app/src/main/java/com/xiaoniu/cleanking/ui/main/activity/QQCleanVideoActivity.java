package com.xiaoniu.cleanking.ui.main.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CommonFragmentPageAdapter;
import com.xiaoniu.cleanking.ui.main.fragment.QQVideoFragment;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * qq视频清理
 * Created by lang.chen on 2019/8/6
 */
public class QQCleanVideoActivity extends BaseActivity {


    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    //
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
        return R.layout.qq_clean_video_main;
    }

    @Override
    protected void initView() {

        mFragments.add(QQVideoFragment.newInstance());
        mAdapter = new CommonFragmentPageAdapter(getSupportFragmentManager());
        mAdapter.modifyList(mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    @OnClick({R.id.img_back})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            StatisticsUtils.trackClick("qq_video_return_click","\"qq视频返回\"点击"
                    ,"qq_cleaning_page","qq_video_cleaning_page");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsUtils.trackClick("qq_video_return_click","\"qq视频返回\"点击"
                ,"qq_cleaning_page","qq_video_cleaning_page");
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("qq_video_cleaning_view_page","视频清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("qq_video_cleaning_view_page","视频清理页面浏览");

    }
}

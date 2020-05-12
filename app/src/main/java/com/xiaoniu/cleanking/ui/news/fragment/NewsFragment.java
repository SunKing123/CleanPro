package com.xiaoniu.cleanking.ui.news.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.news.adapter.NewsTypeNavigatorAdapter;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.cleanking.widget.magicIndicator.MagicIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.ViewPagerHelper;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.CommonNavigator;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.base.BaseFragment;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;

/**
 * viewpager 新闻页
 */
public class NewsFragment extends BaseFragment {
    private static final String KEY_TYPE = "TYPE";
    private NewsType[] mNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUOJI, NewsType.YUN_SHI, NewsType.JIAN_KANG, NewsType.REN_WEN};
    private NewsTypeNavigatorAdapter mNewsTypeNaviAdapter;

    private ViewPager mViewPager;
    private MagicIndicator mTabIndicator;
    private ArrayList<NewsListFragment> mFragments;
    private ImageView mIvBack;
    private String mType = "white";
    private LinearLayout mFLTopNav;
    private boolean isShowBack = false;

    /**
     * @param type 白色样式、绿色样式
     * @return
     */
    public static NewsFragment getNewsFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TYPE, type);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public ImageView getIvBack() {
        return mIvBack;
    }

    public void showIvBack(boolean show) {
        isShowBack = show;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initVariable(Bundle arguments) {
        mNewTypes = NewsUtils.sNewTypes;
        mFragments = new ArrayList<>();
        if (arguments != null) {
            mType = arguments.getString(KEY_TYPE);
        }
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        mIvBack = contentView.findViewById(R.id.iv_back);
        mViewPager = contentView.findViewById(R.id.newsViewPager);
        mViewPager.setOffscreenPageLimit(6);
        mTabIndicator = contentView.findViewById(R.id.newsTabIndicator);
        mFLTopNav = contentView.findViewById(R.id.fl_top_nav);
        if (isShowBack) {
            mIvBack.setVisibility(View.VISIBLE);
        }
        if ("white".equals(mType)) {
            mIvBack.setColorFilter(Color.BLACK);
        }
        initFeedView();
    }

    private void initFeedView() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                StatisticsUtils.trackClickNewsTab("content_cate_click", "“分类”点击", "selected_page", "information_page", i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initMagicIndicator();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setSkimOver(true);
        mNewsTypeNaviAdapter = new NewsTypeNavigatorAdapter(mNewTypes, false);
        mNewsTypeNaviAdapter.setOnClickListener(new NewsTypeNavigatorAdapter.OnClickListener() {
            @Override
            public void onClickTitleView(int index) {
                onClickNavigator(index);
            }
        });
        commonNavigator.setAdapter(mNewsTypeNaviAdapter);
//        mTabIndicator.setBackgroundColor(getResources().getColor(R.color.transparent));
        mTabIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mTabIndicator, mViewPager);
    }

    private void onClickNavigator(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> getActivity().finish());
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
        }
    }

    @Override
    protected void loadData() {
        for (int i = 0; i < mNewTypes.length; i++) {
            NewsListFragment listFragment = NewsListFragment.getInstance(mNewTypes[i]);
            mFragments.add(listFragment);
        }

        FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // mTabIndicator.setCurrentTab(0);
            mViewPager.setCurrentItem(0);
            mFragments.get(0).startLoadData();

            NiuDataAPI.onPageStart("information_page_view_page", "信息页面浏览");
            StatisticsUtils.trackClickNewsTab("content_cate_click", "“分类”点击", "selected_page", "information_page", 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_27D599), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_27D599), false);
            }
        } else {
            NiuDataAPI.onPageEnd("information_page_view_page", "信息页面浏览");
        }
    }
}

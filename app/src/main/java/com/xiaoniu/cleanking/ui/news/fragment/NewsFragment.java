package com.xiaoniu.cleanking.ui.news.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.base.BaseFragment;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.viewpagerindicator.IScrollBar;
import com.xiaoniu.common.widget.viewpagerindicator.IndicatorAdapter;
import com.xiaoniu.common.widget.viewpagerindicator.LineScrollBar;
import com.xiaoniu.common.widget.viewpagerindicator.ViewPagerIndicator;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {
    private static final String KEY_TYPE = "TYPE";
    private NewsType[] mNewTypes = {NewsType.TOUTIAO, NewsType.VIDEO, NewsType.SHEHUI, NewsType.GUONEI, NewsType.GUOJI, NewsType.YULE};
    private ViewPager mViewPager;
    private ViewPagerIndicator mTabIndicator;
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

    public void showIvBack(boolean show){
        isShowBack = show;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initVariable(Bundle arguments) {
        mFragments = new ArrayList<>();
        if (arguments != null) {
            mType = arguments.getString(KEY_TYPE);
        }
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        mIvBack = contentView.findViewById(R.id.iv_back);
        mViewPager = contentView.findViewById(R.id.newsViewPager);
        mViewPager.setOffscreenPageLimit(5);
        mTabIndicator = contentView.findViewById(R.id.newsTabIndicator);
        mFLTopNav = contentView.findViewById(R.id.fl_top_nav);
        if (isShowBack) {
            mIvBack.setVisibility(View.VISIBLE);
        }
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
        mTabIndicator.bindViewPager(mViewPager);
        mTabIndicator.setIndicatorAdapter(new IndicatorAdapter() {
            @Override
            public View getTabView(Context context, int position) {
                TextView textView = (TextView) mInflater.inflate(R.layout.layout_news_tab_item, null);
                textView.setText(mNewTypes[position].getName());
                if ("white".equals(mType)) {
                    mTabIndicator.setBackgroundColor(Color.WHITE);
                    mFLTopNav.setBackgroundColor(Color.WHITE);
                    mIvBack.setColorFilter(Color.BLACK);
                }else {
                    textView.setTextColor(Color.WHITE);
                }
                return textView;
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setColor(Color.WHITE);//滚动块颜色
                scrollBar.setHeight(DisplayUtils.dip2px(context, 2));//滚动块高度，不设置默认和每个tabview高度一致
                scrollBar.setRadius(DisplayUtils.dip2px(context, 1));//滚动块圆角半径
                scrollBar.setGravity(Gravity.BOTTOM);//可设置上中下三种
                scrollBar.setWidth(DisplayUtils.dip2px(context, 12));//滚动块宽度，不设置默认和每个tabview宽度一致
                return scrollBar;
            }

            @Override
            public int getTabCount() {
                return mFragments.size();
            }

            @Override
            public void onTabChange(View tabView, int position, float selectPercent) {
            }
        });

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
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mTabIndicator.setCurrentTab(0);
            mFragments.get(0).startLoadData();
            NiuDataAPI.onPageStart("information_page_view_page", "信息页面浏览");
            StatisticsUtils.trackClickNewsTab("content_cate_click", "“分类”点击", "selected_page", "information_page",0);
        }else {
            NiuDataAPI.onPageEnd("information_page_view_page", "信息页面浏览");
        }
    }
}

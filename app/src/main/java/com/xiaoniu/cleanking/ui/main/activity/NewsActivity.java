package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 新闻热点
 */
public class NewsActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_news_layout;
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setCenterTitle(getString(R.string.tool_news));
        replaceFragment(R.id.fragment_container, NewsFragment.getNewsFragment("white"), false);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }
}

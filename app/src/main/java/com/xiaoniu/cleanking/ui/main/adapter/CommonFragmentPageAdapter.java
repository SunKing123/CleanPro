package com.xiaoniu.cleanking.ui.main.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加 ViewPager Fragment
 */

public class CommonFragmentPageAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public CommonFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public CommonFragmentPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }


    public void modifyList(List<Fragment> fragments) {
        mFragments.addAll(fragments);
        notifyDataSetChanged();
    }


    public void clear() {
        if (mFragments.size() > 0) {
            mFragments.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }


    @Override
    public int getCount() {
        return (null == mFragments) ? 0 : mFragments.size();
    }

}

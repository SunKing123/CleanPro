package com.xiaoniu.cleanking.ui.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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


    /**
     * 重写该方法用来实现pageAdapter刷新
     * @param object
     * @return
     */
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
}

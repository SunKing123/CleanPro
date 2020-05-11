package com.xiaoniu.cleanking.ui.news.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xiaoniu.cleanking.ui.news.fragment.NewsListFragment;
import com.xiaoniu.common.base.BaseFragment;

import java.util.List;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/10
 */
public class ComFragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;

    public ComFragmentAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }

    public void setData(List<BaseFragment> fragments) {
        this.fragments.clear();
        this.fragments.addAll(fragments);
        for (BaseFragment lazyFragment : fragments){
            if(lazyFragment instanceof NewsListFragment){
                NewsListFragment newsFragment = (NewsListFragment) lazyFragment;
//                newsFragment.setIsLoaded(false);
                newsFragment.setIsRefresh(true);   // TODO
            }
        }
        notifyDataSetChanged();
    }


    public void resetScrollToTop(){
        if(fragments != null){
            for (BaseFragment lazyFragment : fragments){
                if(lazyFragment instanceof NewsListFragment){
                    NewsListFragment newsFragment = (NewsListFragment)lazyFragment;
                    newsFragment.returnTop();
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public List<BaseFragment> getFragments() {
        return fragments;
    }
}

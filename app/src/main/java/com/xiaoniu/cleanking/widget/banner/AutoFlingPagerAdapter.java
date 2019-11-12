package com.xiaoniu.cleanking.widget.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.LinkedList;
import java.util.List;

public abstract class AutoFlingPagerAdapter<T> extends PagerAdapter {
    protected List<T> mData;
    private List<View> mRecycleViews;

    public AutoFlingPagerAdapter() {
        mRecycleViews = new LinkedList<View>();
    }

    public int getRealCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getCount() {
        return mData != null ? (mData.size() == 1 ? 1 : Integer.MAX_VALUE) : 0;
    }

    public T getItem(int position) {
        int currentPosition = getRealCount() == 0 ? 0 : position % getRealCount();
        return mData != null ? mData.get(currentPosition) : null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public abstract View instantiateView(Context context);

    public abstract void bindView(T t, View view, int position);

    public abstract String getTitle(int position);

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        int currentPosition = getRealCount() == 0 ? 0 : position % getRealCount();
        View view = null;
        if (mRecycleViews.size() == 0) {
            view = instantiateView(container.getContext());
        } else {
            view = mRecycleViews.remove(0);
        }
        bindView(getItem(currentPosition), view, currentPosition);
        if (view != null) {
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mRecycleViews.add((View) object);
    }

    public void append(List<T> items) {
        if (items != null && !items.isEmpty()) {
            mData.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void add(T item) {
        if (item != null) {
            mData.add(item);
            notifyDataSetChanged();
        }
    }

    public void remove(T item) {
        if (item != null) {
            mData.remove(item);
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void setData(List<T> items) {
        if (items != null && !items.isEmpty()) {
            mData = items;
            notifyDataSetChanged();
        }
    }

}

package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 代码描述<p>
 * @author zhengzhihao
 */
public class CustomLinearLayoutManger extends LinearLayoutManager {

    public CustomLinearLayoutManger(Context context) {
        super(context);
    }


    public CustomLinearLayoutManger(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * recyclerview自带问题修复（java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position 2）
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {

        }

    }
}

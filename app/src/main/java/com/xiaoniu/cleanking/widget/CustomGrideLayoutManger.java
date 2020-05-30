package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 代码描述<p>
 * @author zhengzhihao
 */
public class CustomGrideLayoutManger extends GridLayoutManager {

    public CustomGrideLayoutManger(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        }catch (IndexOutOfBoundsException e){
            //手动catch住
            e.printStackTrace();
        }
    }
}

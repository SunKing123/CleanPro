package com.xiaoniu.cleanking.ui.main.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 为RecyclerView 网格布局设置间距
 * Created by lang.chen on 2019/3/16
 */
public class GrideManagerWrapper extends RecyclerView.ItemDecoration {

    private int mSpace;

    public GrideManagerWrapper(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mSpace;

    }
}

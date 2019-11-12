package com.xiaoniu.cleanking.ui.main.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.common.utils.DisplayUtils;

/**
 * author : zchu
 * date   : 2017/8/30
 * desc   : 为Recyclerview 的GridLayoutManager添加列间距
 */

public class CustomerSpaceDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // item position
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = DisplayUtils.dip2px(16);
        } else {
            outRect.left = 0;
        }
        outRect.right = DisplayUtils.dip2px(27);
    }
}

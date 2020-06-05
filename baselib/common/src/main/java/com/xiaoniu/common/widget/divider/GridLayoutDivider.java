package com.xiaoniu.common.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xinxiaolong on 2020/6/5.
 * email：xinxiaolong123@foxmail.com
 */
public class GridLayoutDivider extends RecycleViewDivider {


    public GridLayoutDivider(Context context, int orientation) {
        super(context, orientation);
    }

    public GridLayoutDivider(Context context, int orientation, int drawableId) {
        super(context, orientation, drawableId);
    }

    public GridLayoutDivider(Context context, int orientation, int dividerHeight, int dividerColor) {
        super(context, orientation, dividerHeight, dividerColor);
    }

    //绘制横向 item 分割线
    protected void drawHorizontal(Canvas canvas, RecyclerView parent) {

        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();

        final int left = parent.getPaddingLeft();//获取分割线的左边距，即RecyclerView的padding值
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();//分割线右边距
        final int childSize = parent.getChildCount();

        int spanCount = gridLayoutManager.getSpanCount();

        int totalSize = 0;
        int lineNum = -1;

        ArrayList<View> lineViewList = new ArrayList<>();

        for (int i = 0; i < childSize; i++) {
            int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(i);
            totalSize = totalSize + spanSize;
            int thisLine = totalSize / spanCount;
            //do not draw line without newLine;
            if (thisLine == lineNum) {
                continue;
            }
            lineViewList.add(parent.getChildAt(i));
            lineNum = thisLine;
        }

        //the lineNum starts at zero。less than lineNum because i did not draw last line。
        for (int i = 0; i < lineNum; i++) {
            final View child = lineViewList.get(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}

package com.xiaoniu.common.widget.xrecyclerview;

import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    public View itemView;

    public CommonViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.mViews = new SparseArray<View>();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
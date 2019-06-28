package com.installment.mall.callback;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tie on 2017/4/4.
 * 这个接口主要是为了监听某一组件的touch事件
 */

public interface StartDragListener {
    void onStartDrag(RecyclerView.ViewHolder view);
}

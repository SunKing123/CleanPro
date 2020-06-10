package com.xiaoniu.cleanking.utils;

import android.view.View;

public interface OnItemClickListener<T> {

    void onItemClick(View view, T data, int position);
}

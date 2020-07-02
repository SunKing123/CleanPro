package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;

import androidx.annotation.Nullable;

/**
 * Created by xinxiaolong on 2020/7/2.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeToolTableView extends LinearLayout {

    public HomeToolTableView(Context context) {
        super(context);
    }

    public HomeToolTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeToolTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HomeToolTableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_home_tool_table_layout, this);

    }

}

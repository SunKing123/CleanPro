package com.xiaoniu.cleanking.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.GlideUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Created by xinxiaolong on 2020/6/2.
 * email：xinxiaolong123@foxmail.com
 */
public class ItmMTView extends LinearLayout {

    private TextView tv;
    private ImageView ivIcon;
    private ImageView ivTick;
    private TextView tvMark;
    private ImageView ivLine;

    public ItmMTView(Context context) {
        super(context);
        init(context);
    }

    public ItmMTView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItmMTView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_main_table_view, this);
        tv = findViewById(R.id.tv_main_table_s2);
        ivIcon = findViewById(R.id.iv_item_main_s2);
        ivTick = findViewById(R.id.iv_tick_s2);
        tvMark = findViewById(R.id.tv_corner_mark);
        ivLine=findViewById(R.id.iv_line);
    }

    public void setVisibleTick(int visible) {
        ivTick.setVisibility(visible);
    }

    public void loadDrawable(Context context, int resId) {
        GlideUtils.loadDrawble(context, resId, ivIcon);
    }

    public void setText(CharSequence text) {
        tv.setText(text);
    }

    public void setTextColor(@ColorInt int color) {
        tv.setTextColor(color);
    }

    public void setMarkText(String text) {
        tvMark.setVisibility(View.VISIBLE);
        tvMark.setText(text);
    }

    public void clearMark() {
        tvMark.setVisibility(View.GONE);
    }

    public void setLineVisible(int visible){
        ivLine.setVisibility(visible);
    }
}

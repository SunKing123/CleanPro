package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by xinxiaolong on 2020/7/2.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeToolTableItemView extends ConstraintLayout {

    ImageView icon;
    TextView tvTitle;
    TextView tvContent;

    public HomeToolTableItemView(Context context) {
        super(context);

    }

    public HomeToolTableItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeToolTableItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_home_tool_table_item_layout, this);
        icon = findViewById(R.id.image_icon);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
    }

    public void setIcon(int resId) {
        icon.setImageResource(resId);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setContentColor(int color) {
        tvContent.setTextColor(color);
    }
    public void setContent(CharSequence content) {
        tvContent.setText(content);
    }
}

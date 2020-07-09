package com.xiaoniu.cleanking.ui.main.widget;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.umeng.commonsdk.debug.I;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.GlideUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

/**
 * Created by xinxiaolong on 2020/7/9.
 * email：xinxiaolong123@foxmail.com
 */
public class BottomScratchTab extends BottomBarTab {

    Context context;
    ImageView imageBottom;

    public BottomScratchTab(Context context, int icon, String iconString, CharSequence title, int orderNum) {
        super(context, icon, iconString, title, orderNum);
        init(context,iconString);
    }

    private void init(Context context,String url){
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.item_bottom_scratch_tab_view,this);
        imageBottom=findViewById(R.id.image_bottom);
        GlideUtils.loadImage(context, url, imageBottom);
    }

    protected void init(Context context, int icon, String iconString, CharSequence title, int orderNum) {

    }

    @Override
    public void setSelected(boolean selected) {

    }

}

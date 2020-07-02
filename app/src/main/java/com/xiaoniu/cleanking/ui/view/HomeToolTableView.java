package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import androidx.annotation.Nullable;

/**
 * Created by xinxiaolong on 2020/7/2.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeToolTableView extends LinearLayout {

    ViewGroup wxClean;
    TextView tvWxContent;
    HomeToolTableItemView itemTemperature;
    HomeToolTableItemView itemNotify;
    HomeToolTableItemView itemNetwork;
    HomeToolTableItemView itemFolder;

    public HomeToolTableView(Context context) {
        super(context);
    }

    public HomeToolTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeToolTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HomeToolTableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_home_tool_table_layout, this);
        wxClean = findViewById(R.id.wx_clean);
        tvWxContent = findViewById(R.id.tv_wx_content);
        itemTemperature = findViewById(R.id.item_temperature);
        itemNotify = findViewById(R.id.item_notify);
        itemNetwork = findViewById(R.id.item_network);
        itemFolder = findViewById(R.id.item_folder);
    }

    public void initViewState() {

        //wx
        if (PreferenceUtil.getWeChatCleanTime()) {
            wxCleanUnusedStyle();
        } else {
            wxCleanUsedStyle();
        }

        //cooling
        itemTemperature.setIcon(R.drawable.icon_home_temperature);
        itemTemperature.setTitle("手机降温");
        if (PreferenceUtil.getCoolingCleanTime()) {
            coolingUnusedStyle();
        } else {
            coolingUsedStyle();
        }

        //notify
        itemNotify.setTitle("通知栏清理");
        itemNotify.setIcon(R.drawable.icon_home_notify);
        if (PreferenceUtil.getNotificationCleanTime()) {
            notifyUnusedStyle();
        } else {
            notifyUsedStyle();
        }

        //network
        itemNetwork.setTitle("网络加速");
        itemNetwork.setIcon(R.drawable.icon_home_network);
        itemNetwork.setContent("有效提高20%");

        //folder
        itemFolder.setTitle("深度清理");
        itemFolder.setContent("大文件专清");
        itemFolder.setIcon(R.drawable.icon_home_folder);
    }

    /*
     ************************************************************************************************************************************************************************
     ********************************************************************wx clean style***********************************************************************************
     ************************************************************************************************************************************************************************
     */
    public void wxCleanUnusedStyle() {
        tvWxContent.setText("大量缓存垃圾");
    }

    public void wxCleanUsedStyle() {
        tvWxContent.setText("经常清理，使用更流畅");
    }

    /*
     ************************************************************************************************************************************************************************
     ********************************************************************cooling style***************************************************************************************
     ************************************************************************************************************************************************************************
     */
    public void coolingUnusedStyle() {
        String tHead = "温度已高达";
        String tColor = "41°";
        SpannableString text = AndroidUtil.inertColorText(tHead + tColor, tHead.length(), tHead.length() + tColor.length(), getRedColor());
        itemTemperature.setContent(text);
    }

    public void coolingUsedStyle() {
        String tHead = "已成功降温";
        String tColor = "36.5°";
        SpannableString text = AndroidUtil.inertColorText(tHead + tColor, tHead.length(), tHead.length() + tColor.length(), getGreenColor());
        itemTemperature.setContent(text);
    }


    /*
     ************************************************************************************************************************************************************************
     ********************************************************************notify style***************************************************************************************
     ************************************************************************************************************************************************************************
     */
    public void notifyUnusedStyle() {
        itemNotify.setContentColor(getYellowColor());
        itemNotify.setContent("发现骚扰通知");
    }

    public void notifyUsedStyle() {
        itemNotify.setContentColor(getNormalColor());
        itemNotify.setContent("开启防骚扰模式");
    }


    public int getRedColor() {
        return getContext().getResources().getColor(R.color.home_content_red);
    }

    public int getGreenColor() {
        return getContext().getResources().getColor(R.color.home_content_green);
    }

    public int getYellowColor() {
        return getContext().getResources().getColor(R.color.home_content_yellow);
    }

    public int getNormalColor() {
        return getContext().getResources().getColor(R.color.home_content_yellow);
    }
}

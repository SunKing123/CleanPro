package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import androidx.annotation.Nullable;

/**
 * Created by xinxiaolong on 2020/7/2.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeToolTableView extends LinearLayout {

    public static final int ITEM_WX = 1;
    public static final int ITEM_TEMPERATURE = 2;
    public static final int ITEM_NOTIFY = 3;
    public static final int ITEM_NETWORK = 4;
    public static final int ITEM_FOLDER = 5;

    private TextView wxBtn;
    private TextView tvWxContent;
    private HomeToolTableItemView itemTemperature;
    private HomeToolTableItemView itemNotify;
    private HomeToolTableItemView itemNetwork;
    private HomeToolTableItemView itemFolder;

    private IntentFilter iFilter;
    private Intent batteryStatus;

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

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_home_tool_table_layout, this);
        wxBtn = findViewById(R.id.btn_wx_clean);
        tvWxContent = findViewById(R.id.tv_wx_content);
        itemTemperature = findViewById(R.id.item_temperature);
        itemNotify = findViewById(R.id.item_notify);
        itemNetwork = findViewById(R.id.item_network);
        itemFolder = findViewById(R.id.item_folder);

        iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = AppApplication.getInstance().registerReceiver(null, iFilter);

        wxBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_WX);
            }
        });
        itemTemperature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_TEMPERATURE);
            }
        });
        itemNotify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_NOTIFY);
            }
        });
        itemNetwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_NETWORK);
            }
        });
        itemFolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_FOLDER);
            }
        });
    }


    private void triggerClick(int item) {
        if (onItemClick != null) {
            onItemClick.onClick(item);
        }
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
        itemNetwork.setContent("有效提高20%网速");

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
        int temp=batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        int random=NumberUtils.mathRandomInt(1, 6);
        temp= temp / 10;
        temp = temp > 0 ? temp : 30;
        temp=temp+random;
        String tColor = temp+"C°";
        SpannableString text = AndroidUtil.inertColorText(tHead + tColor, tHead.length(), tHead.length() + tColor.length(), getRedColor());
        itemTemperature.setContent(text);
    }

    public void coolingUsedStyle() {
        String tHead = "已成功降温";
        String tColor = PreferenceUtil.getCleanCoolNum() + "°C";
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
        itemNotify.setContent("已发现骚扰通知");
    }

    public void notifyUsedStyle() {
        itemNotify.setContentColor(getNormalColor());
        itemNotify.setContent("开启防骚扰模式");
    }


    /**
     * ****************************************************************************************************************************
     * ******************************************************item view click listener**********************************************
     * ****************************************************************************************************************************
     */
    OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick(int item);
    }

    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
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
        return getContext().getResources().getColor(R.color.home_content);
    }
}

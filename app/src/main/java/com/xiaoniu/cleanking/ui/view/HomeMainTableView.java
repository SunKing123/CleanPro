package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by xinxiaolong on 2020/7/1.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeMainTableView extends ConstraintLayout {
    public static final int ITEM_ONE_KEY = 1;
    public static final int ITEM_KILL_VIRUS = 2;
    public static final int ITEM_ELECTRIC = 3;

    ViewGroup oneKey;
    ViewGroup killVirus;
    ViewGroup electric;

    TextView tvOneKey;
    TextView tvKillVirus;
    TextView tvElectric;

    public HomeMainTableView(Context context) {
        super(context);
    }

    public HomeMainTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeMainTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_home_main_table_layout, this);
        oneKey = findViewById(R.id.card_oneKey);
        killVirus = findViewById(R.id.card_virus);
        electric = findViewById(R.id.card_electric);

        tvOneKey = findViewById(R.id.tv_oneKeyContent);
        tvKillVirus = findViewById(R.id.tv_virusContent);
        tvElectric = findViewById(R.id.tv_electricContent);

        oneKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_ONE_KEY);
            }
        });

        killVirus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_KILL_VIRUS);
            }
        });

        electric.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerClick(ITEM_ELECTRIC);
            }
        });
    }

    public void initViewState() {
        if (PreferenceUtil.getCleanTime()) {
            oneKeySpeedUnusedStyle();
        } else {
            oneKeySpeedUsedStyle();
        }
        if (PreferenceUtil.getVirusKillTime()) {
            killVirusUnusedStyle();
        } else {
            killVirusUsedStyle();
        }
        if (PreferenceUtil.getPowerCleanTime()) {
            electricUnusedStyle();
        } else {
            electricUsedStyle();
        }
    }

    private void triggerClick(int item) {
        if (onItemClick != null) {
            onItemClick.onClick(item);
        }
    }

    /*
     ************************************************************************************************************************************************************************
     ********************************************************************oneKeySpeed style***********************************************************************************
     ************************************************************************************************************************************************************************
     */
    //一键加速未使用风格
    public void oneKeySpeedUnusedStyle() {
        String tColor = NumberUtils.mathRandom(70, 85) + "%";
        setOneKeyText(tColor, getRedColor());
    }

    //一键加速已使用风格
    public void oneKeySpeedUsedStyle() {
        String tColor = NumberUtils.mathRandom(15, 35) + "%";
        setOneKeyText(tColor, getGreenColor());
    }

    private void setOneKeyText(String tColor, int color) {
        String tHead = "内存占用";
        SpannableString text = AndroidUtil.inertColorText(tHead + tColor, tHead.length(), tHead.length() + tColor.length(), color);
        tvOneKey.setText(text);
    }

    /*
     ************************************************************************************************************************************************************************
     ********************************************************************killVirus style*************************************************************************************
     ************************************************************************************************************************************************************************
     */
    //病毒查杀未使用风格
    public void killVirusUnusedStyle() {
        int unusedDays = PreferenceUtil.getUnusedVirusKillDays();
        if (unusedDays >= 1) {
            String tColor = unusedDays + "天";
            SpannableString text = AndroidUtil.inertColorText(tColor + "未杀毒", 0, tColor.length(), getRedColor());
            tvKillVirus.setText(text);
        } else {
            String tColor = "可能有风险";
            SpannableString text = AndroidUtil.inertColorText(tColor, 0, tColor.length(), getRedColor());
            tvKillVirus.setText(text);
        }
    }

    //病毒查杀已使用风格
    public void killVirusUsedStyle() {
        String tColor = "防御保护已开启";
        SpannableString text = AndroidUtil.inertColorText(tColor, 0, tColor.length(), getGreenColor());
        tvKillVirus.setText(text);
    }

    /*
     ************************************************************************************************************************************************************************
     ********************************************************************electric style**************************************************************************************
     ************************************************************************************************************************************************************************
     */

    //unused electric style
    public void electricUnusedStyle() {
        String tColor = NumberUtils.mathRandom(5, 15) + "个";
        SpannableString text = AndroidUtil.inertColorText(tColor + "应用正在耗电", 0, tColor.length(), getRedColor());
        tvElectric.setText(text);
    }

    //used electric style
    public void electricUsedStyle() {
        String tColor = getRandomOptimizeElectricNum();
        String head = "延长时间";
        SpannableString text = AndroidUtil.inertColorText(head + tColor + "分钟", head.length(), head.length() + tColor.length(), getGreenColor());
        tvElectric.setText(text);
    }

    //get random optimize electric num by electric value
    private String getRandomOptimizeElectricNum() {
        int electric = AndroidUtil.getElectricityNum(getContext());
        String num = "";
        if (electric >= 70) {
            num = NumberUtils.mathRandom(30, 60);
        } else if (electric >= 50) {
            num = NumberUtils.mathRandom(20, 50);
        } else if (electric >= 20) {
            num = NumberUtils.mathRandom(10, 45);
        } else if (electric >= 10) {
            num = NumberUtils.mathRandom(10, 30);
        } else {
            num = NumberUtils.mathRandom(5, 15);
        }
        return num;
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

    @NonNull
    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    public int getRedColor() {
        return getContext().getResources().getColor(R.color.home_content_red);
    }

    public int getGreenColor() {
        return getContext().getResources().getColor(R.color.home_content_green);
    }
}

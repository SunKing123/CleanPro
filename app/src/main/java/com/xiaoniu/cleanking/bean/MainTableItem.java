package com.xiaoniu.cleanking.bean;

/**
 * Created by xinxiaolong on 2020/6/2.
 * email：xinxiaolong123@foxmail.com
 */
public class MainTableItem {

    public static final int TAG_ACC = 1;
    public static final int TAG_COOL = 2;
    public static final int TAG_CLEAN_WX = 3;
    public static final int TAG_CLEAN_NOTIFY = 4;
    public static final int TAG_KILL_VIRUS = 5;
    public static final int TAG_BATTER = 6;

    public int styleType;
    public int drawableResId;
    public String text;
    public int tag;

    public MainTableItem(int styleType, int tag) {
        this.styleType = styleType;
        this.tag = tag;
    }

    public MainTableItem(int styleType, int tag, int drawableResId, String text) {
        this.styleType = styleType;
        this.tag=tag;
        this.drawableResId = drawableResId;
        this.text = text;
    }
}

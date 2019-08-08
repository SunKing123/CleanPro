package com.xiaoniu.cleanking.ui.main.event;

import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;

import java.util.List;

/**
 * 文件扫描EventBus
 * Created by lang.chen on 2019/7/17
 */
public class WxQqCleanEvent {
    public static final int WX_CLEAN_FILE = 0;
    public static final int WX_CLEAN_AUDIO = 1;
    public long cleanSize;
    public int type;

    public WxQqCleanEvent(int type,long cleanSize) {
        this.type = type;
        this.cleanSize = cleanSize;
    }
}

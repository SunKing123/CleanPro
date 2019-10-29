package com.xiaoniu.cleanking.base;

import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;

/**
 * 保存埋点来源
 */
public class AppHolder {
    private static AppHolder appHolder;

    private AppHolder() {
    }

    private static class Holder {
        // 这里的私有没有什么意义
        static AppHolder appHolder = new AppHolder();
    }

    public static AppHolder getInstance() {
        // 外围类能直接访问内部类（不管是否是静态的）的私有变量
        return Holder.appHolder;
    }

    /**
     * 保存上级页面id
     */
    private String sourcePageId = "home_page";

    /**
     * 保存二级上级页面id
     */
    private String otherSourcePageId = "home_page";

    public String getSourcePageId() {
        return sourcePageId;
    }

    public void setSourcePageId(String sourcePageId) {
        this.sourcePageId = sourcePageId;
    }

    public void setOtherSourcePageId(String otherSourcePageId) {
        this.otherSourcePageId = otherSourcePageId;
    }

    public String getOtherSourcePageId() {
        return otherSourcePageId;
    }

    private SwitchInfoList switchInfoList;

    public void setSwitchInfoList(SwitchInfoList switchInfoList) {
        this.switchInfoList = switchInfoList;
    }

    public SwitchInfoList getSwitchInfoList() {
        return switchInfoList;
    }

    private String cleanFinishSourcePageId = "";
    private boolean isPush = false;

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        isPush = push;
    }

    public String getCleanFinishSourcePageId() {
        return cleanFinishSourcePageId;
    }

    public void setCleanFinishSourcePageId(String cleanFinishSourcePageId) {
        this.cleanFinishSourcePageId = cleanFinishSourcePageId;
    }
}

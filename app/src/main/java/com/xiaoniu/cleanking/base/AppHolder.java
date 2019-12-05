package com.xiaoniu.cleanking.base;

import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, InsertAdSwitchInfoList.DataBean> insertAdSwitchmap = new HashMap<>();

    public Map<String, InsertAdSwitchInfoList.DataBean> getInsertAdSwitchmap() {
        return insertAdSwitchmap;
    }

    public void setInsertAdSwitchInfoList(InsertAdSwitchInfoList insertAdSwitchInfoList) {
        //开关数据Map存储
        insertAdSwitchmap.clear();
        for (InsertAdSwitchInfoList.DataBean post : insertAdSwitchInfoList.getData()) {
            insertAdSwitchmap.put(post.getConfigKey(), post);
        }
    }


    public void setSwitchInfoList(SwitchInfoList switchInfoList) {
        this.switchInfoList = switchInfoList;
    }

    public SwitchInfoList getSwitchInfoList() {
        return switchInfoList;
    }

    private List<BottoomAdList.DataBean> mBottoomAdList;

    public void setBottomAdList(List<BottoomAdList.DataBean> switchInfoList) {
        this.mBottoomAdList = switchInfoList;
    }

    public List<BottoomAdList.DataBean> getBottomAdList() {
        return mBottoomAdList;
    }

    private RedPacketEntity mRedPacketEntity;

    public void setRedPacketEntityList(RedPacketEntity redPacketEntity) {
        this.mRedPacketEntity = redPacketEntity;
    }

    public RedPacketEntity getRedPacketEntityList() {
        return mRedPacketEntity;
    }

    private IconsEntity mIconsEntity;

    public void setIconsEntityList(IconsEntity iconsEntity) {
        this.mIconsEntity = iconsEntity;
    }

    public IconsEntity getIconsEntityList() {
        return mIconsEntity;
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

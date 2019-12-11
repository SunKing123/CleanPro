package com.xiaoniu.cleanking.base;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存埋点来源
 */
public class AppHolder {

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

    //总广告
    private SwitchInfoList switchInfoList;

    //插屏广告
    private Map<String, InsertAdSwitchInfoList.DataBean> insertAdSwitchmap = new HashMap<>();

    //兜底广告
    private List<BottoomAdList.DataBean> mBottoomAdList;

    //红包弹窗
    private RedPacketEntity mRedPacketEntity;

    //底部Icon
    private IconsEntity mIconsEntity;

    //完成页sourcePageId暂存
    private String cleanFinishSourcePageId = "";



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



    public Map<String, InsertAdSwitchInfoList.DataBean> getInsertAdSwitchmap() {
        return insertAdSwitchmap;
    }

    public void setInsertAdSwitchInfoList(InsertAdSwitchInfoList insertAdSwitchInfoList) {
        //开关数据Map存储
        insertAdSwitchmap.clear();
        for (InsertAdSwitchInfoList.DataBean post : insertAdSwitchInfoList.getData()) {
            insertAdSwitchmap.put(post.getConfigKey(), post);
        }
        MmkvUtil.setSwitchInfo(new Gson().toJson(insertAdSwitchInfoList));
    }


    public void setSwitchInfoList(SwitchInfoList switchInfoList) {
        this.switchInfoList = switchInfoList;
    }

    public SwitchInfoList getSwitchInfoList() {
        return switchInfoList;
    }



    public void setBottomAdList(List<BottoomAdList.DataBean> switchInfoList) {
        this.mBottoomAdList = switchInfoList;
    }

    public List<BottoomAdList.DataBean> getBottomAdList() {
        return mBottoomAdList;
    }



    public void setRedPacketEntityList(RedPacketEntity redPacketEntity) {
        this.mRedPacketEntity = redPacketEntity;
    }

    public RedPacketEntity getRedPacketEntityList() {
        return mRedPacketEntity;
    }



    public void setIconsEntityList(IconsEntity iconsEntity) {
        this.mIconsEntity = iconsEntity;
    }

    public IconsEntity getIconsEntityList() {
        return mIconsEntity;
    }


    public String getCleanFinishSourcePageId() {
        return cleanFinishSourcePageId;
    }

    public void setCleanFinishSourcePageId(String cleanFinishSourcePageId) {
        this.cleanFinishSourcePageId = cleanFinishSourcePageId;
    }

    /**
     *  总开关检查
     * @param configKey
     * @param advertPosition
     * @return
     */
    public boolean checkAdSwitch(String configKey,String advertPosition){
        boolean isOpen = false;
        if (null != getSwitchInfoList() && null != getSwitchInfoList().getData() && getSwitchInfoList().getData().size() > 0 && !TextUtils.isEmpty(configKey) && !TextUtils.isEmpty(advertPosition)) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (configKey.equals(switchInfoList.getConfigKey()) && advertPosition.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
       return isOpen;
    }


    /**
     *  总开关检查
     * @param configKey
     * @return
     */
    public boolean checkAdSwitch(String configKey){
        boolean isOpen = false;
        if (null != getSwitchInfoList() && null != getSwitchInfoList().getData() && getSwitchInfoList().getData().size() > 0 && !TextUtils.isEmpty(configKey)) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (configKey.equals(switchInfoList.getConfigKey())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        return isOpen;
    }

    /**
     *  插屏开关Data
     * @param configKey
     * @return
     */
    public InsertAdSwitchInfoList.DataBean getInsertAdInfo(String configKey) {
        if (null != getInsertAdSwitchmap()) {
            Map<String, InsertAdSwitchInfoList.DataBean> map = getInsertAdSwitchmap();
            if (null != map.get(configKey)) {
                InsertAdSwitchInfoList.DataBean dataBean = map.get(configKey);
                return dataBean;
            }
        }
        return null;
    }


    /**
     *  插屏开关Data
     * @param configKey
     * @return
     */
    public InsertAdSwitchInfoList.DataBean getInsertAdInfo(String configKey,String insertData) {
        if (!TextUtils.isEmpty(insertData)) {
            InsertAdSwitchInfoList dataBean = new Gson().fromJson(insertData,InsertAdSwitchInfoList.class);
            if(null!=dataBean && dataBean.getData()!=null && dataBean.getData().size()>0){
                List<InsertAdSwitchInfoList.DataBean> dataBeans = dataBean.getData();
                for(int i=0;i<dataBeans.size();i++){
                    InsertAdSwitchInfoList.DataBean posData = dataBeans.get(i);
                    if (null != posData && posData.getConfigKey().equals(configKey)) {
                        return posData;
                    }
                }
            }
        }
        return null;
    }



}

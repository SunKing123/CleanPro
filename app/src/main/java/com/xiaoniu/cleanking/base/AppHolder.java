package com.xiaoniu.cleanking.base;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存埋点来源
 */
public class AppHolder {
    // private static AppHolder appHolder;

    //广告配置内存缓存
    //  public static Map<String, SwitchInfoList.DataBean> mAdsMap = CollectionUtils.createMap();

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
    private Map<String, InsertAdSwitchInfoList.DataBean> insertAdSwitchMap = new HashMap<>();

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


    public Map<String, InsertAdSwitchInfoList.DataBean> getInsertAdSwitchMap() {
        if (insertAdSwitchMap.size() <= 0 && !TextUtils.isEmpty(MmkvUtil.getInsertSwitchInfo())) {
            InsertAdSwitchInfoList dataBean = new Gson().fromJson(MmkvUtil.getInsertSwitchInfo(), InsertAdSwitchInfoList.class);
            if (null != dataBean && dataBean.getData() != null && dataBean.getData().size() > 0) {
                List<InsertAdSwitchInfoList.DataBean> dataBeans = dataBean.getData();
                for (InsertAdSwitchInfoList.DataBean post : dataBeans) {
                    insertAdSwitchMap.put(post.getConfigKey(), post);
                }
            }
        }
        return insertAdSwitchMap;
    }

    public void setInsertAdSwitchInfoList(InsertAdSwitchInfoList insertAdSwitchInfoList) {
        //开关数据Map存储
        insertAdSwitchMap.clear();
        for (InsertAdSwitchInfoList.DataBean post : insertAdSwitchInfoList.getData()) {
            insertAdSwitchMap.put(post.getConfigKey(), post);
        }
        MmkvUtil.setInsertSwitchInfo(new Gson().toJson(insertAdSwitchInfoList));
    }


    public void setSwitchInfoList(SwitchInfoList switchInfoList) {
        this.switchInfoList = switchInfoList;
        //本地数据保存
        MmkvUtil.setSwitchInfo(new Gson().toJson(switchInfoList));
    }

    public SwitchInfoList getSwitchInfoList() {
        return switchInfoList != null ? switchInfoList : new Gson().fromJson(MmkvUtil.getSwitchInfo(), SwitchInfoList.class);
    }


    public void setBottomAdList(List<BottoomAdList.DataBean> switchInfoList) {
        this.mBottoomAdList = switchInfoList;
        MmkvUtil.setBottoomAdInfo(new Gson().toJson(mBottoomAdList));
    }

    public List<BottoomAdList.DataBean> getBottomAdList() {
        return mBottoomAdList != null ? mBottoomAdList : new Gson().fromJson(MmkvUtil.getBottoomAdInfo(), new TypeToken<List<BottoomAdList.DataBean>>() {
        }.getType());
    }


    public void setPopupDataEntity(RedPacketEntity redPacketEntity) {
        this.mRedPacketEntity = redPacketEntity;
    }

    public RedPacketEntity getPopupDataEntity() {
        return mRedPacketEntity;
    }


    public RedPacketEntity.DataBean getPopupDataFromListByType(RedPacketEntity redPacketEntity, @PopupWindowType String type) {
        RedPacketEntity.DataBean dataBean = null;
        if (redPacketEntity != null && redPacketEntity.getData() != null) {
            for (RedPacketEntity.DataBean item : redPacketEntity.getData()) {
                if (!TextUtils.isEmpty(item.getPopUpType()) && type.equals(item.getPopUpType())) {
                    dataBean = item;
                    break;
                }
            }
        }
        return dataBean;
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
     * 总开关检查
     *
     * @param configKey
     * @param advertPosition
     * @return
     */
    public boolean checkAdSwitch(String configKey, String advertPosition) {
        boolean isOpen = false;
        //过审开关是否打开
        String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1") && null != getSwitchInfoList() && null != getSwitchInfoList().getData() && getSwitchInfoList().getData().size() > 0 && !TextUtils.isEmpty(configKey) && !TextUtils.isEmpty(advertPosition)) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (configKey.equals(switchInfoList.getConfigKey()) && advertPosition.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                    break;
                }
            }
        }
        return isOpen;
    }


    /**
     * 总开关检查
     *
     * @param configKey
     * @return
     */
    public boolean checkAdSwitch(String configKey) {
        boolean isOpen = false;
        //过审开关是否打开
        String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1") &&null != getSwitchInfoList() && null != getSwitchInfoList().getData() && getSwitchInfoList().getData().size() > 0 && !TextUtils.isEmpty(configKey)) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (configKey.equals(switchInfoList.getConfigKey())) {
                    isOpen = switchInfoList.isOpen();
                    return isOpen;
                }
            }
        }
        return isOpen;
    }

    /**
     * 插屏开关Data
     *
     * @param configKey
     * @return
     */
    public InsertAdSwitchInfoList.DataBean getInsertAdInfo(String configKey) {
        String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1") && null != getInsertAdSwitchMap()) {
            Map<String, InsertAdSwitchInfoList.DataBean> map = getInsertAdSwitchMap();
            if (null != map.get(configKey)) {
                return map.get(configKey);
            }
        }
        return null;
    }


    /**
     * 插屏开关Data
     *
     * @param configKey
     * @return
     */
    public InsertAdSwitchInfoList.DataBean getInsertAdInfo(String configKey, String insertData) {
        if (!TextUtils.isEmpty(insertData)) {
            InsertAdSwitchInfoList dataBean = new Gson().fromJson(insertData, InsertAdSwitchInfoList.class);
            if (null != dataBean && dataBean.getData() != null && dataBean.getData().size() > 0) {
                List<InsertAdSwitchInfoList.DataBean> dataBeans = dataBean.getData();
                for (int i = 0; i < dataBeans.size(); i++) {
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

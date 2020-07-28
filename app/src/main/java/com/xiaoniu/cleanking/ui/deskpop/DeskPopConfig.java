package com.xiaoniu.cleanking.ui.deskpop;

import com.google.gson.internal.$Gson$Preconditions;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.ExternalPopNumEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;

/**
 * Created by xinxiaolong on 2020/7/27.
 * email：xinxiaolong123@foxmail.com
 */
public class DeskPopConfig {

    private static DeskPopConfig config;

    private InsertAdSwitchInfoList.DataBean stateConfig;
    private InsertAdSwitchInfoList.DataBean batteryConfig;


    public static DeskPopConfig getInstance() {
        if (config == null) {
            config = new DeskPopConfig();
        }
        return config;
    }

    private DeskPopConfig() {
        initData();
    }

    private void initData() {
        initStateConfig();
        initBatteryConfig();
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************state config*********************************************************************************
     *********************************************************************************************************************************************************
     */

    /**
     * 开关是否打开
     *
     * @return
     */
    public boolean isStateOpen() {
        return stateConfig.isOpen();
    }

    public int getStateDisplayTime(){
       return stateConfig.getDisplayTime();
    }
    /**
     * 手机状态是否可以弹出
     * @return
     */
    public boolean isStateCanPop() {
        if(isStateOpen()){
            ExternalPopNumEntity externalPopNumEntity= getLastSameDayExternalPop();
            return externalPopNumEntity.getPopupCount()>0;
        }
        return false;
    }

    /**
     * 获取服务端配置，每日可弹次数。
     * @return
     */
    public int getStatePopCount(){
        return stateConfig.getShowRate();
    }

    /**
     * 存储递减弹框次数
     */
    public void saveAndDecreaseStatePopNum() {
        LogUtils.e("=======================pulseTimer   in saveAndDecreaseStatePopNum()========================");
        ExternalPopNumEntity externalPopNumEntity= getLastSameDayExternalPop();
        externalPopNumEntity.setPopupTime(System.currentTimeMillis());
        externalPopNumEntity.setPopupCount(externalPopNumEntity.getPopupCount()-1);
        LogUtils.e("pulseTimer:  thisPopNum: "+externalPopNumEntity.getPopupCount()+"    "+externalPopNumEntity.getPopupTime());
        PreferenceUtil.saveStateExternalPopNumEntity(externalPopNumEntity);
        LogUtils.e("=======================pulseTimer   in saveAndDecreaseStatePopNum()========================");
    }

    /**
     * 获取相同天内的弹框次数信息
     * @return
     */
    private ExternalPopNumEntity getLastSameDayExternalPop(){
        ExternalPopNumEntity externalPopNumEntity= PreferenceUtil.getStateExternalPopNumEntity();
        if(externalPopNumEntity==null||!DateUtils.isSameDay(externalPopNumEntity.getPopupTime(), System.currentTimeMillis())){
            //如果已隔天，则重新开始计算弹出次数
            externalPopNumEntity=new ExternalPopNumEntity(System.currentTimeMillis(),getStatePopCount());
        }
        LogUtils.e("pulseTimer:  lastPopNum: "+externalPopNumEntity.getPopupCount()+"    "+externalPopNumEntity.getPopupTime());
        return externalPopNumEntity;
    }

    private InsertAdSwitchInfoList.DataBean initStateConfig() {
        stateConfig = AppHolder.getInstance().getInsertAdInfo("page_desk_device_info");
        if (stateConfig == null) {
            stateConfig = new InsertAdSwitchInfoList.DataBean();
            stateConfig.setOpen(false);
        }
        return stateConfig;
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************battery config*******************************************************************************
     *********************************************************************************************************************************************************
     */

    public boolean isBatteryOpen() {
        return batteryConfig.isOpen();
    }

    private InsertAdSwitchInfoList.DataBean initBatteryConfig() {
        batteryConfig = AppHolder.getInstance().getInsertAdInfo("page_desk_battery_info");
        if (batteryConfig == null) {
            batteryConfig = new InsertAdSwitchInfoList.DataBean();
            batteryConfig.setOpen(false);
        }
        return batteryConfig;
    }

    /**
     * 电量状态是否可以弹出
     * @return
     */
    public boolean isBatteryCanPop() {
        if(isBatteryOpen()){
            ExternalPopNumEntity externalPopNumEntity= getLastSameDayExternalBatteryPop();
            return externalPopNumEntity.getPopupCount()>0;
        }
        return false;
    }

    /**
     * 获取服务端配置，每日可弹次数。
     * @return
     */
    public int getBatteryPopCount(){
        return batteryConfig.getShowRate();
    }

    /**
     * 存储递减弹框次数
     */
    public void saveAndDecreaseBatteryPopNum() {
        ExternalPopNumEntity externalPopNumEntity= getLastSameDayExternalBatteryPop();
        externalPopNumEntity.setPopupTime(System.currentTimeMillis());
        externalPopNumEntity.setPopupCount(externalPopNumEntity.getPopupCount()-1);
        PreferenceUtil.saveBatteryExternalPopNumEntity(externalPopNumEntity);
    }

    /**
     * 获取相同天内的弹框次数信息
     * @return
     */
    private ExternalPopNumEntity getLastSameDayExternalBatteryPop(){
        ExternalPopNumEntity externalPopNumEntity= PreferenceUtil.getBatteryExternalPopNumEntity();
        if(externalPopNumEntity==null||!DateUtils.isSameDay(externalPopNumEntity.getPopupTime(), System.currentTimeMillis())){
            //如果已隔天，则重新开始计算弹出次数
            externalPopNumEntity=new ExternalPopNumEntity(System.currentTimeMillis(),getBatteryPopCount());
        }
        return externalPopNumEntity;
    }
}

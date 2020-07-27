package com.xiaoniu.cleanking.ui.deskpop;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;

/**
 * Created by xinxiaolong on 2020/7/27.
 * email：xinxiaolong123@foxmail.com
 */
public class DeskPopConfig {

    public static boolean isStateOpen(){
        InsertAdSwitchInfoList.DataBean dataBean=getStateConfig();
        return dataBean.isOpen();
    }

    public static boolean isBatteryOpen(){
        InsertAdSwitchInfoList.DataBean dataBean= getBatteryConfig();
        return dataBean.isOpen();
    }

    public static InsertAdSwitchInfoList.DataBean getStateConfig(){
        InsertAdSwitchInfoList.DataBean dataBean= AppHolder.getInstance().getInsertAdInfo("page_desk_device_info");
        if(dataBean==null){
            dataBean=new InsertAdSwitchInfoList.DataBean();
            dataBean.setOpen(false);
        }
        return dataBean;
    }

    public static InsertAdSwitchInfoList.DataBean getBatteryConfig(){
        InsertAdSwitchInfoList.DataBean dataBean= AppHolder.getInstance().getInsertAdInfo("page_desk_battery_info");
        if(dataBean==null){
            dataBean=new InsertAdSwitchInfoList.DataBean();
            dataBean.setOpen(false);
        }
        return dataBean;
    }
}

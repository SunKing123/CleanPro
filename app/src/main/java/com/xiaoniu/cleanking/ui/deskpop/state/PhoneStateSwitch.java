package com.xiaoniu.cleanking.ui.deskpop.state;

import com.xiaoniu.cleanking.base.AppHolder;

/**
 * Created by xinxiaolong on 2020/7/27.
 * email：xinxiaolong123@foxmail.com
 */
public class PhoneStateSwitch {

    public static boolean isOpen(){
        return AppHolder.getInstance().checkSwitchIsOpen("page_desk_device_info");
    }
}

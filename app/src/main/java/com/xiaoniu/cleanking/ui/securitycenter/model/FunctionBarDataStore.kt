package com.xiaoniu.cleanking.ui.securitycenter.model

import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/9/4.
 * email：xinxiaolong123@foxmail.com
 */
class FunctionBarDataStore {

    var modelList = arrayListOf(FunctionBarModel(R.drawable.icon_security_camera, "摄像头检测", "快速识别隐藏非法摄像头", "vip看视频解锁"),
            FunctionBarModel(R.drawable.icon_security_battery, "电池体检", "电池状态分析，提高续航时长", "独家"),
            FunctionBarModel(R.drawable.icon_security_red_packet_video, "视频红包", "您有一个红包待领取", ""))

    fun getCameraModel(): FunctionBarModel {
        return modelList[0]
    }

    fun getBatteryModel(): FunctionBarModel {
        return modelList[1]
    }

    fun getRedPacketModel(): FunctionBarModel {
        return modelList[2]
    }
}

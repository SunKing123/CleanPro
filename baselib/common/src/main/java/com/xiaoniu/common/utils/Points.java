package com.xiaoniu.common.utils;

/**
 * Created by xinxiaolong on 2020/6/15.
 * email：xinxiaolong123@foxmail.com
 */
public interface Points {

    //读写sd卡权限检测
    String EVENT_CODE_STORAGE_PERMISSION = "storage_permission_detection";
    String EVENT_NAME_STORAGE_PERMISSION = "存储权限检测";

    //读取手机状态权限检测
    String EVENT_CODE_DEVICE_IDENTIFICATION = "device_identification_authority_detection";
    String EVENT_NAME_DEVICE_IDENTIFICATION = "设备识别检测";

    //系统返回
    String EVENT_CODE_SYSTEM_RETURN_CLICK = "system_return_click";

    interface Virus {
        //病毒查杀扫描
        String PAGE_SCAN = "virus_killing_scan_page";
        String EVENT_CODE_SCAN_PAGE = "virus_killing_scan_page_view_page";
        String EVENT_NAME_SCAN_PAGE = "病毒查杀扫描页浏览";
        String EVENT_NAME_SCAN_SYSTEM_RETURN = "用户在病毒查杀页返回";

        //病毒查杀动画页
        String PAGE_ANIMATION = "virus_killing_animation_page";
        String EVENT_CODE_ANIMATION_PAGE = "virus_killing_animation_page_view_page";
        String EVENT_NAME_ANIMATION_PAGE = "病毒查杀动画页浏览";
        String EVENT_NAME_ANIMATION_SYSTEM_RETURN = "病毒查杀动画页返回";

        //病毒查杀完成页
        String PAGE_ANIMATION_FINISH = "virus_killing_finish_animation_page";
        String EVENT_CODE_ANIMATION_FINISH_PAGE = "virus_killing_finish_animation_page_view_page";
        String EVENT_NAME_ANIMATION_FINISH_PAGE = "病毒查杀动画完成页浏览";
        String EVENT_NAME_ANIMATION_FINISH_SYSTEM_RETURN = "病毒查杀动画完成页返回";

    }


}

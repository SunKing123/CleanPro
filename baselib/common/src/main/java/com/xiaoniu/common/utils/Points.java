package com.xiaoniu.common.utils;

/**
 * Created by xinxiaolong on 2020/6/15.
 * email：xinxiaolong123@foxmail.com
 */
public interface Points {

    //读写sd卡权限检测
    String STORAGE_PERMISSION_EVENT_CODE = "storage_permission_detection";
    String STORAGE_PERMISSION_EVENT_NAME = "存储权限检测";

    //读取手机状态权限检测
    String DEVICE_IDENTIFICATION_EVENT_CODE = "device_identification_authority_detection";
    String DEVICE_IDENTIFICATION_EVENT_NAME = "设备识别检测";

    //系统返回
    String SYSTEM_RETURN_CLICK_EVENT_CODE = "system_return_click";

    interface Virus {
        //病毒查杀扫描
        String SCAN_PAGE = "virus_killing_scan_page";
        String SCAN_PAGE_EVENT_CODE = "virus_killing_scan_page_view_page";
        String SCAN_PAGE_EVENT_NAME = "病毒查杀扫描页浏览";
        String SCAN_SYSTEM_RETURN_EVENT_NAME = "用户在病毒查杀页返回";

        //病毒查杀动画页
        String ANIMATION_PAGE = "virus_killing_animation_page";
        String ANIMATION_PAGE_EVENT_CODE = "virus_killing_animation_page_view_page";
        String ANIMATION_PAGE_EVENT_NAME = "病毒查杀动画页浏览";
        String ANIMATION_SYSTEM_RETURN_EVENT_NAME = "病毒查杀动画页返回";

        //病毒查杀完成页
        String ANIMATION_FINISH_PAGE = "virus_killing_finish_animation_page";
        String ANIMATION_FINISH_PAGE_EVENT_CODE = "virus_killing_finish_animation_page_view_page";
        String ANIMATION_FINISH_PAGE_EVENT_NAME = "病毒查杀动画完成页浏览";
        String ANIMATION_FINISH_SYSTEM_RETURN_EVENT_NAME = "病毒查杀动画完成页返回";

    }


}

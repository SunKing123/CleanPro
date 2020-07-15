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

    interface ScratchCard {
        String WINDOW_PAGE = "scratch_card_gold_coin_pop_up_window_page";

        String WINDOW_UP_EVENT_CODE = "scratch_card_gold_coin_pop_up_window_custom";
        String WINDOW_UP_EVENT_NAME = "刮刮卡金币领取弹窗曝光";
        String WINDOW_DOUBLE_CLICK_EVENT_CODE = "double_the_gold_coin_click";
        String WINDOW_DOUBLE_CLICK_EVENT_NAME = "金币翻倍按钮点击";
        String WINDOW_CLOSE_CLICK_CODE = "close_click";
        String WINDOW_CLOSE_CLICK_NAME = "弹窗关闭点击";
        String WINDOW_GOLD_NUM_NAME = "刮刮卡金币弹框金币发放数";

        String VIDEO_PAGE = "scratch_card_incentive_video_page";
        String VIDEO_PAGE_CLOSE_CLICK_CODE = "close_click";
        String VIDEO_PAGE_CLOSE_CLICK_NAME = "刮刮卡激励视频广告关闭点击";

        String SUCCESS_PAGE = "scratch_card_success_page";
        String SUCCESS_EXPOSURE_CODE = "scratch_card_success_page_custom";
        String SUCCESS_EXPOSURE_NAME = "刮刮卡翻倍金币领取完成页曝光";
        String SUCCESS_RETURN_CLICK_NAME = "刮刮卡翻倍金币领取完成页返回";
        String SUCCESS_AD_REQUEST_SDK_NAME = "刮刮卡翻倍金币领取完成页广告发起请求";
        String SUCCESS_NUMBER_OF_GOLD_NAME = "刮刮卡翻倍金币领取完成页金币发放数";


    }

    interface FunctionGoldCoin {
        String SUCCESS_PAGE = "success_page_gold_coin_pop_up_window_success_page";

        String SUCCESS_EXPOSURE_CODE = "success_page_gold_coin_pop_up_window_success_page_custom";
        String SUCCESS_EXPOSURE_NAME = "功能完成页金币翻倍领取完成页曝光";

        String SUCCESS_RETURN_CLICK_NAME = "功能完成页金币翻倍领取完成页返回";

        String SUCCESS_AD_REQUEST_SDK_NAME = "功能完成页翻倍金币领取完成页广告发起请求";
        String SUCCESS_NUMBER_OF_GOLD_NAME = "功能完成页翻倍金币领取完成页金币发放数";
    }

    interface MainGoldCoin {
        String SUCCESS_PAGE = "home_page_gold_coin_pop_up_window_success_page";

        String SUCCESS_EXPOSURE_CODE = "home_page_gold_coin_pop_up_window_success_page_custom";
        String SUCCESS_EXPOSURE_NAME = "首页金币翻倍领取完成页曝光";

        String SUCCESS_RETURN_CLICK_NAME = "首页金币翻倍领取完成页返回";

        String SUCCESS_AD_REQUEST_SDK_NAME = "首页翻倍金币领取完成页广告发起请求";

        String SUCCESS_NUMBER_OF_GOLD_NAME = "首页翻倍金币领取完成页金币发放数";
    }


    interface MainHome {
        String BOOST_CLICK_CODE = "boost_click";
        String BOOST_CLICK_NAME = "用户在首页点击【一键加速】按钮";

        String VIRUS_KILLING_CLICK_CODE = "virus_killing_click";
        String VIRUS_KILLING_CLICK_NAME = "用户在首页点击【病毒查杀】按钮";

        String POWERSAVE_CLICK_CODE = "powersave_click";
        String POWERSAVE_CLICK_NAME = "用户在首页点击【超强省电】按钮";

        String WXCLEAN_CLICK_CODE = "wxclean_click";
        String WXCLEAN_CLICK_NAME = "用户在首页点击【微信专清】按钮";

        String COOLING_CLICK_CODE = "cooling_click";
        String COOLING_CLICK_NAME = "用户在首页点击【手机降温】按钮";

        String NETWORK_ACCELERATION_CLICK_CODE = "network_acceleration_click";
        String NETWORK_ACCELERATION_CLICK_NAME = "用户在首页点击【网络加速】按钮";

        String NOTIFICATION_CLEAN_CLICK_CODE = "notification_clean_click";
        String NOTIFICATION_CLEAN_CLICK_NAME = "用户在首页点击【通知清理】按钮";

        String DEEP_CLEANING_CLICK_CODE = "deep_cleaning_click";
        String DEEP_CLEANING_CLICK_NAME = "用户在首页点击【深度清理】按钮";

        String SCRAPING_BUOY_CLICK_CODE="scraping_buoy_click";
        String SCRAPING_BUOY_CLICK_NAME="首页刮刮卡浮标点击";
    }


    interface Tab{
        String CLEAN_CLICK_CODE="tab_clean_click";
        String CLEAN_CLICK_NAME="底部tab栏清理点击";

        String TOOLBOX_CLICK_CODE="tab_toolbox_click";
        String TOOLBOX_CLICK_NAME="底部tab栏工具箱点击";

        String SCRAPING_CARD_CLICK_CODE="tab_scraping_card_click";
        String SCRAPING_CARD_CLICK_NAME="底部tab栏刮刮卡点击";

        String MY_CLICK_CODE="tab_my_click";
        String MY_CLICK_NAME="底部tab栏我的点击";
    }

}

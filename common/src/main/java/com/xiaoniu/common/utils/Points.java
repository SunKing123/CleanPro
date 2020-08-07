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
    String RETURN_CLICK_EVENT_CODE = "return_click";


    interface Virus {
        //病毒查杀扫描
        String SCAN_PAGE = "virus_killing_scan_page";
        String SCAN_PAGE_EVENT_CODE = "virus_killing_scan_page_view_page";
        String SCAN_PAGE_EVENT_NAME = "病毒查杀扫描页浏览";
        String SCAN_SYSTEM_RETURN_EVENT_NAME = "用户在病毒查杀页返回";

        //病毒扫描结果页面
        String RESULT_PAGE = "virus_killing_scan_result_page";
        String RESULT_PAGE_EVENT_CODE = "virus_killing_scan_result_page_view_page";
        String RESULT_PAGE_EVENT_NAME = "用户在病毒查杀诊断页浏览";
        String RESULT_TO_CLEAN_EVENT_CODE = "virus_killing_scan_result_page_click";
        String RESULT_TO_CLEAN_EVENT_NAME = "用户在病毒查杀诊断页点击【一键优化】按钮";

        String RESULT_RETURN_EVENT_NAME = "用户在病毒查杀诊断页返回";


        //病毒查杀完成页
        String CLEAN_FINISH_PAGE = "virus_killing_animation_page";
        String CLEAN_FINISH_PAGE_EVENT_CODE = "virus_killing_animation_page_view_page";
        String CLEAN_FINISH_PAGE_EVENT_NAME = "病毒查杀动画页浏览";
        String CLEAN_FINISH_SYSTEM_RETURN_EVENT_NAME = "病毒查杀动画完成页返回";
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

        String POWER_SAVE_CLICK_CODE = "powersave_click";
        String POWER_SAVE_CLICK_NAME = "用户在首页点击【超强省电】按钮";

        String WX_CLEAN_CLICK_CODE = "wxclean_click";
        String WX_CLEAN_CLICK_NAME = "用户在首页点击【微信专清】按钮";

        String COOLING_CLICK_CODE = "cooling_click";
        String COOLING_CLICK_NAME = "用户在首页点击【手机降温】按钮";

        String NETWORK_ACCELERATION_CLICK_CODE = "network_acceleration_click";
        String NETWORK_ACCELERATION_CLICK_NAME = "用户在首页点击【网络加速】按钮";

        String NOTIFICATION_CLEAN_CLICK_CODE = "notification_clean_click";
        String NOTIFICATION_CLEAN_CLICK_NAME = "用户在首页点击【通知清理】按钮";

        String DEEP_CLEANING_CLICK_CODE = "deep_cleaning_click";
        String DEEP_CLEANING_CLICK_NAME = "用户在首页点击【深度清理】按钮";

        String SCRAPING_BUOY_CLICK_CODE = "scraping_buoy_click";
        String SCRAPING_BUOY_CLICK_NAME = "首页刮刮卡浮标点击";
    }

    interface Tab {
        String CLEAN_CLICK_CODE = "tab_clean_click";
        String CLEAN_CLICK_NAME = "底部tab栏清理点击";

        String TOOLBOX_CLICK_CODE = "tab_toolbox_click";
        String TOOLBOX_CLICK_NAME = "底部tab栏工具箱点击";

        String SCRAPING_CARD_CLICK_CODE = "tab_scraping_card_click";
        String SCRAPING_CARD_CLICK_NAME = "底部tab栏刮刮卡点击";

        String MINE_CLICK_CODE = "tab_my_click";
        String MINE_CLICK_NAME = "底部tab栏我的点击";
    }

    interface ExternalDevice {

        String PAGE = "mobile_phone_status_insertion_screen";

        String PAGE_EVENT_CODE = "mobile_phone_status_insertion_screen_custom";
        String PAGE_EVENT_NAME = "手机状态插屏曝光";

        String MEET_CONDITION_CODE = "mobile_phone_status_insertion_screen_meets_opportunity";
        String MEET_CONDITION_NAME = "手机状态插屏满足时机";

        String CLICK_MEMORY_BTN_CODE = "running_memory_button_click";
        String CLICK_MEMORY_BTN_NAME = "运行内存按钮点击";

        String CLICK_STORAGE_BTN_CODE = "internal_storage_button_click";
        String CLICK_STORAGE_BTN_NAME = "内存储存按钮点击";

        String CLICK_BATTERY_TEMPERATURE_BTN_CODE = "battery_temperature_button_click";
        String CLICK_BATTERY_TEMPERATURE_BTN_NAME = "电池温度按钮点击";

        String CLICK_BATTERY_QUANTITY_BTN_CODE = "electricity_quantity_button_click";
        String CLICK_BATTERY_QUANTITY_BTN_NAME = "电量按钮点击";

        String CLICK_CLOSE_CODE = "close_click";
        String CLICK_CLOSE_NAME = "手机状态插屏关闭按钮点击";
    }

    /**
     * 功能完成页埋点
     */
    interface CleanFinish {

        String RECOMMEND_CLICK_NAME = "recommendation_function_click";
        String RECOMMEND_CLICK_CODE = "推荐功能点击";

        interface GoldCoin{
            String PAGE = "success_page_gold_coin_pop_up_window";
            String PAGE_EVENT_CODE = "success_page_gold_coin_pop_up_window_custom";
            String PAGE_EVENT_NAME = "功能完成页金币领取弹窗曝光";

            String DOUBLE_CLICK_EVENT_CODE="double_the_gold_coin_click";
            String DOUBLE_CLICK_EVENT_NAME="金币翻倍按钮点击";

            String CLOSE_CLICK_EVENT_CODE="close_click";
            String CLOSE_CLICK_EVENT_NAME="弹窗关闭点击";

            String REQUEST_ADV1_EVENT_CODE="ad_request_sdk_1";
            String REQUEST_ADV1_EVENT_NAME="功能完成页金币领取弹窗上广告发起请求";

            String REQUEST_ADV2_EVENT_CODE="ad_request_sdk_2";
            String REQUEST_ADV2_EVENT_NAME="功能完成页翻倍激励视频广告发起请求";

            String NUMBER_OF_GOLD_COINS_EVENT_CODE="number_of_gold_coins_issued";
            String NUMBER_OF_GOLD_COINS_EVENT_NAME="功能完成页领取弹窗金币发放数";
        }

        interface Insert{
            String PAGE = "success_page";

            String REQUEST_ADV4_EVENT_CODE = "ad_request_sdk_4";
            String REQUEST_ADV4_EVENT_NAME = "功能完成页广告位4发起请求";

            String REQUEST_ADV5_EVENT_CODE = "ad_request_sdk_5";
            String REQUEST_ADV5_EVENT_NAME = "功能完成页广告位5发起请求";
        }

        interface Point {
            String getPage();
            String getPageEventCode();
            String getPageEventName();
            String getReturnClickName();
        }

        class Clean implements Point{
            String PAGE = "clean_success_page";
            String PAGE_EVENT_CODE = "clean_success_page_custom";
            String PAGE_EVENT_NAME = "垃圾清理完成页曝光时";
            String RETURN_CLICK_NAME = "用户在垃圾清理完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class Acc implements Point{
            String PAGE = "boost_success_page";
            String PAGE_EVENT_CODE = "boost_success_page_custom";
            String PAGE_EVENT_NAME = "加速完成页曝光时";
            String RETURN_CLICK_NAME = "用户在加速完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class Virus implements Point{
            String PAGE = "virus_killing_success_page";
            String PAGE_EVENT_CODE = "virus_killing_success_page_custom";
            String PAGE_EVENT_NAME = "病毒查杀完成页曝光时";
            String RETURN_CLICK_NAME = "用户在病毒查杀完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class Power implements Point{
            String PAGE = "powersave_success_page";
            String PAGE_EVENT_CODE = "powersave_success_page_custom";
            String PAGE_EVENT_NAME = "省电完成页曝光时";
            String RETURN_CLICK_NAME = "用户在省电完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class WxClean implements Point{
            String PAGE = "wxclean_success_page";
            String PAGE_EVENT_CODE = "wxclean_success_page_custom";
            String PAGE_EVENT_NAME = "微信清理完成页曝光时";
            String RETURN_CLICK_NAME = "用户在微信清理完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class Cool implements Point{
            String PAGE = "cool_success_page";
            String PAGE_EVENT_CODE = "cool_success_page_custom";
            String PAGE_EVENT_NAME = "手机降温完成页曝光时";
            String RETURN_CLICK_NAME = "用户在手机降温完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }

        }

        class Notify implements Point{
            String PAGE = "notification_clean_success_page";
            String PAGE_EVENT_CODE = "notification_clean_success_page_custom";
            String PAGE_EVENT_NAME = "通知清理完成页曝光时";
            String RETURN_CLICK_NAME = "用户在通知清理完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class NetWork implements Point{
            String PAGE = "network_acceleration_success_page";
            String PAGE_EVENT_CODE = "network_acceleration_success_page_custom";
            String PAGE_EVENT_NAME = "网络加速完成页曝光时";
            String RETURN_CLICK_NAME = "用户在网络加速完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }

        class Folder implements Point{
            String PAGE = "depth_clean_success_page";
            String PAGE_EVENT_CODE = "depth_clean_success_pagesuccess_page_custom";
            String PAGE_EVENT_NAME = "深度清理完成页曝光时";
            String RETURN_CLICK_NAME = "用户在深度清理完成页返回";

            @Override
            public String getPage() {
                return PAGE;
            }

            @Override
            public String getPageEventCode() {
                return PAGE_EVENT_CODE;
            }

            @Override
            public String getPageEventName() {
                return PAGE_EVENT_NAME;
            }

            @Override
            public String getReturnClickName() {
                return RETURN_CLICK_NAME;
            }
        }
    }

}

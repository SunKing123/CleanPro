package com.xiaoniu.cleanking.utils;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONObject;

public class NiuDataAPIUtil {

    /**
     * 清理完成
     * @param current_page_id
     * @param source_page_id
     * @param event_code
     * @param event_name
     */
    public static void onPageEnd(String source_page_id, String current_page_id, String event_code, String event_name) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(event_code, event_name,extension);
    }
    /**
     * 极光推送 点击埋点
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     * @param url
     */
    public static void trackClickJPush(String eventCode, String eventName, String sourcePage, String currentPage, String url,int id,String title) {
        String push_type = "";
        if ("cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=4".equals(url)){
            //立即清理页面
            AppHolder.getInstance().setCleanFinishSourcePageId("push_info_click");
            push_type = "clean_up_immediately";
        }else if (url.contains("main.activity.PhoneAccessActivity")){
            //  一键加速页面
            push_type = "mobile_phone_boost";
        }else if (url.contains("main.activity.CleanBigFileActivity")){
            //  手机清理页面
            push_type = "mobile_phone _cleaning";
        }else if (url.contains("main.activity.FileManagerHomeActivity")){
            //  文件清理页面
            push_type = "file_cleanup";
        }else if (url.contains("tool.wechat.activity.WechatCleanHomeActivity")){
            //  微信专清页面
            push_type = "wechat_cleaning";
        }else if (url.contains("tool.qq.activity.QQCleanHomeActivity")){
            //  QQ专清页面
            push_type = "QQ_cleaning";
        }else if (url.contains("main.activity.PhoneCoolingActivity")){
            // 手机降温页面
            push_type = "cooling";
        }else if (url.contains("main.activity.PhoneSuperPowerActivity")){
            // 超强省电页面
            push_type = "power_saving";
        }
        JSONObject extension = new JSONObject();
        try {
            extension.put("push_id", String.valueOf(id));
            extension.put("push_title", title);
            extension.put("push_type", push_type);
            extension.put("target_page_id", url);
            extension.put("source_page_id", sourcePage);
            extension.put("current_page_id", currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackClick(eventCode, eventName, extension);
    }

}

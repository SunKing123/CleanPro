package com.xiaoniu.common.utils;

import android.text.TextUtils;

import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 埋点统计工具类
 * Created by fengpeihao on 2018/1/24.
 */

public class StatisticsUtils {

    /**
     * sdk 点击埋点（click类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param eventName   事件说明
     * @param currentPage 当前页面
     */
    public static void trackClick(String eventCode, String eventName, String sourcePage, String currentPage) {
        trackClick(eventCode, eventName, sourcePage, currentPage, null);
    }

    /**
     * sdk 点击埋点（click类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     * @param extension   额外参数
     */
    public static void trackClick(String eventCode, String eventName, String sourcePage, String currentPage, JSONObject extension) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page_id", sourcePage);
            j.put("current_page_id", currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (extension != null) {
            JSONUtils.mergeJSONObject(extension, j);
        }

        NiuDataAPI.trackClick(eventCode, eventName, j);
    }

    public static void trackClickH5(String eventCode, String eventName, String sourcePage, String currentPage, String id, String name) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("content_cate_id", id.trim());
            extension.put("content_cate_name", name.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }

    /**
     * h5的点击埋点
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     * @param productId
     */
    public static void trackClickH5(String eventCode, String sourcePage, String currentPage, String productId) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("product_id", productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, "", sourcePage, currentPage, extension);
    }

    /**
     * h5的Url 点击埋点
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     * @param url
     */
    public static void trackClickUrlH5(String eventCode, String eventName, String sourcePage, String currentPage, String url) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", sourcePage);
            extension.put("current_page_id", currentPage);
            extension.put("h5_uri", url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }

    /**
     * sdk 首页广告
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param eventName   事件说明
     * @param currentPage 当前页面
     * @param position    广告位置
     */
    public static void trackClickAD(String eventCode, String eventName, String sourcePage, String currentPage, String position) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("ad_position_id", position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }

    /**
     * 页面进入
     *
     * @param eventCode 事件code
     */
    public static void onPageStart(String eventCode, String eventName) {
        NiuDataAPI.onPageStart(eventCode, eventName);
    }

    /**
     * 页面退出
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     */
    public static void onPageEnd(String eventCode, String eventName, String sourcePage, String currentPage) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page_id", sourcePage);
            j.put("current_page_id", currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(eventCode, eventName, j);
    }

    /**
     * h5页面的退出
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     */
    public static void onPageEndH5(String eventCode, String eventName, String sourcePage, String currentPage) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page_id", sourcePage);
            j.put("current_page_id", currentPage);
            //h5要设置对应的页面类型
            j.put("page_type", "h5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(eventCode, eventName, j);
    }

    /**
     * 极光推送 曝光点击埋点
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     */
    public static void trackClickJShow(String eventCode, String eventName, String sourcePage, String currentPage, String url,int id,String title) {
        String push_type = "";
        if ("cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=4".equals(url)){
            //立即清理页面
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
        NiuDataAPI.trackEvent(eventCode, eventName, extension);
    }

    /**
     * 资讯埋点
     * @param eventCode
     * @param eventName
     * @param sourcePage
     * @param currentPage
     * @param id
     */
    public static void trackClickNewsTab(String eventCode, String eventName, String sourcePage, String currentPage, int id) {
        String push_type = "";
        if (id == 0){
            //头条
            push_type = "headlines";
        }else  if (id == 1){
            //视频
            push_type = "video";
        }else  if (id == 2){
            //  社会
            push_type = "society";
        }else  if (id == 3){
            //  国内
            push_type = "domestic";
        }else  if (id == 4){
            //  国际
            push_type = "lnternational";
        }else  if (id == 5){
            //  娱乐
            push_type = "entertainment";
        }
        JSONObject extension = new JSONObject();
        try {
            extension.put("content_cate_id", String.valueOf(id));
            extension.put("content_cate_name", push_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }

    /**
     *  资讯内 item 埋点
     * @param eventCode
     * @param eventName
     * @param sourcePage
     * @param currentPage
     * @param newsName
     * @param newsId
     * @param position
     */
    public static void trackClickNewsItem(String eventCode, String eventName, String sourcePage, String currentPage,String newsName,String newsId, int position) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("content_id", newsId);
            extension.put("content_title", newsName);
            extension.put("position_id", String.valueOf(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }


    /**
     *  推荐 功能 埋点
     * @param eventCode
     * @param eventName
     * @param sourcePage
     * @param currentPage
     * @param newsName
     * @param position
     */
    public static void trackFunctionClickItem(String eventCode, String eventName, String sourcePage, String currentPage,String newsName, String position) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("position_title", newsName);
            extension.put("position_id", position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackClick(eventCode, eventName, sourcePage, currentPage, extension);
    }




    /**
     * sdk 点击埋点（click类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     * @param app_name   额外参数
     */
    public static void trackDivideClick(String eventCode, String eventName, String sourcePage, String currentPage, String app_name) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page_id", sourcePage);
            j.put("current_page_id", currentPage);
            j.put("app_name", app_name);
        } catch (Exception e) {
            e.printStackTrace();
        }


        NiuDataAPI.trackClick(eventCode, eventName, j);
    }



    /**
     * 添加source_page_id、current_page_id的自定义事件
     * @param event_code
     * @param event_name
     * @param source_page_id
     * @param current_page_id
     */
    public static void customTrackEvent(String event_code,String event_name, String source_page_id, String current_page_id) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }



    /**
     * 添加source_page_id、current_page_id的自定义事件
     *
     * @param event_code
     * @param event_name
     * @param source_page_id
     * @param current_page_id
     */
    public static void customTrackEvent(String event_code, String event_name, String source_page_id, String current_page_id, Map<String, Object> extParam) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            if (extParam != null && extParam.size() > 0) {
                for (Map.Entry<String, Object> param : extParam.entrySet()) {
                    extension.put(param.getKey(), param.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }

    /**
     * 广告曝光
     * @param event_code
     * @param event_name
     * @param ad_position_id
     * @param ad_id
     * @param ad_agency
     * @param source_page_id
     * @param current_page_id
     */
    public static void customAD(String event_code,String event_name,String ad_position_id, String ad_id, String ad_agency, String source_page_id, String current_page_id,String title) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("ad_position_id", ad_position_id);
            extension.put("ad_id", ad_id);
            extension.put("ad_title",title);
            extension.put("ad_agency", ad_agency);
            //广告sdk埋点
            String adBid = String.valueOf(ContextUtils.getAdBid());
            if (!TextUtils.isEmpty(adBid)) {
                extension.put("bid", adBid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }
    /**
     * 广告请求_自定义 类型
     * @param event_code
     * @param event_name
     * @param testing_status
     * @param source_page_id
     * @param current_page_id
     */
    public static void customCheckPermission(String event_code,String event_name,String testing_status, String source_page_id, String current_page_id) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("testing_status", testing_status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }
    /**
     * 广告请求_自定义 类型
     * @param event_code
     * @param event_name
     * @param ad_position_id
     * @param ad_id
     * @param ad_agency
     * @param ad_request_status
     * @param source_page_id
     * @param current_page_id
     */
    public static void customADRequest(String event_code,String event_name,String ad_position_id, String ad_id, String ad_agency,String ad_request_status, String source_page_id, String current_page_id) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("ad_position_id", ad_position_id);
            extension.put("ad_id", ad_id);
//            extension.put("ad_title","");//请求阶段无title
            extension.put("ad_agency", ad_agency);
            extension.put("ad_request_status", ad_request_status);
            //广告sdk埋点
            String adBid = String.valueOf(ContextUtils.getAdBid());
            if (!TextUtils.isEmpty(adBid)) {
                extension.put("bid", adBid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }

    /**
     * 广告点击
     * @param event_code
     * @param event_name
     * @param ad_position_id
     * @param ad_id
     * @param ad_agency
     * @param source_page_id
     * @param current_page_id
     */
    public static void clickAD(String event_code,String event_name,String ad_position_id, String ad_id, String ad_agency, String source_page_id, String current_page_id,String title) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("ad_position_id", ad_position_id);
            extension.put("ad_id", ad_id);
            extension.put("ad_title",title);
            extension.put("ad_agency", ad_agency);
            //广告sdk埋点
            String adBid = String.valueOf(ContextUtils.getAdBid());
            if (!TextUtils.isEmpty(adBid)) {
                extension.put("bid", adBid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackClick(event_code, event_name, extension);
    }


    /**
     * 刮刮卡弹窗埋点
     * @param event_code
     * @param event_name
     * @param cardIndex
     * @param source_page_id
     * @param current_page_id
     */
    public static void scratchCardCustom(String event_code,String event_name,int cardIndex, String source_page_id, String current_page_id) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("card_position_id", String.valueOf(cardIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackEvent(event_code, event_name, extension);
    }

    /**
     * 刮刮卡弹窗埋点
     * @param event_code
     * @param event_name
     * @param cardIndex
     * @param source_page_id
     * @param current_page_id
     */
    public static void scratchCardClick(String event_code,String event_name,int cardIndex, String source_page_id, String current_page_id) {
        JSONObject extension = new JSONObject();
        try {
            extension.put("source_page_id", source_page_id);
            extension.put("current_page_id", current_page_id);
            extension.put("card_position_id", String.valueOf(cardIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackClick(event_code, event_name, extension);
    }
}

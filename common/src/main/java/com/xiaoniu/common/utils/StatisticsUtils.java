package com.xiaoniu.common.utils;

import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

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
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
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
    public static void trackClickUrlH5(String eventCode,String eventName, String sourcePage, String currentPage, String url) {
        JSONObject extension = new JSONObject();
        try {
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
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
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
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
            //h5要设置对应的页面类型
            j.put("page_type", "h5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(eventCode, eventName, j);
    }
}

package com.xiaoniu.cleanking.utils;

import android.text.TextUtils;

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
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(eventCode);
        NiuDataAPI.trackClick(builder.toString(), eventName, j);
    }


    /**
     * sdk 首页广告
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param eventName   事件说明
     * @param currentPage 当前页面
     * @param position  广告位置
     */
    public static void trackClickHolder(String eventCode, String eventName, String sourcePage, String currentPage,String position) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
            j.put("ad_position_id",position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(eventCode);
        NiuDataAPI.trackClick(builder.toString(), eventName, j);
    }
    /**
     * h5的点击埋点
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     * @param productId
     */
    public static void trackH5Click(String eventCode, String sourcePage, String currentPage, String productId) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
            if (!TextUtils.isEmpty(productId)) {
                j.put("product_id", productId);
            }
            //h5要设置对应的页面类型
            j.put("page_type", "h5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackClick(eventCode, "", j);
    }

    /**
     * sdk 点击埋点（click类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     * @param extension   额外参数
     */
    public static void trackClick(String eventCode, String sourcePage, String currentPage, JSONObject extension) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (extension != null) {
            JSONUtils.mergeJSONObject(extension, j);
        }

        NiuDataAPI.trackClick(eventCode, "", j);
    }

    /**
     * sdk 埋点（custom类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     */
    public static void trackCustom(String eventCode, String sourcePage, String currentPage) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.trackClick(eventCode, "", j);
    }

    /**
     * sdk 埋点（custom类型）
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     * @param extension   需要扩展的键值对
     */
    public static void trackCustom(String eventCode, String sourcePage, String currentPage, JSONObject extension) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (extension != null) {
            JSONUtils.mergeJSONObject(extension, j);
        }
        NiuDataAPI.trackClick(eventCode, "", j);
    }

    /**
     * 页面进入
     *
     * @param eventCode 事件code
     */
    public static void onPageStart(String eventCode) {
        NiuDataAPI.onPageStart(eventCode, "");
    }

    /**
     * 页面退出
     *
     * @param eventCode   事件code
     * @param sourcePage  来源页面
     * @param currentPage 当前页面
     */
    public static void onPageEnd(String eventCode, String sourcePage, String currentPage) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(eventCode, "", j);
    }

    /**
     * h5页面进入
     *
     * @param eventCode 事件code
     */
    public static void onH5PageStart(String eventCode) {
        NiuDataAPI.onPageStart(eventCode, "");
    }

    /**
     * h5页面的退出
     *
     * @param eventCode
     * @param sourcePage
     * @param currentPage
     */
    public static void onH5PageEnd(String eventCode, String sourcePage, String currentPage) {
        JSONObject j = new JSONObject();
        try {
            j.put("source_page", sourcePage);
            j.put("current_page", currentPage);
            //h5要设置对应的页面类型
            j.put("page_type", "h5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NiuDataAPI.onPageEnd(eventCode, "", j);
    }
}

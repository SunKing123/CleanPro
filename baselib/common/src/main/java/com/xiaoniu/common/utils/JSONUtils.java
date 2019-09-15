package com.xiaoniu.common.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by fangmingdong on 2016/12/27.
 * json tools
 */

public class JSONUtils {
    public static final String TAG = "JSONUtils/TAG";

    public static final int getInt(JSONObject json, String key) {
        if (json == null) {
            return 0;
        }
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static final String getString(JSONObject json, String key) {
        if (json == null) {
            return "";
        }
        try {
            return json.get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static final long getLong(JSONObject json, String key) {
        if (json == null) {
            return 0l;
        }

        try {
            return Long.parseLong(json.get(key).toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return 0l;
        }
    }

    public static final void put(JSONObject json, String key, Object value) {

        if (json == null) return;

        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final JSONObject build(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return new JSONObject();
        } else {
            try {
                return new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }
    }

    /**
     * 合并JSONObject
     *
     * @param source
     * @param dest   最终要合并的对象
     */
    public static void mergeJSONObject(JSONObject source, JSONObject dest) {
        try {
            Iterator<String> superPropertiesIterator = source.keys();
            while (superPropertiesIterator.hasNext()) {
                String key = superPropertiesIterator.next();
                Object value = source.get(key);
                dest.put(key, value);
            }
        } catch (Exception e) {
        }
    }
}

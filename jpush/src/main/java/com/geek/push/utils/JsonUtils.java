package com.geek.push.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Json 工具类
 * Created by pyt on 2017/7/14.
 */

public class JsonUtils {

    /**
     * 转换成Map
     *
     * @param jsonObject
     * @return
     */
    public static HashMap<String, String> toMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Iterator<String> keys = jsonObject.keys();
        HashMap<String, String> map = new HashMap<>();
        while (keys.hasNext()) {
            String next = keys.next();
            try {
                Object o = jsonObject.get(next);
                map.put(next, String.valueOf(o));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static boolean isJsonFormat(String json) {
        return !TextUtils.isEmpty(json) && (json.startsWith("{") || json.startsWith("[")
                && (json.endsWith("]") || json.endsWith("}")));
    }

    public static String encode(Object json) {
        return new Gson().toJson(json);
    }

    public static String encode(Object json, boolean isPretty) {
        return (isPretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson()).toJson(json);
    }

    public static Object decode(String json) {
        return new JsonParser().parse(json);
    }

    public static <T> T decode(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    public static <T> T decode(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }

    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();

        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();

        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }
}

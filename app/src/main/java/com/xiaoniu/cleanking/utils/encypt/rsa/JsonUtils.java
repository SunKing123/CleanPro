package com.xiaoniu.cleanking.utils.encypt.rsa;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by RobinYu on 2017/4/3.
 */

public class JsonUtils {
    public static Map<String, Object> fromJson(String json) {
        return fromJson(json, Map.class);
    }

    public static String toJson(Object src) {
        Gson gson = new Gson();
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json, typeOfT);
    }

    public static boolean isValidJson(String json) {
        try {
            fromJson(json);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }

    public static boolean isValidJson(String json, Type typeOfT) {
        try {
            fromJson(json, Map.class);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }
}

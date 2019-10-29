package com.xiaoniu.cleanking.scheme.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * deprecation:协议参数
 * author:ayb
 * time:2017-6-8
 */
public class Parameters {
    private final Map<String, Map<Integer, Object>> paramHashValues = new LinkedHashMap<>();

    private int limit = -1;
    private int parameterCount = 0;
    private int parameterIndex = 0;

    public void addParameter(String key, Object value) throws IllegalStateException {
        if (key == null) {
            return;
        }
        if (!checkValuePrimitive(value)) {
            throw new IllegalArgumentException("Please use value which is primitive type like: String,Integer,Long and so on. But not Collection !");
        }
        parameterCount++;
        if (limit > -1 && parameterCount > limit) {
            throw new IllegalStateException("parameters.maxCountFail: " + limit);
        }

        Map<Integer, Object> values = paramHashValues.get(key);
        if (values == null) {
            values = new LinkedHashMap<>(1);
            paramHashValues.put(key, values);
        }
        values.put(parameterIndex++, value == null ? "" : value);
    }

    private boolean checkValuePrimitive(Object value) {
        return (value == null || value instanceof String || value instanceof Integer
                || value instanceof Long || value instanceof Boolean || value instanceof Float
                || value instanceof Double || value instanceof Character || value instanceof Byte
                || value instanceof Short);
    }

    public String[] getParameterValues(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        return values.values().toArray(new String[values.size()]);
    }

    public String getParameter(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values != null) {
            if (values.size() == 0) {
                return "";
            }
            String value = values.values().iterator().next().toString();
            return (value != null && !"null".equals(value)) ? value : "";
        } else {
            return "";
        }
    }

    public Set<String> getParameterNames() {
        return paramHashValues.keySet();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

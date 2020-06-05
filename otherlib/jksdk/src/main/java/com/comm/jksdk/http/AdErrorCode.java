package com.comm.jksdk.http;

import android.text.TextUtils;

import com.comm.jksdk.utils.CollectionUtils;

import java.util.Map;

/**
 * 代码描述<p>
 *
 *错误代码描述
 */
public class AdErrorCode {


    public static final int ERROR_EMPTY = 9000001;//联盟返回物料空_错误码

    private final static Map<Integer, String> map = CollectionUtils.createMap();

    static {
        map.put(ERROR_EMPTY, "联盟返回物料为空");
    }

    public static String getError(int code){
        String msg = map.get(code);
        if (TextUtils.isEmpty(msg)) {
            return "未知错误，错误码："+code;
        }
        return msg;
    }

}

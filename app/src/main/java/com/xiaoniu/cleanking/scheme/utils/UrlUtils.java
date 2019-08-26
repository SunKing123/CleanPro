package com.xiaoniu.cleanking.scheme.utils;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * deprecation:协议地址工具类
 * author:ayb
 * time:2017-6-8
 */
public class UrlUtils {

    public static Parameters getParamsFromUrl(String url) {
        Parameters parameters = null;
        if (url != null && url.indexOf('?') > -1) {
            String tempUrl = url.substring(url.indexOf('?') + 1);
            int indexSharp = tempUrl.indexOf('#');
            if(indexSharp > -1) {
                parameters = splitUrlQuery(tempUrl.substring(0, indexSharp));
            } else {
                parameters = splitUrlQuery(tempUrl);
            }
        }
        if (parameters == null) {
            parameters = new Parameters();
        }
        return parameters;
    }

    public static Parameters splitUrlQuery(String query) {
        Parameters parameters = new Parameters();
        try {
            String[] pairs = query.split("&");
            if (pairs != null && pairs.length > 0) {
                for (String pair : pairs) {
                    String[] param = pair.split("=", 2);
                    if (param != null && param.length == 2) {
                        parameters.addParameter(URLDecoder.decode(param[0]), URLDecoder.decode(param[1]));
                    }
                }
            }
        }catch (Exception e){
            Log.e("UrlUtils",e.getMessage());
        }
        return parameters;
    }

    public static String removeParameter(String strUrl, String query)
            throws IOException {
        URL url = new URL(strUrl);
        if (query.isEmpty()) {
            return strUrl;
        }
        if (url.getQuery().isEmpty()) {
            return strUrl;
        } else if (strUrl.contains("&" + query) || strUrl.contains("?" + query)) {
            return strUrl.replace(query, "");
        }
        return strUrl;
    }

}

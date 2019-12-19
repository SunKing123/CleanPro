package com.comm.jksdk.http.utils;


import com.comm.jksdk.http.Api;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/4/24 12:40
 */
public class ApiManage {

    public static String getWeatherURL() {
        String weatherUrl = null;
        switch (AppEnvironment.getServerApiEnvironment()) {
            case Dev:
                weatherUrl = Api.URL_DEV.APP_WEATHER_DOMAIN;
                break;
            case Test:
                weatherUrl = Api.URL_TEST.APP_WEATHER_DOMAIN;
                break;
            case Uat:
                weatherUrl = Api.URL_UAT.APP_WEATHER_DOMAIN;
                break;
            case Product:
                weatherUrl = Api.URL_PRODUCT.APP_WEATHER_DOMAIN;
                break;
            default:
                weatherUrl = Api.URL_PRODUCT.APP_WEATHER_DOMAIN;
                break;
        }
        return weatherUrl;
    }


}


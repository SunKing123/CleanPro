package com.comm.jksdk.http;

/**
 * 代码描述<p>
 *
 *     存放一些与 API 有关的东西,如请求地址
 *
 * @author anhuiqing
 * @since 2019/3/30 18:34
 */
public interface Api {

    String WEATHER_DOMAIN_NAME = "weather";

    class URL_DEV{//开发环境
        public static final String APP_WEATHER_DOMAIN = "http://172.16.11.251:8974/adsenseapi/";

    }

    class URL_TEST{//测试环境
//        public static final String APP_WEATHER_DOMAIN = "http://172.16.11.247:8974/adsenseapi/"; //测试环境
//        public static final String APP_WEATHER_DOMAIN = "http://testadsenseapi.hellogeek.com/adsenseapi/"; //测试环境

        //广告中心整合
          public static final String APP_WEATHER_DOMAIN = "http://clsystem-mclean-dev-default.fqt188.com/"; //测试环境

    }

    class URL_UAT{//预发布环境
//        public static final String APP_WEATHER_DOMAIN = "http://testadsenseapi.hellogeek.com/adsenseapi/";//预发布域名

        //广告中心整合
        public static final String APP_WEATHER_DOMAIN = "http://clsystem-mclean-fat-default.fqt188.com/"; //测试环境

    }

    class URL_PRODUCT{//生产环境
//        public static final String APP_WEATHER_DOMAIN = "http://adsenseapi.hellogeek.com/adsenseapi/";
        public static final String APP_WEATHER_DOMAIN = "https://clsystem.wukongclean.com/";

    }




}
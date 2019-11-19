package com.comm.jksdk.http.utils;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/4/24 12:51
 */
public class UaUtils {
    public static String getUa(){
        return "geekWeather/" + AppInfoUtils.getVersionName() +"/"+ getSystemUa();
    }

    public static String getSystemUa(){
        String systemUa = null;
        try {
            systemUa = System.getProperty("http.agent");
        }catch (Exception e){

        }
        return systemUa;
    }
}

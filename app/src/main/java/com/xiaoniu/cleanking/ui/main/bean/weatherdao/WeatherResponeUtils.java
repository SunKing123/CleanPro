package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.util.Base64;

import com.xiaoniu.cleanking.utils.GZipUtils;

import java.io.IOException;

/**
 * 代码描述<p>
 *
 * @author xiajianbin
 * @since 2019/4/16 14:22
 */
public class WeatherResponeUtils {

    public static String getResponseStr(String content){
        String decompress="";
        byte[] decode = Base64.decode(content.getBytes(), Base64.DEFAULT);
        try {
             decompress = GZipUtils.decompress(decode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompress;
    }
}

package com.comm.jksdk.http.utils;

import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.Constant;


import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 代码描述<p>
 *
 *     获取渠道包名称
 *
 * @author anhuiqing
 * @since 2019/4/16 18:47
 */
public class ChannelUtil {

    private static final String CHANNEL_KEY = "cztchannel";


    /**
     * 返回市场。  如果获取失败返回""
     *
     * @return
     */
    public static String getChannel() {
        return getChannelFromApk(CHANNEL_KEY);
    }



    /**
     * 从apk中获取版本信息
     *
     * @param channelKey
     * @return
     */
    private static String getChannelFromApk(String channelKey) {
        //从apk包中获取
        if(GeekAdSdk.getContext()==null){
            return "";
        }
        ApplicationInfo appinfo = GeekAdSdk.getContext().getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }


            return TextUtils.isEmpty(channel)? Constant.CHANNEL_TEST :channel;

//            return TextUtils.isEmpty(channel)? GlobalConstant.CHANNEL_RELEASE:channel;

    }

}

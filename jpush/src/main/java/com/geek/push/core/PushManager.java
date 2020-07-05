package com.geek.push.core;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.geek.push.cache.PushConfigCache;
import com.geek.push.log.LogUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * the one push context
 * <p>
 * Created by pyt on 2017/5/9.
 */

public class PushManager {

    //the meta_data split symbol
    public static final String METE_DATA_SPLIT_SYMBOL = "_";
    private static final String TAG = "PushManager";
    //the meta-data header
    private static final String META_DATA_PUSH_HEADER = "Geek_";
    private IPushClient mIPushClient;

    private int mPlatformCode;

    private String mPlatformName;
    private Context context;
    //all support push platform map
    private LinkedHashMap<String, String> mAllSupportPushPlatformMap = new LinkedHashMap<>();

    private PushManager() {

    }

    /**
     * Using the simple interest
     *
     * @return
     */
    public static PushManager getInstance() {
        return Single.sInstance;
    }

    public void init(Application context, OnPushRegisterListener listener) {
        try {
            this.context = context;
            //find all support push platform
            Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                Set<String> allKeys = metaData.keySet();
                if (allKeys != null && !allKeys.isEmpty()) {
                    Iterator<String> iterator = allKeys.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        LogUtils.d("meta data key:" + key);
                        if (key.startsWith(META_DATA_PUSH_HEADER)) {
                            mAllSupportPushPlatformMap.put(key, metaData.getString(key));
                        }
                    }
                }
            } else {
                LogUtils.e("meta data is null");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (mAllSupportPushPlatformMap.isEmpty()) {
            throw new IllegalArgumentException("have none push platform,check AndroidManifest.xml is have meta-data name is start with OnePush_");
        }

        //choose custom push platform
        Iterator<Map.Entry<String, String>> iterator = mAllSupportPushPlatformMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String metaPlatformName = next.getKey();
            String metaPlatformClassName = next.getValue();
            StringBuilder stringBuilder = new StringBuilder(metaPlatformName).delete(0, 5);
            int len = stringBuilder.length();
            int lastIndexSymbol = stringBuilder.lastIndexOf(METE_DATA_SPLIT_SYMBOL);
            int platformCode = Integer.parseInt(stringBuilder.substring(lastIndexSymbol + 1, len));
            String platformName = stringBuilder.substring(0, lastIndexSymbol);
            LogUtils.d("platformCode:" + platformCode + " platformName:" + platformName);
            try {
                Class<?> currentClz = Class.forName(metaPlatformClassName);
                Class<?>[] interfaces = currentClz.getInterfaces();
                List<Class<?>> allInterfaces = Arrays.asList(interfaces);
                LogUtils.d("currentClz:" + currentClz + " interfaces:" + allInterfaces.size());
                if (allInterfaces.contains(IPushClient.class)) {
                    //create object with no params
                    IPushClient iPushClient = (IPushClient) currentClz.newInstance();
                    LogUtils.d("iPushClient:" + iPushClient.toString());
                    if (listener.onRegisterPush(platformCode, platformName)) {
                        this.mIPushClient = iPushClient;
                        this.mPlatformCode = platformCode;
                        this.mPlatformName = platformName;
                        //invoke IPushClient initContext method
                        LogUtils.i("current register platform is " + metaPlatformName);
                        iPushClient.initContext(context);
                        break;
                    } else {
                        LogUtils.e("-------unregister push-----");
                    }
                } else {
                    throw new IllegalArgumentException(metaPlatformClassName + "is not implements " + IPushClient.class.getName());
                }

            } catch (ClassNotFoundException e) {
                LogUtils.e(e.getMessage());
                throw new IllegalArgumentException("can not find class " + metaPlatformClassName);
            } catch (InstantiationException e) {
                LogUtils.e(e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                LogUtils.e(e.getMessage());
                e.printStackTrace();
            }
        }
        //clear cache client
        mAllSupportPushPlatformMap.clear();
        if (mIPushClient == null) {
            throw new IllegalStateException("onRegisterPush must at least one of them returns to true");
        }
    }

    public void register() {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "register()"));
        mIPushClient.register();
    }

    public void unRegister() {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "unRegister()"));
        mIPushClient.unRegister();
    }

    public void bindAlias(String alias) {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "bindAlias(" + alias + ")"));
        mIPushClient.bindAlias(alias);
    }

    public void unBindAlias(String alias) {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "unBindAlias(" + alias + ")"));
        mIPushClient.unBindAlias(alias);
    }

    public void checkTagState(String tag) {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "checkTagState(" + tag + ")"));
        mIPushClient.checkTagState(tag);
    }

    public void addTag(String tag) {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "addTag(" + tag + ")"));
        mIPushClient.addTag(tag);
    }

    public void deleteTag(String tag) {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "deleteTag(" + tag + ")"));
        mIPushClient.deleteTag(tag);
    }

    public void clearAllTag() {
        LogUtils.i(String.format("%s--%s", getPushPlatFormName(), "deleteAllTag"));
        mIPushClient.clearAllTag();
    }

    public int getPushPlatFormCode() {
        return mPlatformCode;
    }

    public String getPushPlatFormName() {
        return mPlatformName;
    }

    public String getRid() {
        return PushConfigCache.getRid(context);
    }

    private static class Single {
        static PushManager sInstance = new PushManager();
    }

}

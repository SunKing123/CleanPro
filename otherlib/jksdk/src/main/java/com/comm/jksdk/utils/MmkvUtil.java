package com.comm.jksdk.utils;

import com.comm.jksdk.BuildConfig;
import com.tencent.mmkv.MMKV;

/**
 * @author zhengzhihao
 * @date 2019/12/11 13
 * @mail：zhengzhihao@hellogeek.com 多进程_key_value 存取
 */
public class MmkvUtil {

    //(string数据保存)
    public static void saveString(String key, String data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(string数据获取)
    public static String getString(String key, String def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeString(key) : def;
    }

    //(int数据保存)
    public static void saveInt(String key, int data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(int数据获取)
    public static int getInt(String key, int def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeInt(key) : def;
    }


    //(boolean数据保存)
    public static void saveBool(String key, boolean data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(boolean数据获取)
    public static boolean getBool(String key, boolean def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeBool(key) : def;
    }


    //(long数据保存)
    public static void saveLong(String key, long data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(long数据获取)
    public static long getLong(String key, long def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.LIBRARY_PACKAGE_NAME, MMKV.MULTI_PROCESS_MODE);
        return kv.decodeLong(key, def);
    }


}

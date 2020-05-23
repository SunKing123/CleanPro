package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.xiaoniu.cleanking.app.Constant.WHITE_LIST;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/23
 */
public class PhoneAccessUtil {


    /**
     *
     * @param listInfos
     * @return
     */
    public static ArrayList<FirstJunkInfo> filterFirstJunkInfo(ArrayList<FirstJunkInfo> listInfos) {
        ArrayList<FirstJunkInfo> listInfoData = new ArrayList<>();
        if (listInfos == null || listInfos.size() == 0) {
            return listInfoData;
        }
        for (FirstJunkInfo firstJunkInfo : listInfos) {
            if (!PhoneAccessUtil.isCacheWhite(firstJunkInfo.getAppPackageName()) && firstJunkInfo.getGarbageIcon() != null ) {
                listInfoData.add(firstJunkInfo);
            }
        }
        return listInfoData;
    }

    /**
     * 获取缓存白名单
     */
    public static boolean isCacheWhite(String packageName) {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
//        Set<String> sets = sp.getStringSet(WHITE_LIST_SOFT_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        if (null != sets && sets.size() > 0) {
            Iterator<String> it = sets.iterator();
            while (it.hasNext()) {
                String str = it.next();
            }
        }
        if(sets.contains(packageName)){
            return true;
        }
        return WHITE_LIST.contains(packageName);
    }

    /**
     * 计算总的缓存大小
     * @param context
     * @param listInfo
     * @return
     */
    public static long computeTotalSize(Context context, ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            for (FirstJunkInfo firstJunkInfo : listInfo) {
                totalSizes += !isCacheWhite(firstJunkInfo.getAppPackageName()) ? firstJunkInfo.getTotalSize() : 0;
            }
        } else { //8.0以上内存[200M,2G]随机数
            long lastCheckTime = SPUtil.getLong(context, SPUtil.ONEKEY_ACCESS, 0);
            long timeTemp = System.currentTimeMillis() - lastCheckTime;
            if (timeTemp >= 3 * 60 * 1000 && timeTemp < 6 * 60 * 1000) { // 3 - 6min
                long cacheSize = SPUtil.getLong(context, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = (long) (cacheSize * 0.3);
                SPUtil.setLong(context, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
            } else if (timeTemp >= 6 * 60 * 1000 && timeTemp < 10 * 60 * 1000) { // 6 - 10 min
                long cacheSize = SPUtil.getLong(context, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = (long) (cacheSize * 0.6);
                SPUtil.setLong(context, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
            } else {
                SPUtil.setLong(context, SPUtil.ONEKEY_ACCESS, 0);
                SPUtil.setLong(context, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = Long.valueOf(NumberUtils.mathRandom(200 * 1024 * 1024, 2 * 1024 * 1024 * 1024));
            }

        }
        return totalSizes;
    }


}

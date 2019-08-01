package com.xiaoniu.cleanking.ui.tool.wechat.util;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class QueryFileUtil {
    private Method mGetPackageSizeInfoMethod;
    long oneAppCacheSize = 0;

    public Long getOneAppCache(Activity activity, String str, final int i) {
        try {
            this.mGetPackageSizeInfoMethod = activity.getPackageManager().getClass().getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            this.mGetPackageSizeInfoMethod.invoke(activity.getPackageManager(), new Object[]{str, new IPackageStatsObserver.Stub() {
                public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
                    if (z) {
                        try {
                            QueryFileUtil.this.oneAppCacheSize = packageStats.cacheSize + packageStats.externalCacheSize;
                            if (i == -1) {
                                QueryFileUtil.this.oneAppCacheSize += packageStats.dataSize;
                                QueryFileUtil.this.oneAppCacheSize += packageStats.externalDataSize;
                                QueryFileUtil.this.oneAppCacheSize += packageStats.codeSize;
                                QueryFileUtil.this.oneAppCacheSize += packageStats.externalCodeSize;
                            }
                        } catch (Exception e) {
                        }
                    }
                    countDownLatch.countDown();
                }
            }});
            countDownLatch.await();
            return Long.valueOf(this.oneAppCacheSize);
        } catch (Exception e) {
            return Long.valueOf(0);
        }
    }

}

package com.xiaoniu.cleanking.ui.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.xiaoniu.cleanking.utils.CacheUtil;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.receiver
 * @ClassName: InstallUninstallBroadcastReceiver
 * @Description: 應用安裝卸載監聽
 * @Author: LiDing
 * @CreateDate: 2020/5/11 9:48
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/11 9:48
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class InstallUninstallBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = InstallUninstallBroadcastReceiver.class.getSimpleName();

    private static AlertDialog mDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 获取（安装/替换/卸载）应用的 信息
         */
        String packages = intent.getDataString();
        packages = packages.split(":")[1];

        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            Log.d(TAG, packages + "应用程序安装了，需要进行该应用安全扫描吗");
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            Log.d(TAG, packages + "应用程序卸载了，需要清理垃圾有缓存吗");
            try {
                showClearAlert(context, packages);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            Log.d(TAG, packages + "应用程序覆盖了");
        }
    }

    public void showClearAlert(Context context, String packages) throws Exception {
        long cacheSize = CacheUtil.getTotalCacheSize(context, packages);
        if (cacheSize <= 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAppName("刚卸载的应用")
                .setGarbageNum(CacheUtil.getFormatSize(cacheSize))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        CacheUtil.clearAllCache(context, packages);
                    }
                });
        mDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
    }

}

package com.xiaoniu.cleanking.ui.main.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import com.xiaoniu.cleanking.R;
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

    /**
     * @param path 路径
     * @return void
     * @method clearUninstallResidue
     * @description 清除卸載殘留
     * @date: 2020/5/11 9:48
     * @author: LiDing
     */
    public void clearUninstallResidue(String path) {
        delFileByPath(path);
    }


    private boolean delFileByPath(String filePath) {
        // showDialog("清理中");
        try {
            File dir = new File(filePath);
            if (dir == null || !dir.exists() || !dir.isDirectory()) {
//                dismissDialog();
                ToastUtils.showShort("缓存文件空");
                return false;
            }

            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    //自定义
                    /**
                     if (Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date())) - Integer.parseInt(file.getName().split("-")[2].split("\\.")[0].substring(0, 8)) > 30) {
                     Log.e(TAG, "deleteFolderFile:30天之前的删除 ");
                     }
                     Log.e(TAG, "deleteFile: " + file.getName().split("-")[2].split("\\.")[0].substring(0, 8));
                     */
                    //具体操作
                    file.delete(); // 删除所有文件
                } else if (file.isDirectory()) {
                    delFileByPath(file.getPath()); // 递规的方式删除文件夹
                }
            }
            Log.e(TAG, "deleteFolder: " + dir.getName());
            dir.delete(); // 删除目录本身
//            initView();
//            dismissDialog();
            ToastUtils.showShort("清理完成");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            dismissDialog();
            ToastUtils.showShort("清理失败");
            return false;
        }
    }


    /**
     * 获取指定目录大小
     *
     * @return
     */
    public double getDirSize(String filePath) {
        File file = new File(filePath);
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f.getAbsolutePath());
                return size;
            } else { //如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

}

package com.xiaoniu.cleanking.utils;

import android.content.res.AssetManager;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库操作工具类
 */
public class DbHelper {

    public static void copyDb() {
        ///data/data/packageName/databases/
        File mkdir = new File(AppApplication.getInstance().getFilesDir().getParent(), "databases");
        //创建 databases文件夹
        if (!mkdir.exists()) {
            mkdir.mkdirs();
        }
        //数据库文件
        File file = new File(mkdir, "clean_db.db");
        //只是在程序第一次启动时创建
        if (!file.exists()) {
            //获取 assets管理
            AssetManager assets = AppApplication.getInstance().getAssets();
            //执行文件复制
            try {
                InputStream open = assets.open(Constant.CLEAN_DB_NAME);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bs = new byte[1024];
                int len;
                while ((len = open.read(bs)) != -1) {
                    fos.write(bs, 0, len);
                }
                fos.flush();
                fos.close();
                open.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

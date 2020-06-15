package com.xiaoniu.cleanking.room.clean;
import com.xiaoniu.cleanking.bean.path.AppPath;
import com.xiaoniu.cleanking.bean.path.UninstallList;
import com.xiaoniu.cleanking.bean.path.UselessApk;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * @author XiLei
 * @date 2019/10/30.
 * description：
 */
@Database(entities = {AppPath.class, UninstallList.class, UselessApk.class}, version = 2) //1.4.0 -->  version = 2
public abstract class AppPathDataBase extends RoomDatabase {

    public abstract CleanPathDao cleanPathDao();

    public abstract UninstallListDao uninstallListDao();


    public abstract UselessApkDao uselessApkDao();

}

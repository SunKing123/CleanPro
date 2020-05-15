package com.xiaoniu.cleanking.app;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;

import com.xiaoniu.cleanking.room.AppDataBase;

/**
 * Desc: room数据库初始化和升级相关
 * <p>
 * Author: WuCongYi
 * Date: 2020/5/13
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * 构建配置参见:
 *
 * @author wucongyi
 */
public class RoomDataBaseUtils {

    public static AppDataBase init(Application app) {
        AppDataBase dataBase = Room.databaseBuilder(app, AppDataBase.class, "wukong_cleanking.db")
                .allowMainThreadQueries()
                .addMigrations(addAdInfoTable(1, 3))
                .addMigrations(addAdInfoTable(2, 3))
                .build();
        return dataBase;
    }


    /**
     * 新增加一张AdInfo的表
     *
     * @param startVersion 老版本
     * @param endVersion   新版本
     */
    private static Migration addAdInfoTable(int startVersion, int endVersion) {
        return new Migration(startVersion, endVersion) {
            @Override
            public void migrate(SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `AdInfo` (`isOpen` INTEGER NOT NULL, `configKey` TEXT NOT NULL," +
                        " `switcherName` TEXT, `advertPosition` TEXT NOT NULL," +
                        " `versions` TEXT, `advertId` TEXT, `secondAdvertId` TEXT," +
                        " `showRate` INTEGER NOT NULL, PRIMARY KEY(`configKey`, `advertPosition`))");
            }
        };
    }


}

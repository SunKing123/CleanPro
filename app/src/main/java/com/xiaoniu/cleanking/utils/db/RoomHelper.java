package com.xiaoniu.cleanking.utils.db;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.room.clean.AppPathDataBase;

import androidx.room.Room;

/**
 * @author zhengzhihao
 * @date 2020/6/12 19
 * @mail：zhengzhihao@hellogeek.com
 */
public class RoomHelper {

    private AppPathDataBase db;
    private volatile static RoomHelper roomHelper;

    private RoomHelper() {
        db = Room.databaseBuilder(AppApplication.getInstance(),
                AppPathDataBase.class, Constant.CLEAN_DB_NAME)
                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .build();
    }

    public static RoomHelper getInstance() {
        if (roomHelper == null) {

            roomHelper = new RoomHelper();


        }
        return roomHelper;
    }

    public AppPathDataBase getDb() {
        return db;
    }

    public void setDb(AppPathDataBase db) {
        this.db = db;
    }
}

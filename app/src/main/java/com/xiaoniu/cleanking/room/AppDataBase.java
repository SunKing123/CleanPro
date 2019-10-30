package com.xiaoniu.cleanking.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;

/**
 * @author XiLei
 * @date 2019/10/30.
 * description：
 */
@Database(entities = {HomeRecommendListEntity.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract HomeRecommendDao homeRecommendDao();
}

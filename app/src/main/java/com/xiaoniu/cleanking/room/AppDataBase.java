package com.xiaoniu.cleanking.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;

/**
 * @author XiLei
 * @date 2019/10/30.
 * description：
 */
@Database(entities = {HomeRecommendListEntity.class, GameSelectEntity.class}, version = 2)
//1.4.0 -->  version = 2
public abstract class AppDataBase extends RoomDatabase {
    public abstract HomeRecommendDao homeRecommendDao();

    public abstract GameSelectDao gameSelectDao();
}

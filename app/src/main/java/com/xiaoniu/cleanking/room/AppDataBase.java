package com.xiaoniu.cleanking.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;

/**
 * @author XiLei
 * @date 2019/10/30.
 * description：
 */
@Database(entities = {HomeRecommendListEntity.class, GameSelectEntity.class, SwitchInfoList.DataBean.class}, version = 3) //1.4.0 -->  version = 2
public abstract class AppDataBase extends RoomDatabase {
    public abstract HomeRecommendDao homeRecommendDao();

    public abstract GameSelectDao gameSelectDao();
    //广告配置数据
    public abstract AdInfotDao adInfotDao();
}

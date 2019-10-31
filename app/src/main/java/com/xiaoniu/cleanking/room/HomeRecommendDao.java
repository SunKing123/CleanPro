package com.xiaoniu.cleanking.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;

import java.util.List;

/**
 * @author XiLei
 * @date 2019/10/30.
 * description：
 */
@Dao
public interface HomeRecommendDao {

    @Query("SELECT * FROM HomeRecommend")
    List<HomeRecommendListEntity> getAll();

    @Insert
    void insertAll(List<HomeRecommendListEntity> list);

    @Query("DELETE FROM HomeRecommend")
    void deleteAll();
}

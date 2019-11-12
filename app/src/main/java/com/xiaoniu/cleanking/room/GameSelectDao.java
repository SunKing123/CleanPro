package com.xiaoniu.cleanking.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;

import java.util.List;

/**
 * @author XiLei
 * @date 2019/11/2.
 * description：
 */
@Dao
public interface GameSelectDao {

    @Query("SELECT * FROM GameSelect")
    List<GameSelectEntity> getAll();

    @Insert
    void insertAll(List<GameSelectEntity> list);

    @Query("DELETE FROM GameSelect")
    void deleteAll();
}

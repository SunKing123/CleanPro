package com.xiaoniu.cleanking.room.clean;

import com.xiaoniu.cleanking.bean.path.UselessApk;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.room
 * @ClassName: AdInfotDao
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/8 20:36
 */
@Dao
public interface UselessApkDao {

    @Query("SELECT * FROM uselessApk")
    List<UselessApk> getAll();

    @Insert
    void insertAll(List<UselessApk> list);

    @Query("DELETE FROM uselessApk")
    void deleteAll();
}

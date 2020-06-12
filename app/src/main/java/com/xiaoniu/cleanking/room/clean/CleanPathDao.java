package com.xiaoniu.cleanking.room.clean;

import com.xiaoniu.cleanking.bean.path.AppPath;

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
public interface CleanPathDao {

    @Query("SELECT * FROM applist")
    List<AppPath> getAll();
//
//    @Query("SELECT * FROM AdInfo WHERE configKey= :configKey AND advertPosition= :advertPosition")
//    List<SwitchInfoList.DataBean> getAdInfo(String configKey, String advertPosition);

    @Insert
    void insertAll(List<AppPath> list);

    @Query("DELETE FROM applist")
    void deleteAll();
}

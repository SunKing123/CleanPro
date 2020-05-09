package com.xiaoniu.cleanking.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;

import java.util.List;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.room
 * @ClassName: AdInfotDao
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/8 20:36
 */
@Dao
public interface AdInfotDao {

    @Query("SELECT * FROM AdInfo")
    List<SwitchInfoList.DataBean> getAll();

    @Query("SELECT * FROM AdInfo WHERE configKey= :configKey AND advertPosition= :advertPosition")
    List<SwitchInfoList.DataBean> getAdInfo(String configKey,String advertPosition);

    @Insert
    void insertAll(List<SwitchInfoList.DataBean> list);

    @Query("DELETE FROM AdInfo")
    void deleteAll();
}

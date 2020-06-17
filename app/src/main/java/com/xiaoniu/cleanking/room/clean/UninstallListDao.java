package com.xiaoniu.cleanking.room.clean;

import com.xiaoniu.cleanking.bean.path.AppPath;
import com.xiaoniu.cleanking.bean.path.UninstallList;

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
public interface UninstallListDao {

    @Query("SELECT * FROM uninstallList")
    List<UninstallList> getAll();

    //去重查询
    @Query("SELECT DISTINCT packageName FROM uninstallList")
    List<String> getUninstallList();

    @Query("SELECT * FROM uninstallList WHERE packageName= :packageName ")
    List<UninstallList> getPathList(String packageName);

    @Query("SELECT * FROM uninstallList WHERE packageName= :packageName ")
    List<UninstallList> getPathData(String packageName);
    @Insert
    void insertAll(List<UninstallList> list);

    @Query("DELETE FROM uninstallList")
    void deleteAll();
}

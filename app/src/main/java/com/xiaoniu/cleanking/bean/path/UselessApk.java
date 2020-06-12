package com.xiaoniu.cleanking.bean.path;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author zhengzhihao
 * @date 2020/6/2 18
 * @mail：zhengzhihao@hellogeek.com
 */
@Entity(tableName = "uselessApk")
public class UselessApk {

    @NonNull
    @PrimaryKey
    private String id;
    private String filePath;


    public UselessApk(@NonNull String id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

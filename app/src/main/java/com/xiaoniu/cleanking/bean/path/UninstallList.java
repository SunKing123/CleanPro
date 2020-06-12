package com.xiaoniu.cleanking.bean.path;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author zhengzhihao
 * @date 2020/6/2 18
 * @mail：zhengzhihao@hellogeek.com
 */
@Entity(tableName = "uninstallList")
public class UninstallList {

    @NonNull
    @PrimaryKey
    private String id;
    private String filePath;
    private String nameEn;
    private String nameZh;
    private String packageName;





    public UninstallList(@NonNull String id, String filePath, String nameEn, String nameZh, String packageName) {
        this.id = id;
        this.filePath = filePath;
        this.nameEn = nameEn;
        this.nameZh = nameZh;
        this.packageName = packageName;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

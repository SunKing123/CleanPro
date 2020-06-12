package com.xiaoniu.cleanking.bean.path;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author zhengzhihao
 * @date 2020/6/2 18
 * @mail：zhengzhihao@hellogeek.com
 */
@Entity(tableName = "applist")
public class AppPath {

    @NonNull
    @PrimaryKey
    private String id;
    private String package_name;
    private String file_path;
    private int file_type;
    private int clean_type;


    public AppPath(String id, String package_name, String file_path, int file_type, int clean_type) {
        this.id = id;
        this.package_name = package_name;
        this.file_path = file_path;
        this.file_type = file_type;
        this.clean_type = clean_type;
    }

    /*public AppPath(String package_name, String file_path, int file_type, int clean_type) {
        this.package_name = package_name;
        this.file_path = file_path;
        this.file_type = file_type;
        this.clean_type = clean_type;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public int getClean_type() {
        return clean_type;
    }

    public void setClean_type(int clean_type) {
        this.clean_type = clean_type;
    }
}

package com.xiaoniu.cleanking.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author : lvdongdong
 * @since 2019/12/20
 */
@Entity
public class AppPackageNameListDB {

    /**
     * packageName : com.xunmeng.pinduoduo
     * index : 0
     * name : 拼多多
     */
    @Id(autoincrement = true)
    private Long id;
    private String packageName;
    private int index;
    private String name;
    private String time;
    @Generated(hash = 1652023735)
    public AppPackageNameListDB(Long id, String packageName, int index, String name,
            String time) {
        this.id = id;
        this.packageName = packageName;
        this.index = index;
        this.name = name;
        this.time = time;
    }
    @Generated(hash = 886929937)
    public AppPackageNameListDB() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}

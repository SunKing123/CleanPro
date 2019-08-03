package com.xiaoniu.cleanking.ui.main.bean;

/**
 * Created by lang.chen on 2019/8/1
 */
public class FileChildEntity {

    public String id;
    public String name;
    //文件路径
    public String path;
    //文件大小
    public long size;
    //是否已选择
    public boolean isSelect;

    //父类id，对应type
    public int parentId;


    @Override
    public String toString() {
        return "FileChildEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", isSelect=" + isSelect +
                '}';
    }
}

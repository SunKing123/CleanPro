package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lang.chen on 2019/8/1
 */
public class FileTitleEntity extends BaseEntity {


    public String id;
    public String title;
    //标题类型 ,一般根据position来判断
    public int type;
    public long size;
    public boolean isExpand;
    public boolean isSelect;

    //二级菜单
    public List<FileChildEntity> lists = new ArrayList<>();



    public static  FileTitleEntity copyObject(String id,String title,int type, long size,boolean isExpand,boolean isSelect){
        FileTitleEntity fileTitleEntity=new FileTitleEntity();
        fileTitleEntity.id=id;
        fileTitleEntity.title=title;
        fileTitleEntity.type=type;
        fileTitleEntity.size=size;
        fileTitleEntity.isExpand=isExpand;
        fileTitleEntity.isSelect=isSelect;
        return  fileTitleEntity;
    }
    public interface Type {

        //缩略图
        int TH = 0;
        //今天
        int TODAY = 1;
        //昨天
        int YESTERDAY = 2;
        //当月
        int MONTH = 3;
        //半年
        int YEAR_HALF = 4;
    }
}

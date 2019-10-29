package com.xiaoniu.cleanking.ui.main.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by lang.chen on 2019/7/8
 */
public class  FileUploadInfoBean extends BaseEntity {


    public ImgUrl data=new ImgUrl();



    public  static  class  ImgUrl implements Serializable{

        public String url;
    }
}

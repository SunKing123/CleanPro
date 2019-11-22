package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsItemInfo implements Serializable {
//    public String pk = "";
//    public String hotnews = "";
    public String type = "";
    public String topic = "";
    public String date = "";
    public String url = "";
    public String source = "";
    public int miniimg_size;
    public String rowkey = "";
    public boolean isAd = false;
    public ArrayList<NewsPicInfo> miniimg;
}

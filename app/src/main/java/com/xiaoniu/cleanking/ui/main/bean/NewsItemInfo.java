package com.xiaoniu.cleanking.ui.main.bean;

import android.view.View;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<NewsPicInfo> miniimg;
}

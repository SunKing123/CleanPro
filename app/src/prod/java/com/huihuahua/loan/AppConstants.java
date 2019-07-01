package com.xiaoniu.cleanking;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;

/**
 * Created by admin on 2017/8/14.
 */

public class AppConstants {
    //生产环境
    public static String Base_Host = "https://fddapi.fdd188.com";
    public static String Base_Image_Host = "http://fddimage.51huihuahua.com";//图片路径
    public static String Base_H5_Host = "https://fddh5.fdd188.com";//H5路径
    public static String Base_H5_Host2 = "http://fddh5.51huihuahua.com";//H5路径
    public static String Map_Https = "http://fddh5.51huihuahua.com";//地图路径
    public static String Base_Big_Data = "http://dataprobe.xiaoniu.com";//大数据接口路径
    public static String GuanJia_Host1 = "http://user.mydkguanjia.com";//贷款管家接口域名
    public static String GuanJia_Host2 = "http://mall.mydkguanjia.com";//贷款管家接口域名
    public static String Action_Base_Host = "https://hhh.51huihuahua.com";//活动接口域名
    public static String Base_Host_Finance="https://financeapi.51huihuahua.com";//账务接口域名
    public static String AGGREMENT_H5_HOST = "https://h5.51huihuahua.com"; //H5协议域名

    //埋点sdk接口
    public static final String Bury_Base_Host = "http://aidataprobe.51huihuahua.com/apis/v1/dataprobe/";

    public static final int miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;//微信小程序生产环境
    //是否debug模式
    public static final boolean DEBUG = false;
}

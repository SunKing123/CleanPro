package com.installment.mall;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;

/**
 * Created by admin on 2017/8/14.
 */

public class AppConstants {
    //开发环境服务器
    public static String Base_Host = "http://devfdd.51huihuahua.com";
    public static String Base_Host_Finance = "http://devfdd.51huihuahua.com";//账务接口域名
    public static String Base_Image_Host = "http://testfddimage.51huihuahua.com";//图片路径
    public static String Base_H5_Host = "https://devfddh5.51huihuahua.com";//H5路径
    public static String Base_H5_Host2 = "http://devfddh5.51huihuahua.com";//H5路径
    public static String Map_Https = "https://devfddh5.51huihuahua.com";//地图路径
    public static String Base_Big_Data = "http://testhhh.xnshandai.net";//大数据接口路径
    public static String GuanJia_Host1 = "http://testuser.mydkguanjia.com";//贷款管家接口域名
    public static String GuanJia_Host2 = "http://testloan.mydkguanjia.com";//贷款管家接口域名
    public static String Action_Base_Host = "http://testosplatform.51huihuahua.com";//活动接口域名
    public static String AGGREMENT_H5_HOST = "http://testh5.51huihuahua.com";//相关协议域名

    public static String OSS_ACCESS_KEY_ID = "LTAIWVBu27n5Vw0d";
    public static String OSS_ACCESS_KEY_SECRET = "18i7N4uSuLMlICX3Lk7r98uVOWPz4b";
    public static String OSS_END_POINT = "http://oss-cn-shanghai.aliyuncs.com";
    public static String OSS_BUCKET_NAME = "fdd-oss";

    //埋点sdk接口
    public static final String Bury_Base_Host = "http://testaidataprobe.51huihuahua.com/apis/v1/dataprobe/";
    //是否debug模式
    public static final boolean DEBUG = true;

    public static final int miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST;//微信小程序测试环境
}
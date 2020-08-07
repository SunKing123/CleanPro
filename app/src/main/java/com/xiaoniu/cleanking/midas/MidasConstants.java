package com.xiaoniu.cleanking.midas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhengzhihao
 * @date 2020/7/3 13
 * @mail：zhengzhihao@hellogeek.com
 */
public interface MidasConstants {

    /**
     * Midas业务线应用ID
     */
    String APP_ID = "181001";

    /**
     * 业务线ID
     */
    String PRODUCT_ID = "181";

//    have done
    /**
     * 冷启动-开屏
     */
    String SP_CODE_START_ID = "adpos_2851353101";












    /**
     * 首页内部插屏广告ID
     */
    String MAIN_INSIDE_SCREEN_ID = "adpos_5659620321";

    /**
     * 首页模版插屏广告(功能完成页，点击左上角返回按钮时，展示模板插屏)
     */
    String MAIN_FINISH_PAGE_BACK = "adpos_4237081021";


    /**
     * 完成页插屏广告ID
     */
    String FINISH_INSIDE_SCREEN_ID = "adpos_8769830121";

    /**
     * 首页广告位1 ID
     */
    String MAIN_ONE_AD_ID = "adpos_3925669851";

    /**
     * 首页广告位2 ID
     */
    String MAIN_TWO_AD_ID = "adpos_3109552951";

    /**
     * 首页广告位3 ID
     */
    String MAIN_THREE_AD_ID = "adpos_5761437951";


    /**
     * 挽留弹窗广告 ID
     */
    String EXIT_RETAIN_ID = "adpos_8952152251";

    /**
     * 加速页面底部广告
     */

    String SPEED_BOTTOM_ID = "adpos_4094793551";

    /**
     * 我的页面底部广告
     */
    String ME_BOTTOM_ID = "adpos_9634429151";



    /**
     * 完成页面金币弹窗领取广告
     */
    String FINISH_GET_GOLD_COIN = "adpos_4210290151";

    /**
     * 点击领取双倍金币按钮时的激励视频
     */
    String CLICK_GET_DOUBLE_COIN_BUTTON = "adpos_9076480841";

    /***
     * 翻倍领取金币成功页面
     */
    String GET_DOUBLE_GOLD_COIN_SUCCESS = "adpos_7576129651";

    /**
     * 红包广告ID
     */
    String RED_PACKET = "adpos_7710425041";

    /**
     * 完成页_头部-信息流模板
     */
    String FINISH01_TOP_FEEED_ID = "adpos_9504039951";

    /**
     * 完成页_中间-左文右图模板广告
     */
    String FINISH01_CENTER_FEEED_ID = "adpos_5609031951";


    /**
     * 完成页02-信息流模板
     */
    String FINISH02_FEEED_ID = "adpos_9065892151";

    /**
     * 锁屏-信息流模板
     */
    String LOCK_PAGE_FEED_ID = "adpos_1531089251";

    /**
     * 首页金币-左上-模板
     */
    String HOME_PAGE_LFTOP_GOLD_01 = "adpos_2731393451";


    /**
     * 首页金币-左上-激励视频
     */
    String HOME_PAGE_LFTOP_GOLD_02 = "adpos_7147998041";

    /**
     * 首页金币-左上-模板
     */
    String HOME_PAGE_LFTOP_GOLD_03 = "adpos_7526694551";


    List<String> HOMELTTOP_LIST = new ArrayList<String>(Arrays.asList(new String[]{HOME_PAGE_LFTOP_GOLD_01, HOME_PAGE_LFTOP_GOLD_02, HOME_PAGE_LFTOP_GOLD_03}));


    /**
     * 首页金币-右上-模板
     */
    String HOME_PAGE_RTTOP_GOLD_01 = "adpos_6859891951";


    /**
     * 首页金币-右上-激励视频
     */
    String HOME_PAGE_RTTOP_GOLD_02 = "adpos_9419610341";

    /**
     * 首页金币-右上-模板
     */
    String HOME_PAGE_RTTOP_GOLD_03 = "adpos_3592791451";

    List<String> HOMERTTOP_LIST = new ArrayList<String>(Arrays.asList(new String[]{HOME_PAGE_RTTOP_GOLD_01, HOME_PAGE_RTTOP_GOLD_02, HOME_PAGE_RTTOP_GOLD_03}));

    /**
     * 首页金币-左下-模板
     */
    String HOME_PAGE_LFBOTTOM_GOLD_01 = "adpos_5841153051";


    /**
     * 首页金币-右上-激励视频
     */
    String HOME_PAGE_LFBOTTOM_GOLD_02 = "adpos_3829083041";

    /**
     * 首页金币-右上-模板
     */
    String HOME_PAGE_LFBOTTOM_GOLD_03 = "adpos_1561862051";

    List<String> HOMELFBOTTOM_LIST = new ArrayList<String>(Arrays.asList(new String[]{HOME_PAGE_LFBOTTOM_GOLD_01, HOME_PAGE_LFBOTTOM_GOLD_02, HOME_PAGE_LFBOTTOM_GOLD_03}));

    /**
     * 首页金币-左下-模板
     */
    String HOME_PAGE_RTBOTTOM_GOLD_01 = "adpos_9321897351";

    /**
     * 首页金币-右上-激励视频
     */
    String HOME_PAGE_RTLFBOTTOM_GOLD_02 = "adpos_4476822141";

    /**
     * 首页金币-右上-模板
     */
    String HOME_PAGE_RTLFBOTTOM_GOLD_03 = "adpos_7546373351";
    /**
     * 首页金币-右上
     */
    List<String> HOMERTBOTTOM_LIST = new ArrayList<String>(Arrays.asList(new String[]{HOME_PAGE_RTBOTTOM_GOLD_01, HOME_PAGE_RTLFBOTTOM_GOLD_02, HOME_PAGE_RTLFBOTTOM_GOLD_03}));
    /**
     * 解锁或按home键10秒后在桌面显示的插屏广告
     */
    String SCREEN_ON_ID = "adpos_9164081851";


    /**
     * 天气预报页面信息流模板；
     */
    String WEATHER_VIDEO_PAGE_BELOW = "adpos_9164081851";





}

package com.xiaoniu.cleanking.ui.newclean.util;


import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.qq.e.comm.constants.AdPatternType;
import com.xiaoniu.cleanking.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: rongfeng
 * @CreateDate: 2019/7/19 16:00
 */
public class ADUtils {
    //冷启动闪屏页开屏广告-穿山甲
    public final static String CODEID_COLDSTART_CSJ = "887289469";
    //热启动闪屏页开屏广告-穿山甲
    public final static String CODEID_HOTSTART_CSJ = "887289470";


    public static String adSource = "幸运金币";

    public final static String SUCCESS_CODE = "20000";

    //看看列表广告位
    public final static String VIDIO_LIST_ID = "5";

    //看看详情页
    public final static String VIDIO_DETAIL_ID = "6";
    //个人中心
    public final static String USERCENTER_AD_ID = "11";



    /**
     * 获取list指定位置左右两边最近的数据
     *
     * @param list
     * @param position
     * @param size
     * @return
     */
    public static List getBetweenList(List list, int position, int size) {
        List list2 = new ArrayList<>();
        if (list.size() > size) {
            list2.add(list.get(position));
            for (int i = 1; i < size && list2.size() < size; i++) {
                int left = position - i;
                if (left >= 0) {
                    list2.add(0, list.get(left));
                }
                int right = position + i;
                if (right < list.size()) {
                    list2.add(list.get(right));
                }
                if (left < 0) { //左边没数据了，补充右边
                    list2.add(list.get(right + 1));
                    i++;
                }
                if (right >= list.size()) {//右边没数据了，补充左边
                    list2.add(0, list.get(left - 1));
                    i++;
                }
            }
            return list2;
        } else {
            return list;
        }
    }


    /**
     * 取广告位type
     *
     * @param type
     * @return
     */
    public static String getAdType(int type) {
        if (type == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return "apk";
        } else if (type == TTAdConstant.INTERACTION_TYPE_BROWSER) {
            return "h5";
        } else if (type == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
            return "video";
        }
        return "h5";
    }

    /**
     * 取广点通广告位type
     *
     * @param type
     * @return
     */
    public static String getYLHAdType(int type) {
        if (type == AdPatternType.NATIVE_VIDEO) {
            return "video";
        }
        return "h5";
    }
    /**
     * 取广告位來源
     *
     * @return
     */
    public static int getAdSource(String key) {
        int newKey = NumberUtils.getInteger(key) + 100;
//        //TODO
//        AdsConfigBean adsConfigBean = AppApplication.getInstance().getAdsConfigBean();
//        if (null != adsConfigBean) {
//            HashMap<String, AdsConfigBean.DataBean> data = AppApplication.getInstance().getAdsConfigBean().getData();
//            if (data != null) {
//                AdsConfigBean.DataBean dataBean = data.get(newKey+"");
//                if (dataBean != null) {
//                    return dataBean.getSource();
//                }
//            }
//
//        }
        return 0;
    }
    /**
     * @param key
     * @return
     */
    public static String getCodeId(String key) {
        int newKey = NumberUtils.getInteger(key) + 100;
//        AdsConfigBean adsConfigBean = AppApplication.getInstance().getAdsConfigBean();
//        if (null != adsConfigBean) {
//            HashMap<String, AdsConfigBean.DataBean> data = AppApplication.getInstance().getAdsConfigBean().getData();
//            if (data != null) {
//                AdsConfigBean.DataBean dataBean = data.get(newKey+"");
//                if (dataBean != null) {
//                    return dataBean.getCodeId();
//                }
//            }
//        }
        return "";
    }
}

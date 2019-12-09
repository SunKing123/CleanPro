package com.comm.jksdk.cache;

import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.utils.CollectionUtils;

import java.util.Map;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.cache
 * @ClassName: CacheAd
 * @Description: java类作用描述
 * @Author: fanhailong
 * @CreateDate: 2019/11/30 17:33
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/30 17:33
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CacheAd {
    public static Map<String, AdInfo> adMap = CollectionUtils.createMap();

    public static void setAd(String position, AdInfo adInfo){
        adMap.put(position, adInfo);
    }

    public static AdInfo getAd(String position){
        return adMap.get(position);
    }

    public static void removeAd(String position) {
        adMap.remove(position);
    }

    public static void setAdByDisk(String position){

    }
}

package com.comm.jksdk.ad.listener;

import android.app.Activity;

import com.comm.jksdk.ad.entity.AdInfo;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.listener
 * @ClassName: AdRequestManager
 * @Description: 广告请求逻辑接口
 * @Author: fanhailong
 * @CreateDate: 2019/12/2 18:17
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/2 18:17
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface AdRequestManager {
    void requestAd(Activity activity, AdInfo adInfo, AdRequestListener listener);

    void cacheImg(String... url);
}

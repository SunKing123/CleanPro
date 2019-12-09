package com.comm.jksdk.ad.listener;

import com.comm.jksdk.ad.entity.AdInfo;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.listener
 * @ClassName: AdRequestListener
 * @Description: 广告请求回调
 * @Author: fanhailong
 * @CreateDate: 2019/12/2 19:40
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/2 19:40
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface AdRequestListener {

    /**
     * 广告请求成功
     */
    void adSuccess(AdInfo info);
    /**
     * 请求广告失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(AdInfo info, int errorCode, String errorMsg);
}

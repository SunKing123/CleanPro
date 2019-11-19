package com.comm.jksdk.ad.factory;

import com.comm.jksdk.ad.admanager.NativeAdManger;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.Provider;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.factory
 * @ClassName: NativeManagerFactory
 * @Description: java类作用描述
 * @Author: fanhailong
 * @CreateDate: 2019/11/11 19:18
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/11 19:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class NativeManagerFactory implements Provider {
    @Override
    public AdManager produce() {
        return new NativeAdManger();
    }
}

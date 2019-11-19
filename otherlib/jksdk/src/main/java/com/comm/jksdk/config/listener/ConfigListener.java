package com.comm.jksdk.config.listener;

import com.comm.jksdk.bean.ConfigBean;

import java.util.List;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.config.listener
 * @ClassName: ConfigListener
 * @Description: 请求广告配置信息回调
 * @Author: fanhailong
 * @CreateDate: 2019/11/11 17:56
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/11 17:56
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ConfigListener {

    /**
     * 配置请求成功
     */
    void adSuccess(List<ConfigBean.AdListBean> configList);

    /**
     * 配置请求失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(int errorCode, String errorMsg);
}

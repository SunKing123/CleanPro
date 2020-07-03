package com.xiaoniu.cleanking.midas;

/**
 * Desc: AdCustomerTemplateView的事件监听
 * <p>
 * Author: WuCongYi
 * Date: 2020/5/20
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * 构建配置参见:
 *
 * @author wucongyi
 */
public interface AdRepeatShowListener {

    /**
     * 广告view重复曝光
     *
     * @param params 广告请求参数
     */
    default void onAdViewRepeatShow(AdRequestParams params) {

    }

}

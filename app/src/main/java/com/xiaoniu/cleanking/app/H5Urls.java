package com.xiaoniu.cleanking.app;

import com.xiaoniu.cleanking.BuildConfig;

/**
 * Desc:H5链接地址
 * <p>
 * Author: AnYaBo
 * Date: 2019/9/20
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 *
 * @author anyabo
 */
public interface H5Urls {

    long timeFlag = System.currentTimeMillis() / 1000000;

    /**
     * 闲玩url协议
     */
    String XIANWAN_URL = "https://h5.17xianwan.com";
    /**
     * 9.9电商url
     */
    String DIANSHANG_URL = "http://aibuxing.xjjifen.com";

    /**
     * 钱包页面
     */
    String WALLET_URL = BuildConfig.Base_H5_Host + "/html/wallet/wallet.html?" + timeFlag;
    /**
     * 钱包页面
     */
    String WITHDRAWAL_URL = BuildConfig.Base_H5_Host + "/html/wallet/withdrawal.html?" + timeFlag;

    /**
     * 邀请好友
     */
    String INVITATION_URL = BuildConfig.Base_H5_Host + "/html/activitiesHtml/invitationActivities/index.html?" + timeFlag;

    /**
     * 隐私条款页面
     */
    String PRIVACY_CLAUSE_URL = BuildConfig.Base_H5_Host + "/html/policy/privacy.html?" + timeFlag;

    /**
     * 服务协议页面
     */
    String SERVICE_AGREEMENT_URL = BuildConfig.Base_H5_Host + "/html/policy/service.html?" + timeFlag;

}

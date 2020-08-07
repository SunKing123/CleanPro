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
     * 钱包页面
     */
//    String WALLET_URL = "http://192.168.85.61:9999" + "/html/wallet/wallet.html?" + timeFlag;
    String WALLET_URL = BuildConfig.Base_H5_Host_300 + "/html/wallet/wallet.html?" + timeFlag;
    /**
     * 钱包页面
     */
//    String WITHDRAWAL_URL = "http://192.168.85.61:9999" + "/html/wallet/withdrawal.html?" + timeFlag;
    String WITHDRAWAL_URL = BuildConfig.Base_H5_Host_300 + "/html/wallet/withdrawal.html?" + timeFlag;
    /**
     * 刮刮卡页面
     * 刘帅帅 域名 http://192.168.85.61:9999/html/wallet/withdrawal.html?
     */
//    String SCRATCHCARDS_URL = "http://192.168.85.61:9999/html/activitiesHtml/scratchCards/cardList2.html?" + timeFlag;
    String SCRATCHCARDS_URL = BuildConfig.Base_H5_Host_300 + "/html/activitiesHtml/scratchCards/cardList2.html?" + timeFlag;
    /**
     * 刮刮卡详情页面
     */
//    String SCRATCHCARDS_DETAIL_URL = "http://192.168.85.61:9999/html/activitiesHtml/scratchCards/scratch.html?";
    String SCRATCHCARDS_DETAIL_URL = BuildConfig.Base_H5_Host_300 + "/html/activitiesHtml/scratchCards/scratch.html?";

    /**
     * 邀请好友
     */
    String INVITATION_URL = BuildConfig.Base_H5_Host + "/html/activitiesHtml/invitationActivities/index.html?" + timeFlag;

    /**
     * 隐私条款页面
     */
    String PRIVACY_CLAUSE_URL = BuildConfig.Base_H5_Host_300 + "/html/agreement/privacy.html?" + timeFlag;

    /**
     * 用户协议页面
     */
    String USER_AGREEMENT_URL = BuildConfig.Base_H5_Host + "/userAgreement.html?" + timeFlag;

}

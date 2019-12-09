package com.comm.jksdk.ad.listener;

import com.comm.jksdk.ad.entity.AdInfo;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:     广告回调接口，给到业务线
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */
public interface AdListener {

    /**
     * 广告请求成功
     */
    void adSuccess(AdInfo info);

    /**
     * 广告展示
     */
    void adExposed(AdInfo info);

    /**
     * 广告点击
     */
    void adClicked(AdInfo info);

    /**
     * 广告关闭
     * @param info
     */
    default void adClose(AdInfo info){

    }

    /**
     * 广告失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(AdInfo info, int errorCode, String errorMsg);


}

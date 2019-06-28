package com.installment.mall.callback;

/**
 * Banner条目点击回调接口
 * Created by tie on 2017/2/27.
 */

public interface OnClickBannerListener {

    void itemClick(String url, String circleColumnName,String activityId,int position);
}

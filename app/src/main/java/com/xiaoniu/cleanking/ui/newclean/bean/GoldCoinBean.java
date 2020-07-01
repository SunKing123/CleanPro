package com.xiaoniu.cleanking.ui.newclean.bean;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe: 领取奖励弹窗的参数
 */
public class GoldCoinBean {
    @NonNull
    public Context context;
    //dialog的类型 1 转圈  2 撒花 3 清理金币奖励 默认是1
    public int dialogType;
    public String formType;
    public String ballPosition;
    public int pageId;
    public String adId;
    public int videoSource;
    public int obtainCoinCount;
    public double totalCoinCount;
    public boolean fbTip;
    public boolean isDouble;
    public String doubleAdCode;
}

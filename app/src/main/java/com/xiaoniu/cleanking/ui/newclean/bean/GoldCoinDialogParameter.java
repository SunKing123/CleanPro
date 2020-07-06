package com.xiaoniu.cleanking.ui.newclean.bean;

import android.app.Activity;
import android.view.View;

import com.xnad.sdk.ad.listener.AbsAdCallBack;

import androidx.annotation.NonNull;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe: 领取奖励弹窗的参数
 */
public class GoldCoinDialogParameter {

    //刮刮卡页面
    public static final int FROM_SCRATCH_CARD = 1;
    //清理完成页
    public static final int FROM_FINISH_COMPLETE = 2;

    //dialog的类型 1 转圈  2 撒花 3 清理金币奖励 默认是1
    public int dialogType;
    public int obtainCoinCount;
    public double totalCoinCount;
    public String adId;
    public boolean fbTip;
    public boolean isDouble;
    //金币来源
    public int fromType;
    @NonNull
    public Activity context;
    //翻倍按钮点击
    public View.OnClickListener onDoubleClickListener;
    //广告回调
    public AbsAdCallBack advCallBack;
}

package com.xiaoniu.cleanking.ui.main.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xinxiaolong on 2020/7/11.
 * email：xinxiaolong123@foxmail.com
 */
public class GoldCoinDoubleModel implements Parcelable {
    //当前页面信息
    public String currentPage;
    //翻倍金币领取的发放数量
    public int goldCoinsNum;
    //广告id
    public String adId;
    //下标值
    public int position;
    //功能名称
    public String functionName = "";

    public GoldCoinDoubleModel(String adId, int goldCoinsNum, int position, String currentPage) {
        this.adId = adId;
        this.goldCoinsNum = goldCoinsNum;
        this.currentPage = currentPage;
        this.position = position;
    }

    public GoldCoinDoubleModel(String adId, int goldCoinsNum, String currentPage, String functionName) {
        this.adId = adId;
        this.goldCoinsNum = goldCoinsNum;
        this.currentPage = currentPage;
        this.functionName = functionName;
    }

    protected GoldCoinDoubleModel(Parcel in) {
        currentPage = in.readString();
        goldCoinsNum = in.readInt();
        adId = in.readString();
        position = in.readInt();
        functionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentPage);
        dest.writeInt(goldCoinsNum);
        dest.writeString(adId);
        dest.writeInt(position);
        dest.writeString(functionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoldCoinDoubleModel> CREATOR = new Creator<GoldCoinDoubleModel>() {
        @Override
        public GoldCoinDoubleModel createFromParcel(Parcel in) {
            return new GoldCoinDoubleModel(in);
        }

        @Override
        public GoldCoinDoubleModel[] newArray(int size) {
            return new GoldCoinDoubleModel[size];
        }
    };
}

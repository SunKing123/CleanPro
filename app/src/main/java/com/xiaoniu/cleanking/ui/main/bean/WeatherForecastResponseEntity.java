package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author xiangzhenbiao
 * @since 2019/9/3 11:11
 */
public class WeatherForecastResponseEntity implements Parcelable {

    /**
     * bizCode : tianqi
     * kid : 3
     * description : 本期节目主要内容：江南强降雨趋于减弱；华北黄淮高温相对较多；城市天气预报。（《午间天气预报》 20190715）
     * updateTime : 2019-07-15T10:28:58.000+0000
     * title : 《午间天气预报》 20190715
     * synchronizedTime : 1563186538
     * url : tianqi/video/15a0e54969b924874767ef0ab5aa6695.mp4
     * showFlag : 0
     * synchronizedAt : 2019-07-15
     * duration : 1:54
     * createTime : 2019-07-15T10:28:58.000+0000
     * coverImageUrl : tianqi/img/2019-07-15/tianqi/1d374bee27cc533f31afb8cf21839541.jpg
     * id : be0c4bbbea9b44e0aa02f3eba1ee27a4
     */

    private String bizCode;
    private int kid;

    /**
     * 视频描述
     */
    private String description;
    private String updateTime;

    /**
     * 标题
     */
    private String title;
    private String synchronizedTime;

    /**
     * 视频文件地址
     */
    @SerializedName(value = "url")
    private String videoUrl;

    private String showFlag;

    /**
     * 更新日期
     */
    private String synchronizedAt;

    /**
     * 视频时长
     */
    private String duration;
    private String createTime;

    /**
     * 视频封面
     */
    @SerializedName(value = "coverImage")
    private String coverImageUrl;

    /**
     * 首页天气预报取图封面图编号,0:早间, 1:午间  2:晚间
     */
    private int picNo;

    private String id;


    private long responseTime;

    /**
     * 发布来源: 天气由中国气象局15:14权威发布
     */
    @SerializedName(value = "source")
    private String publishSource;

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public int getKid() {
        return kid;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynchronizedTime() {
        return synchronizedTime;
    }

    public void setSynchronizedTime(String synchronizedTime) {
        this.synchronizedTime = synchronizedTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(String showFlag) {
        this.showFlag = showFlag;
    }

    public String getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(String synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public int getPicNo() {
        return picNo;
    }

    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getPublishSource() {
        return publishSource;
    }

    public void setPublishSource(String publishSource) {
        this.publishSource = publishSource;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bizCode);
        dest.writeInt(this.kid);
        dest.writeString(this.description);
        dest.writeString(this.updateTime);
        dest.writeString(this.title);
        dest.writeString(this.synchronizedTime);
        dest.writeString(this.videoUrl);
        dest.writeString(this.showFlag);
        dest.writeString(this.synchronizedAt);
        dest.writeString(this.duration);
        dest.writeString(this.createTime);
        dest.writeString(this.coverImageUrl);
        dest.writeInt(this.picNo);
        dest.writeString(this.id);
        dest.writeLong(this.responseTime);
        dest.writeString(this.publishSource);
    }

    public WeatherForecastResponseEntity() {
    }

    protected WeatherForecastResponseEntity(Parcel in) {
        this.bizCode = in.readString();
        this.kid = in.readInt();
        this.description = in.readString();
        this.updateTime = in.readString();
        this.title = in.readString();
        this.synchronizedTime = in.readString();
        this.videoUrl = in.readString();
        this.showFlag = in.readString();
        this.synchronizedAt = in.readString();
        this.duration = in.readString();
        this.createTime = in.readString();
        this.coverImageUrl = in.readString();
        this.picNo = in.readInt();
        this.id = in.readString();
        this.responseTime = in.readLong();
        this.publishSource = in.readString();
    }

    public static final Parcelable.Creator<WeatherForecastResponseEntity> CREATOR = new Parcelable.Creator<WeatherForecastResponseEntity>() {
        @Override
        public WeatherForecastResponseEntity createFromParcel(Parcel source) {
            return new WeatherForecastResponseEntity(source);
        }

        @Override
        public WeatherForecastResponseEntity[] newArray(int size) {
            return new WeatherForecastResponseEntity[size];
        }
    };
}

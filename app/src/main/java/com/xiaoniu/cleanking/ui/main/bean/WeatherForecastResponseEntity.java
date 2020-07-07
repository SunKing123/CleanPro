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

    /**
     * 发布来源: 天气由中国气象局15:14权威发布
     */
    @SerializedName(value = "source")
    private String publishSource;


    protected WeatherForecastResponseEntity(Parcel in) {
        bizCode = in.readString();
        kid = in.readInt();
        description = in.readString();
        updateTime = in.readString();
        title = in.readString();
        synchronizedTime = in.readString();
        videoUrl = in.readString();
        showFlag = in.readString();
        synchronizedAt = in.readString();
        duration = in.readString();
        createTime = in.readString();
        coverImageUrl = in.readString();
        picNo = in.readInt();
        id = in.readString();
        publishSource = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bizCode);
        dest.writeInt(kid);
        dest.writeString(description);
        dest.writeString(updateTime);
        dest.writeString(title);
        dest.writeString(synchronizedTime);
        dest.writeString(videoUrl);
        dest.writeString(showFlag);
        dest.writeString(synchronizedAt);
        dest.writeString(duration);
        dest.writeString(createTime);
        dest.writeString(coverImageUrl);
        dest.writeInt(picNo);
        dest.writeString(id);
        dest.writeString(publishSource);
    }

    public static final Creator<WeatherForecastResponseEntity> CREATOR = new Creator<WeatherForecastResponseEntity>() {
        @Override
        public WeatherForecastResponseEntity createFromParcel(Parcel in) {
            return new WeatherForecastResponseEntity(in);
        }

        @Override
        public WeatherForecastResponseEntity[] newArray(int size) {
            return new WeatherForecastResponseEntity[size];
        }
    };

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublishSource() {
        return publishSource;
    }

    public void setPublishSource(String publishSource) {
        this.publishSource = publishSource;
    }

    public int getPicNo() {
        return picNo;
    }

    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }



}

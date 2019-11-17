/*
package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.geek.jk.weather.utils.G;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

*/
/**
 * @author xiangzhenbiao
 * @since 2019/4/11 21:34
 *//*

@Entity(nameInDb = "XNWeatherCityModel", createInDb = false)
public class WeatherCity implements Parcelable ,Comparable<WeatherCity>{

    @Id
    @Property(nameInDb = "id")
    private Long id;

    //国家
    @Property(nameInDb = "country")
    private String country;

    //省份，如湖北省
    @Property(nameInDb = "province")
    private String province;

    //城市，如武汉市
    @Property(nameInDb = "city")
    private String city;

    //区、县
//    @Property(nameInDb = "county")
    @Property(nameInDb = "district")
    private String district;

    //乡、镇
//    private String town;

    //街道，如“申江路”
    @Transient
    private String street;

//    private Long cityId;

    //城市编码，非空，唯一
    @Property(nameInDb = "area_code")
    @NotNull
    @Unique
    private String areaCode;

    @Property(nameInDb = "longitude")
    private String longitude;

    @Property(nameInDb = "latitude")
    private String latitude;

//    private String pinyin_district;

//    private String recommendCity;
    @Property(nameInDb = "city_type")
    private int cityType;

    @Property(nameInDb = "pinyin_district")
    private String pinyinDistrict;

    @Property(nameInDb = "recommendCity")
    private int recommendCity;

    @Property(nameInDb = "isRecommend")
    private int isRecommend;

    @Property(nameInDb = "isSelected")
    private int isSelected;

    @Property(nameInDb = "isPositioning")
    private int isPositioning;

    @Property(nameInDb = "isDefalut")
    private int isDefalut;

    @Transient
    private String attentionTime;

    //0：热门城市，1：选择省份，2：国际城市
    @Transient
    public int cityShowType = 0;

    @Override
    public String toString() {
        return "WeatherCity{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", cityType=" + cityType +
                ", pinyinDistrict='" + pinyinDistrict + '\'' +
                ", recommendCity=" + recommendCity +
                ", isRecommend=" + isRecommend +
                ", isSelected=" + isSelected +
                ", isPositioning=" + isPositioning +
                ", isDefalut=" + isDefalut +
                ", attentionTime='" + attentionTime + '\'' +
                ", cityShowType=" + cityShowType +
                '}';
    }

    @Generated(hash = 1215391175)
    public WeatherCity(Long id, String country, String province, String city,
            String district, @NotNull String areaCode, String longitude,
            String latitude, int cityType, String pinyinDistrict, int recommendCity,
            int isRecommend, int isSelected, int isPositioning, int isDefalut) {
        this.id = id;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.areaCode = areaCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cityType = cityType;
        this.pinyinDistrict = pinyinDistrict;
        this.recommendCity = recommendCity;
        this.isRecommend = isRecommend;
        this.isSelected = isSelected;
        this.isPositioning = isPositioning;
        this.isDefalut = isDefalut;
    }

    @Generated(hash = 187543502)
    public WeatherCity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getCityType() {
        return this.cityType;
    }

    public void setCityType(int cityType) {
        this.cityType = cityType;
    }

    public String getPinyinDistrict() {
        return this.pinyinDistrict;
    }

    public void setPinyinDistrict(String pinyinDistrict) {
        this.pinyinDistrict = pinyinDistrict;
    }

    public int getRecommendCity() {
        return this.recommendCity;
    }

    public void setRecommendCity(int recommendCity) {
        this.recommendCity = recommendCity;
    }

    public int getIsRecommend() {
        return this.isRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        this.isRecommend = isRecommend;
    }

    public int getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getIsPositioning() {
        return this.isPositioning;
    }

    public void setIsPositioning(int isPositioning) {
        this.isPositioning = isPositioning;
    }

    public int getIsDefalut() {
        return this.isDefalut;
    }

    public void setIsDefalut(int isDefalut) {
        this.isDefalut = isDefalut;
    }

    public String getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(String attentionTime) {
        this.attentionTime = attentionTime;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
      WeatherCity a= (WeatherCity) o;
      return !G.isEmpty(this.getAreaCode())&&this.getAreaCode().equals(a.getAreaCode());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.country);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.areaCode);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeInt(this.cityType);
        dest.writeString(this.pinyinDistrict);
        dest.writeInt(this.recommendCity);
        dest.writeInt(this.isRecommend);
        dest.writeInt(this.isSelected);
        dest.writeInt(this.isPositioning);
        dest.writeInt(this.isDefalut);
        dest.writeInt(this.cityShowType);
        dest.writeString(this.attentionTime);
        dest.writeString(this.street);
    }

    protected WeatherCity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.country = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.areaCode = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.cityType = in.readInt();
        this.pinyinDistrict = in.readString();
        this.recommendCity = in.readInt();
        this.isRecommend = in.readInt();
        this.isSelected = in.readInt();
        this.isPositioning = in.readInt();
        this.isDefalut = in.readInt();
        this.cityShowType = in.readInt();
        this.attentionTime = in.readString();
        this.street = in.readString();
    }

    public static final Creator<WeatherCity> CREATOR = new Creator<WeatherCity>() {
        @Override
        public WeatherCity createFromParcel(Parcel source) {
            return new WeatherCity(source);
        }

        @Override
        public WeatherCity[] newArray(int size) {
            return new WeatherCity[size];
        }
    };

    @Override
    public int compareTo(WeatherCity o) {
        if(this != null && o != null){
            if(o.isPositioning - this.isPositioning == 0){
                if(!TextUtils.isEmpty(o.attentionTime) && !TextUtils.isEmpty(this.attentionTime)){
                    return o.attentionTime.compareTo(this.attentionTime);
                }
                return 1;
            }else{
                return o.isPositioning - this.isPositioning;
            }
        }
        return 0;
    }
}
*/

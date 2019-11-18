package com.xiaoniu.cleanking.ui.main.bean;

/**
 * @author xiangzhenbiao
 * @since 2019/4/18 22:18
 */
public class LocationCityInfo {

    private String longitude;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    //国家
    private String country;

    //省份，如湖北省
    private String province;

    //城市，如武汉市
    private String city;

    //区、县
//    @Property(nameInDb = "county")
    private String district;

    //街道，如“申江路”
    private String street;

    public LocationCityInfo(String province, String city, String district) {
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public LocationCityInfo(String country, String province, String city, String district) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public LocationCityInfo(String country, String province, String city, String district, String street) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "LocationCityInfo{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}

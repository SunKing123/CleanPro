package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

/**
 * @author xiangzhenbiao
 * @since 2019/4/18 22:18
 */
public class LocationCityInfo {

    private String longitude;

    private String latitude;

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

    /**
     * poi，如：上海集成电路科技馆
     */
    private String poiName;

    /**
     * 如：星创科技广场
     */
    private String aoiName;

    /**
     * 详细地址
     */
    private String address;

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

    public LocationCityInfo(String country, String province, String city, String district, String street, String poiName, String aoiName) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        this.poiName = poiName;
        this.aoiName = aoiName;
    }

    public LocationCityInfo(String longitude, String latitude, String country, String province, String city, String district, String street,
                            String poiName, String aoiName, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        this.poiName = poiName;
        this.aoiName = aoiName;
        this.address = address;
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

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                ", poiName='" + poiName + '\'' +
                ", aoiName='" + aoiName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

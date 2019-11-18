package com.xiaoniu.cleanking.ui.main.bean;

/**
 * @author zhengzhihao
 * @date 2019/11/18 09
 * @mail：zhengzhihao@hellogeek.com
 *
 * 定位信息
 */
public class LocationInfo {
    private String areaCode;
    private int permissionState = 1;

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

    private String longitude;

    private String latitude;


    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public int getPermissionState() {
        return permissionState;
    }

    public void setPermissionState(int permissionState) {
        this.permissionState = permissionState;
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
}

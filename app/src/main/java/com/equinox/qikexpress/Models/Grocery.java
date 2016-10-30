package com.equinox.qikexpress.Models;

/**
 * Created by mukht on 10/29/2016.
 */

public class Grocery {

    private Integer id, cityId, brandId;
    private String groceryName, profileImg, backImg;
    private Float latitude, longitude;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCityId() {
        return cityId;
    }
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
    public Integer getBrandId() {
        return brandId;
    }
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
    public String getGroceryName() {
        return groceryName;
    }
    public void setGroceryName(String groceryName) {
        this.groceryName = groceryName;
    }
    public String getProfileImg() {
        return profileImg;
    }
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
    public String getBackImg() {
        return backImg;
    }
    public void setBackImg(String backImg) {
        this.backImg = backImg;
    }
    public Float getLatitude() {
        return latitude;
    }
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }
    public Float getLongitude() {
        return longitude;
    }
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}

package com.equinox.qikexpress.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mukht on 10/30/2016.
 */

public class Place {

    private String placeId, name, vicinity;
    private LatLng location;
    private Boolean openNow, isPartner;
    private Photo photos;
    private String iconURL, gMapURL, webURL, profileImageURL;
    private Double totalRating;
    private List<RatingsManager> individualRatings;
    private String address, phoneNumber;
    private Periods periods;
    private Float distanceFromCurrent;
    private Integer timeFromCurrent;

    public Place mergePlace(Place addPlace) {
        if (vicinity == null)
            vicinity = addPlace.getVicinity();
        if (location == null)
            location = addPlace.getLocation();
        if (openNow == null)
            openNow = addPlace.getOpenNow();
        if (isPartner == null)
            isPartner = addPlace.getPartner();
        if (iconURL == null)
            iconURL = addPlace.getIconURL();
        if (gMapURL == null)
            gMapURL = addPlace.getgMapURL();
        if (webURL == null)
            webURL = addPlace.getWebURL();
        if (totalRating == null)
            totalRating = addPlace.getTotalRating();
        if (individualRatings == null)
            individualRatings = addPlace.getIndividualRatings();
        if (address == null)
            address = addPlace.getAddress();
        if (phoneNumber == null)
            phoneNumber = addPlace.getPhoneNumber();
        if (periods == null)
            periods = addPlace.getPeriods();
        if (distanceFromCurrent == null)
            distanceFromCurrent = addPlace.getDistanceFromCurrent();
        if (timeFromCurrent == null)
            timeFromCurrent = addPlace.getTimeFromCurrent();
        if (profileImageURL == null)
            profileImageURL = addPlace.getProfileImageURL();
        return this;
    }

    public void setDistanceFromCurrent(String distance) {
        if (distance.length()>4)
            this.distanceFromCurrent = Float.parseFloat(distance.substring(0, distance.length()-3));
    }
    public void setTimeFromCurrent(String time) {
        if (time.length()>4)
            this.timeFromCurrent = Integer.parseInt(time.substring(0, time.length()-4).replaceAll(" ",""));
    }
    public Float getDistanceFromCurrent() {   return distanceFromCurrent;  }
    public Integer getTimeFromCurrent() { return timeFromCurrent; }
    public String getIconURL() {
        return iconURL;
    }
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVicinity() {
        return vicinity;
    }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
    public LatLng getLocation() {
        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
    public Boolean getOpenNow() {
        return openNow;
    }
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }
    public Photo getPhoto() {
        return photos;
    }
    public void setPhoto(Photo photos) { this.photos = photos;  }
    public List<RatingsManager> getIndividualRatings() {
        return individualRatings;
    }
    public void setIndividualRatings(List<RatingsManager> individualRatings) {
        this.individualRatings = individualRatings;
    }
    public Double getTotalRating() {
        return totalRating;
    }
    public void setTotalRating(Double totalRating) {
        this.totalRating = totalRating;
    }
    public String getgMapURL() {
        return gMapURL;
    }
    public void setgMapURL(String gMapURL) {
        this.gMapURL = gMapURL;
    }
    public String getWebURL() {
        return webURL;
    }
    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Periods getPeriods() {
        return periods;
    }
    public void setPeriods(Periods periods) {
        this.periods = periods;
    }
    public Boolean getPartner() {
        return isPartner;
    }
    public void setPartner(Boolean partner) {
        isPartner = partner;
    }
    public String getProfileImageURL() {
        return profileImageURL;
    }
    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}

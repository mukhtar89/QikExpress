package com.equinox.qikexpress.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.ADDRESS;
import static com.equinox.qikexpress.Models.Constants.BUSINESS;
import static com.equinox.qikexpress.Models.Constants.BUSINESS_OUTLET;
import static com.equinox.qikexpress.Models.Constants.GMAP_URL;
import static com.equinox.qikexpress.Models.Constants.IS_PARTNER;
import static com.equinox.qikexpress.Models.Constants.PERM_LOCATION_LAT;
import static com.equinox.qikexpress.Models.Constants.PERM_LOCATION_LNG;
import static com.equinox.qikexpress.Models.Constants.NAME;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PROFILE_IMAGE;

/**
 * Created by mukht on 10/30/2016.
 */

public class Place {

    private String placeId, name, vicinity;
    private LatLng location;
    private Boolean openNow, isPartner;
    private Photo photos;
    private String iconURL, gMapURL, webURL;
    private Double totalRating;
    private List<RatingsManager> individualRatings;
    private String phoneNumber;
    private Periods periods;
    private Float distanceFromCurrent;
    private Integer timeFromCurrent;
    private GeoAddress address;
    private String brandName, brandImage;

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
        if (brandName == null)
            brandName = addPlace.getBrandName();
        if (brandImage == null)
            brandImage = addPlace.getBrandImage();
        if (periods == null)
            periods = addPlace.getPeriods();
        if (distanceFromCurrent == null)
            distanceFromCurrent = addPlace.getDistanceFromCurrent();
        if (timeFromCurrent == null)
            timeFromCurrent = addPlace.getTimeFromCurrent();
        return this;
    }

    public String getBasePath() {
        return address.getBasePath();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(PLACE_ID, placeId);
        result.put(NAME, name);
        result.put(PERM_LOCATION_LAT, location.latitude);
        result.put(PERM_LOCATION_LNG, location.longitude);
        result.put(IS_PARTNER, isPartner);
        result.put(GMAP_URL, gMapURL);
        result.put(ADDRESS, address.toMap());
        return result;
    }

    @Exclude
    public Place fromMap(Map<String,Object> entry) {
        placeId = (String) entry.get(PLACE_ID);
        name = (String) entry.get(NAME);
        location = new LatLng((Double) entry.get(PERM_LOCATION_LAT),(Double) entry.get(PERM_LOCATION_LNG));
        isPartner = (Boolean) entry.get(IS_PARTNER);
        gMapURL = (String) entry.get(GMAP_URL);
        address = new GeoAddress().fromMap((Map<String,Object>) entry.get(ADDRESS));
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
    public GeoAddress getAddress() {
        return address;
    }
    public void setAddress(GeoAddress address) {
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
    public String getBrandImage() {
        return brandImage;
    }
    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}

package com.equinox.qikexpress.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mukht on 10/30/2016.
 */

public class Place {

    private String placeId, name, vicinity;
    private LatLng location;
    private Boolean openNow, isPartner;
    private Photo photo;
    private Float distanceFromCurrent;
    private Integer timeFromCurrent;

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
    private String iconURL;
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
        return photo;
    }
    public void setPhoto(Photo photo) { this.photo = photo;  }
    public Boolean getPartner() {
        return isPartner;
    }
    public void setPartner(Boolean partner) {
        isPartner = partner;
    }
}

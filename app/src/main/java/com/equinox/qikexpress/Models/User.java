package com.equinox.qikexpress.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

import static com.equinox.qikexpress.Models.Constants.CURRENT_LOCATION_LAT;
import static com.equinox.qikexpress.Models.Constants.CURRENT_LOCATION_LNG;
import static com.equinox.qikexpress.Models.Constants.PERM_LOCATION_LAT;
import static com.equinox.qikexpress.Models.Constants.PERM_LOCATION_LNG;

/**
 * Created by mukht on 11/16/2016.
 */

public class User {

    private String id, name, email, photoURL, phone, localCurrency, localCurrencySymbol;
    private LatLng permLocation, currentLocation;
    private GeoAddress permAddress, currentAddress;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LatLng getPermLocation() {
        return permLocation;
    }
    public void setPermLocation(LatLng permLocation) {
        this.permLocation = permLocation;
    }
    public GeoAddress getPermAddress() {
        return permAddress;
    }
    public void setPermAddress(GeoAddress permAddress) {
        this.permAddress = permAddress;
    }
    public String getLocalCurrency() {
        return localCurrency;
    }
    public void setLocalCurrency(String localCurrency) {
        this.localCurrency = localCurrency;
    }
    public String getLocalCurrencySymbol() {
        return localCurrencySymbol;
    }
    public void setLocalCurrencySymbol(String localCurrencySymbol) {
        this.localCurrencySymbol = localCurrencySymbol;
    }
    public GeoAddress getCurrentAddress() {
        return currentAddress;
    }
    public void setCurrentAddress(GeoAddress currentAddress) {
        this.currentAddress = currentAddress;
    }
    public LatLng getCurrentLocation() {
        return currentLocation;
    }
    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Exclude
    public HashMap<String,Object> toMap() {
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("id",id);
        userMap.put("name",name);
        userMap.put("email",email);
        userMap.put("photoURL",photoURL);
        if (permLocation != null) {
            userMap.put(PERM_LOCATION_LAT, permLocation.latitude);
            userMap.put(PERM_LOCATION_LNG, permLocation.longitude);
        }
        userMap.put(CURRENT_LOCATION_LAT, currentLocation.latitude);
        userMap.put(CURRENT_LOCATION_LNG, currentLocation.longitude);
        if (permAddress != null) userMap.put("permAddress", permAddress.toMap());
        userMap.put("currentAddress", currentAddress.toMap());
        return userMap;
    }

    @Exclude
    public User fromMap(HashMap<String,Object> userMap) {
        id = (String) userMap.get("id");
        name = (String) userMap.get("name");
        email = (String) userMap.get("email");
        photoURL = (String) userMap.get("photoURL");
        if (userMap.containsKey("permLocation"))
            permLocation = new LatLng((Double) userMap.get(PERM_LOCATION_LAT), (Double) userMap.get(PERM_LOCATION_LNG));
        currentLocation = new LatLng((Double) userMap.get(CURRENT_LOCATION_LAT), (Double) userMap.get(CURRENT_LOCATION_LNG));
        if (userMap.containsKey("permAddress"))
            permAddress = new GeoAddress().fromMap((HashMap<String,Object>)userMap.get("permAddress"));
        currentAddress = new GeoAddress().fromMap((HashMap<String,Object>)userMap.get("currentAddress"));
        return this;
    }
}

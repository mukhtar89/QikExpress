package com.equinox.qikexpress.Models;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mukht on 11/16/2016.
 */

public class User {

    private String id, name, email, photoURL, phone, localCurrency, localCurrencySymbol, featuredAddress;
    private LatLng userLocation;
    private Address address;

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
    public LatLng getUserLocation() {
        return userLocation;
    }
    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
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
    public String getFeaturedAddress() {
        return featuredAddress;
    }
    public void setFeaturedAddress(String featuredAddress) {
        this.featuredAddress = featuredAddress;
    }

    @Exclude
    public HashMap<String,Object> toMap() {
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("id",id);
        userMap.put("name",name);
        userMap.put("email",email);
        userMap.put("photoURL",photoURL);
        userMap.put("userLocationLat",userLocation.latitude);
        userMap.put("userLocationLng",userLocation.longitude);
        userMap.put("featuredAddress",featuredAddress);
        return userMap;
    }

    @Exclude
    public User fromMap(HashMap<String,Object> userMap) {
        id = (String) userMap.get("id");
        name = (String) userMap.get("name");
        email = (String) userMap.get("email");
        photoURL = (String) userMap.get("photoURL");
        userLocation = new LatLng((Double) userMap.get("userLocationLat"), (Double) userMap.get("userLocationLng"));
        featuredAddress = (String) userMap.get("featuredAddress");
        return this;
    }
}

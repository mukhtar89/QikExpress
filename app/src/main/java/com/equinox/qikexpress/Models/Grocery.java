package com.equinox.qikexpress.Models;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Utils.LocationPermission;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mukht on 10/29/2016.
 */

public class Grocery extends Place {

    private Integer brandId;
    private String photoReference, profileImageURL;

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}

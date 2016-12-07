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

    private String photoReference, countryCode;

    public Grocery mergeGrocery(Grocery addGrocery){
        if (countryCode == null)
            countryCode = addGrocery.getCountryCode();
        if (photoReference == null)
            photoReference = addGrocery.getPhotoReference();

        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getPhotoReference() {
        return photoReference;
    }
    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }
}

package com.equinox.qikexpress.Models;

/**
 * Created by mukht on 10/29/2016.
 */

public class Restaurant extends Place {

    private String photoReference, countryCode;

    public Restaurant mergeRestaurant(Restaurant addRestaurant){
        if (countryCode == null)
            countryCode = addRestaurant.getCountryCode();
        if (photoReference == null)
            photoReference = addRestaurant.getPhotoReference();

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

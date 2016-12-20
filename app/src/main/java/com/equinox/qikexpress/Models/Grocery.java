package com.equinox.qikexpress.Models;

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

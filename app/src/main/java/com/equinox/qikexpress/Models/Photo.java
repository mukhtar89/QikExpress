package com.equinox.qikexpress.Models;

/**
 * Created by mukht on 10/30/2016.
 */
public class Photo {

    private Integer height, width;
    private String htmlAttributions, photoReference;

    public Photo(Integer width, Integer height, String htmlAttributions, String photoReference) {
        this.width = width;
        this.height = height;
        this.htmlAttributions = htmlAttributions;
        this.photoReference = photoReference;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public String getHtmlAttributions() {
        return htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public String returnApiUrl(String key) {
        String retURL = "https://maps.googleapis.com/maps/api/place/photo?";
        retURL += "maxwidth=" + width;
        retURL += "&photoreference=" + photoReference;
        retURL += "&key=" + key;
        return retURL;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHtmlAttributions(String htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }
}

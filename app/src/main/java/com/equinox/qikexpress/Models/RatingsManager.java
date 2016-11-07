package com.equinox.qikexpress.Models;

import java.util.ArrayList;

/**
 * Created by mukht on 11/2/2016.
 */

public class RatingsManager {

    private String authorName;
    private String authorPhotoURL;
    private Integer ratingValue;
    private String ratingReview;
    private Integer timeSubmitted;

    public RatingsManager(String authorName, String authorPhotoURL, Integer ratingValue, String ratingReview, Integer timeSubmitted) {
        this.authorName = authorName;
        this.authorPhotoURL = authorPhotoURL;
        this.ratingValue = ratingValue;
        this.ratingReview = ratingReview;
        this.timeSubmitted = timeSubmitted;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPhotoURL() {
        return authorPhotoURL;
    }

    public void setAuthorPhotoURL(String authorPhotoURL) {
        this.authorPhotoURL = authorPhotoURL;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingReview() {
        return ratingReview;
    }

    public void setRatingReview(String ratingReview) {
        this.ratingReview = ratingReview;
    }

    public Integer getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Integer timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }
}

package com.equinox.qikexpress.Models;

/**
 * Created by mukht on 11/7/2016.
 */

public class GroceryItemCart extends GroceryItem {

    private Boolean saveForLater;

    public Boolean getSaveForLater() {
        return saveForLater;
    }

    public void setSaveForLater(Boolean saveForLater) {
        this.saveForLater = saveForLater;
    }
}

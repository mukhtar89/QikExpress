package com.equinox.qikexpress.Models;

/**
 * Created by mukht on 11/7/2016.
 */

public class GroceryItemCart extends GroceryItem {

    private Integer itemQuantity;
    private Boolean saveForLater;


    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Boolean getSaveForLater() {
        return saveForLater;
    }

    public void setSaveForLater(Boolean saveForLater) {
        this.saveForLater = saveForLater;
    }
}

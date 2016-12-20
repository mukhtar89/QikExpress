package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.SAVE_FOR_LATER;

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

    @Exclude
    public Map<String, Object> toMapCart() {
        Map<String, Object> result = toMap();
        result.put(SAVE_FOR_LATER, saveForLater);
        return result;
    }
}

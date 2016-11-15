package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mukht on 11/9/2016.
 */

public class Item {

    private String placeId, placeName;
    private Integer itemId;
    private Integer itemQuantity;
    private Float itemPriceValue;
    private String itemName, itemImage;

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public Integer getItemId() {
        return itemId;
    }
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    public Float getItemPriceValue() {
        return itemPriceValue;
    }
    public void setItemPriceValue(Float itemPriceValue) {this.itemPriceValue = itemPriceValue;}
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {this.itemName = itemName;}
    public String getItemImage() {
        return itemImage;
    }
    public void setItemImage(String itemImage) {this.itemImage = itemImage;}
    public String getPlaceName() {
        return placeName;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public Integer getItemQuantity() {
        return itemQuantity;
    }
    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("placeId", placeId);
        result.put("itemId", itemId);
        result.put("itemPriceValue", itemPriceValue);
        result.put("itemName", itemName);
        result.put("itemImage", itemImage);
        result.put("placeName", placeName);
        result.put("itemQuantity", itemQuantity);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapCheckout() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemId", itemId);
        result.put("itemPriceValue", itemPriceValue);
        result.put("itemName", itemName);
        result.put("itemImage", itemImage);
        result.put("itemQuantity", itemQuantity);
        return result;
    }
}

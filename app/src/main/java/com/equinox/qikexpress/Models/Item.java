package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.ITEM_ID;
import static com.equinox.qikexpress.Models.Constants.ITEM_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_PRICE;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;

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
        result.put(PLACE_ID, placeId);
        result.put(ITEM_ID, itemId);
        result.put(ITEM_PRICE, itemPriceValue);
        result.put(ITEM_NAME, itemName);
        result.put(ITEM_IMAGE, itemImage);
        result.put(PLACE_NAME, placeName);
        result.put(ITEM_QTY, itemQuantity);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapCheckout() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ITEM_ID, itemId);
        result.put(ITEM_PRICE, itemPriceValue);
        result.put(ITEM_NAME, itemName);
        result.put(ITEM_IMAGE, itemImage);
        result.put(ITEM_QTY, itemQuantity);
        return result;
    }

    @Exclude
    public Item fromMap(Map<String,Object> entry) {
        if (entry.containsKey(PLACE_ID)) placeId = (String) entry.get(PLACE_ID);
        itemId = (int) (long) entry.get(ITEM_ID);
        if (entry.containsKey(ITEM_PRICE)) itemPriceValue = (float) (double) entry.get(ITEM_PRICE);
        itemName = (String) entry.get(ITEM_NAME);
        itemImage = (String) entry.get(ITEM_IMAGE);
        if (entry.containsKey(PLACE_NAME)) placeName = (String) entry.get(PLACE_NAME);
        itemQuantity = (int) (long) entry.get(ITEM_QTY);
        return this;
    }
}

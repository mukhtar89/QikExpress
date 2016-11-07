package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 11/3/2016.
 */
public class GroceryItem {

    private String groceryId;
    private Integer groceryItemId;
    private Float groceryItemPriceValue;
    private String groceryItemName, groceryItemImage;
    private List<String> catLevel;

    public String getGroceryId() {
        return groceryId;
    }
    public void setGroceryId(String groceryId) {
        this.groceryId = groceryId;
    }
    public Integer getGroceryItemId() {
        return groceryItemId;
    }
    public void setGroceryItemId(Integer groceryItemId) {
        this.groceryItemId = groceryItemId;
    }
    public Float getGroceryItemPriceValue() {
        return groceryItemPriceValue;
    }
    public void setGroceryItemPriceValue(Float groceryItemPriceValue) {this.groceryItemPriceValue = groceryItemPriceValue;}
    public String getGroceryItemName() {
        return groceryItemName;
    }
    public void setGroceryItemName(String groceryItemName) {this.groceryItemName = groceryItemName;}
    public String getGroceryItemImage() {
        return groceryItemImage;
    }
    public void setGroceryItemImage(String groceryItemImage) {this.groceryItemImage = groceryItemImage;}
    public List<String> getCatLevel() {return catLevel;}
    public void setCatLevel(List<String> catLevel) {
        this.catLevel = catLevel;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groceryId", groceryId);
        result.put("groceryItemId", groceryItemId);
        result.put("groceryItemPriceValue", groceryItemPriceValue);
        result.put("groceryItemName", groceryItemName);
        result.put("groceryItemImage", groceryItemImage);
        result.put("catLevel", catLevel);
        return result;
    }
}

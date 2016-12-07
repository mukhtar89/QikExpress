package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.ITEM_BRAND_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_BRAND_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_CUSTOM_SIZE;
import static com.equinox.qikexpress.Models.Constants.ITEM_ID;
import static com.equinox.qikexpress.Models.Constants.ITEM_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_PRICE;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;
import static com.equinox.qikexpress.Models.Constants.ITEM_VOL;
import static com.equinox.qikexpress.Models.Constants.ITEM_VOL_UNIT;
import static com.equinox.qikexpress.Models.Constants.ITEM_WEIGHT;
import static com.equinox.qikexpress.Models.Constants.ITEM_WEIGHT_UNIT;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;

/**
 * Created by mukht on 11/9/2016.
 */

public class Item {

    private String placeId, placeName;
    private Integer itemId;
    private String itemName, itemImage;
    private String itemBrandImage, itemBrandName;
    private Integer itemQuantity;
    private String countryCode, currencyCode, currencySymbol;

    private String itemCustomSize;
    private Boolean itemWeightLoose, itemVolLoose;
    private Float itemPricePerWeight, itemPricePerVol;
    private Float itemPriceValue, itemWeight, itemVol;
    private String itemWeightUnit, itemVolumeUnit;
    private Float itemWeightScaleMultiplicand, itemVolumeScaleMultiplicand;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(PLACE_ID, placeId);
        result.put(ITEM_ID, itemId);
        result.put(ITEM_PRICE, itemPriceValue);
        result.put(ITEM_CUSTOM_SIZE, itemCustomSize);
        result.put(ITEM_WEIGHT, itemWeight);
        result.put(ITEM_VOL, itemVol);
        result.put(ITEM_WEIGHT_UNIT, itemWeightUnit);
        result.put(ITEM_VOL_UNIT, itemVolumeUnit);
        result.put(ITEM_NAME, itemName);
        result.put(ITEM_IMAGE, itemImage);
        result.put(ITEM_BRAND_NAME, itemBrandName);
        result.put(ITEM_BRAND_IMAGE, itemBrandImage);
        result.put(PLACE_NAME, placeName);
        result.put(ITEM_QTY, itemQuantity);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapCheckout() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ITEM_ID, itemId);
        result.put(ITEM_PRICE, itemPriceValue);
        result.put(ITEM_CUSTOM_SIZE, itemCustomSize);
        result.put(ITEM_WEIGHT, itemWeight);
        result.put(ITEM_VOL, itemVol);
        result.put(ITEM_WEIGHT_UNIT, itemWeightUnit);
        result.put(ITEM_VOL_UNIT, itemVolumeUnit);
        result.put(ITEM_NAME, itemName);
        result.put(ITEM_IMAGE, itemImage);
        result.put(ITEM_BRAND_NAME, itemBrandName);
        result.put(ITEM_BRAND_IMAGE, itemBrandImage);
        result.put(ITEM_QTY, itemQuantity);
        return result;
    }

    @Exclude
    public Item fromMap(Map<String,Object> entry) {
        if (entry.containsKey(PLACE_ID)) placeId = (String) entry.get(PLACE_ID);
        if (entry.containsKey(PLACE_NAME)) placeName = (String) entry.get(PLACE_NAME);
        itemId = (int) (long) entry.get(ITEM_ID);
        itemName = (String) entry.get(ITEM_NAME);
        itemImage = (String) entry.get(ITEM_IMAGE);
        itemQuantity = (int) (long) entry.get(ITEM_QTY);
        if (entry.containsKey(ITEM_PRICE)) {
            if (entry.get(ITEM_PRICE) instanceof  Double) itemPriceValue = (float) (double) entry.get(ITEM_PRICE);
            else itemPriceValue = (float) (long) entry.get(ITEM_PRICE);
        }
        if (entry.containsKey(ITEM_CUSTOM_SIZE)) itemCustomSize = (String) entry.get(ITEM_CUSTOM_SIZE);
        if (entry.containsKey(ITEM_WEIGHT)) {
            if (entry.get(ITEM_WEIGHT) instanceof Double) itemWeight = (float) (double) entry.get(ITEM_WEIGHT);
            else itemWeight = (float) (long) entry.get(ITEM_WEIGHT);
        }
        if (entry.containsKey(ITEM_VOL)) {
            if (entry.get(ITEM_VOL) instanceof Double) itemVol = (float) (double) entry.get(ITEM_VOL);
            else itemVol = (float) (long) entry.get(ITEM_VOL);
        }
        if (entry.containsKey(ITEM_WEIGHT_UNIT)) itemWeightUnit = (String) entry.get(ITEM_WEIGHT_UNIT);
        if (entry.containsKey(ITEM_VOL_UNIT)) itemVolumeUnit = (String) entry.get(ITEM_VOL_UNIT);
        if (entry.containsKey(ITEM_BRAND_NAME)) itemBrandName = (String) entry.get(ITEM_BRAND_NAME);
        if (entry.containsKey(ITEM_BRAND_IMAGE)) itemBrandImage = (String) entry.get(ITEM_BRAND_IMAGE);
        return this;
    }

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
    public Float getItemWeight() {
        return itemWeight;
    }
    public void setItemWeight(Float itemWeight) {
        this.itemWeight = itemWeight;
    }
    public Float getItemVol() {
        return itemVol;
    }
    public void setItemVol(Float itemVol) {
        this.itemVol = itemVol;
    }
    public Boolean getItemWeightLoose() {
        return itemWeightLoose;
    }
    public void setItemWeightLoose(Boolean itemWeightLoose) {
        this.itemWeightLoose = itemWeightLoose;
    }
    public Boolean getItemVolLoose() {
        return itemVolLoose;
    }
    public void setItemVolLoose(Boolean itemVolLoose) {
        this.itemVolLoose = itemVolLoose;
    }
    public String getItemBrandImage() {
        return itemBrandImage;
    }
    public void setItemBrandImage(String itemBrandImage) {
        this.itemBrandImage = itemBrandImage;
    }
    public String getItemBrandName() {
        return itemBrandName;
    }
    public void setItemBrandName(String itemBrandName) {
        this.itemBrandName = itemBrandName;
    }
    public Float getItemPricePerWeight() {
        return itemPricePerWeight;
    }
    public void setItemPricePerWeight(Float itemPricePerWeight) {
        this.itemPricePerWeight = itemPricePerWeight;
    }
    public Float getItemPricePerVol() {
        return itemPricePerVol;
    }
    public void setItemPricePerVol(Float itemPricePerVol) {
        this.itemPricePerVol = itemPricePerVol;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public String getCurrencySymbol() {
        return currencySymbol;
    }
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
    public String getItemCustomSize() {
        return itemCustomSize;
    }
    public void setItemCustomSize(String itemCustomSize) {
        this.itemCustomSize = itemCustomSize;
    }
    public String getItemWeightUnit() {
        return itemWeightUnit;
    }
    public void setItemWeightUnit(String itemWeightUnit) {
        this.itemWeightUnit = itemWeightUnit;
    }
    public String getItemVolumeUnit() {
        return itemVolumeUnit;
    }
    public void setItemVolumeUnit(String itemVolumeUnit) {
        this.itemVolumeUnit = itemVolumeUnit;
    }
    public Float getItemWeightScaleMultiplicand() {
        return itemWeightScaleMultiplicand;
    }
    public void setItemWeightScaleMultiplicand(Float itemWeightScaleMultiplicand) {
        this.itemWeightScaleMultiplicand = itemWeightScaleMultiplicand;
    }
    public Float getItemVolumeScaleMultiplicand() {
        return itemVolumeScaleMultiplicand;
    }
    public void setItemVolumeScaleMultiplicand(Float itemVolumeScaleMultiplicand) {
        this.itemVolumeScaleMultiplicand = itemVolumeScaleMultiplicand;
    }
}

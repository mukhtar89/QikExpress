package com.equinox.qikexpress.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 11/12/2016.
 */

public class Order {

    private String id, status;
    private User from, driver;
    private Place shop = new Place();
    private List<Item> items = new ArrayList<>();
    private Long timestamp;
    private Boolean exchange;
    private Float weight;

    public Float getOrderValue() {
        Float value = 0.00f;
        for (Item item : items) {
            if (item.getItemPriceValue() == null)  return null;
            value += (item.getItemPriceValue() * item.getItemQuantity());
        }
        return value;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public Boolean getExchange() {
        return exchange;
    }
    public void setExchange(Boolean exchange) {
        this.exchange = exchange;
    }
    public void setFrom(User from) {
        this.from = from;
    }
    public User getFrom() {
        if (from == null)
            return new User();
        return from;
    }
    public void setDriver(User driver) {
        this.driver = driver;
    }
    public User getDriver() {
        return driver;
    }
    public Place getShop() {
        return shop;
    }
    public void setShop(Place shop) {
        this.shop = shop;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
}

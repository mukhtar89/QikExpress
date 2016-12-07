package com.equinox.qikexpress.Models;

import com.equinox.qikexpress.Enums.OrderStatus;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.CONSUMER;
import static com.equinox.qikexpress.Models.Constants.DEADLINE;
import static com.equinox.qikexpress.Models.Constants.DRIVER;
import static com.equinox.qikexpress.Models.Constants.EXCHANGE_ITEM;
import static com.equinox.qikexpress.Models.Constants.ORDER_ID;
import static com.equinox.qikexpress.Models.Constants.ORDER_ITEMS;
import static com.equinox.qikexpress.Models.Constants.ORDER_PAYLOAD;
import static com.equinox.qikexpress.Models.Constants.ORDER_STATUS;
import static com.equinox.qikexpress.Models.Constants.SHOP;
import static com.equinox.qikexpress.Models.Constants.TIMESTAMP;

/**
 * Created by mukht on 11/12/2016.
 */

public class Order {

    private String id;
    private OrderStatus orderStatus;
    private User from, driver;
    private Place shop = new Place();
    private List<Item> items = new ArrayList<>();
    private Long timestamp, deadline;
    private Boolean exchange;
    private Float weight;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ORDER_STATUS, orderStatus.toString());
        result.put(CONSUMER, from.toMap());
        if (driver != null) result.put(DRIVER, driver.toMap());
        result.put(TIMESTAMP, timestamp);
        result.put(DEADLINE, deadline);
        result.put(EXCHANGE_ITEM, exchange);
        result.put(ORDER_PAYLOAD, weight);
        Map<String,Object> listItems = new HashMap<>();
        for (Integer i=0; i<items.size(); i++)
            listItems.put(i.toString(),items.get(i).toMapCheckout());
        result.put(ORDER_ITEMS, listItems);
        return result;
    }

    @Exclude
    public Order fromMap(Map<String,Object> entry) {
        exchange = (Boolean) entry.get(EXCHANGE_ITEM);
        if (entry.get(ORDER_PAYLOAD) instanceof Long)
            weight = (float) (long) entry.get(ORDER_PAYLOAD);
        else weight = (float) (double) entry.get(ORDER_PAYLOAD);
        orderStatus = OrderStatus.valueOf((String) entry.get(ORDER_STATUS));
        from = DataHolder.currentUser;
        if (entry.containsKey(DRIVER))
            driver = new User().fromMap((HashMap<String, Object>) entry.get(DRIVER));
        timestamp = (Long) entry.get(TIMESTAMP);
        List<Object> iteratorItemObject = (List<Object>) entry.get(ORDER_ITEMS);
        for (Object itemObject : iteratorItemObject) {
            Item tempItem = new Item();
            items.add(tempItem.fromMap((HashMap<String, Object>) itemObject));
        }
        deadline = (Long) entry.get(DEADLINE);
        return this;
    }

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
    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    public Long getDeadline() {
        return deadline;
    }
    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}

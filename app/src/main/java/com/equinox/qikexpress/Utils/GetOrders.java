package com.equinox.qikexpress.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Enums.ChildState;
import com.equinox.qikexpress.Enums.OrderStatus;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Enums.ChildState.ADDED;
import static com.equinox.qikexpress.Enums.ChildState.CHANGED;
import static com.equinox.qikexpress.Enums.ChildState.REMOVED;
import static com.equinox.qikexpress.Models.Constants.BUSINESS;
import static com.equinox.qikexpress.Models.Constants.CONSUMER;
import static com.equinox.qikexpress.Models.Constants.DEADLINE;
import static com.equinox.qikexpress.Models.Constants.EXCHANGE_ITEM;
import static com.equinox.qikexpress.Models.Constants.ORDERS;
import static com.equinox.qikexpress.Models.Constants.ORDER_ITEMS;
import static com.equinox.qikexpress.Models.Constants.ORDER_PAYLOAD;
import static com.equinox.qikexpress.Models.Constants.ORDER_STATUS;
import static com.equinox.qikexpress.Models.Constants.TIMESTAMP;
import static com.equinox.qikexpress.Models.DataHolder.orderList;
import static com.equinox.qikexpress.Models.DataHolder.ordersReference;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

/**
 * Created by mukht on 11/16/2016.
 */

public class GetOrders {

    private static Handler orderHandler;
    private static GenerateNotification generateNotification;

    public static void updateMeta(Handler handler, Context context) {
        orderHandler = handler;
        generateNotification = new GenerateNotification(context);
    }

    public static synchronized void getOrders() {
        ordersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    if (orderHandler != null) orderHandler.sendMessage(new Message());
                ordersReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        ordersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getOrderPlace(dataSnapshot, ADDED);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                orderList.remove(dataSnapshot.getKey());
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private static void getOrderPlace(DataSnapshot dataSnapshot, ChildState state) {
        final Order tempOrder = new Order();
        tempOrder.setId(dataSnapshot.getKey());
        Place place = new Place().fromMap((Map<String, Object>) dataSnapshot.getValue());
        if (placeMap.containsKey(place.getPlaceId())) placeMap.get(place.getPlaceId()).mergePlace(place);
        else placeMap.put(place.getPlaceId(), place);
        tempOrder.setShop(placeMap.get(place.getPlaceId()));
        orderList.put(tempOrder.getId(), tempOrder);
        DataHolder.database.getReference(BUSINESS).child(tempOrder.getShop().getBasePath()).child(tempOrder.getShop().getPlaceId())
                .child(ORDERS).child(tempOrder.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getOrderLists(dataSnapshot, tempOrder.getId(), ADDED);
                /*generateNotification.showNotification(ADDED, orderList.get(dataSnapshot.getKey()),
                        orderList.get(dataSnapshot.getKey()).getOrderStatus());*/
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getOrderLists(dataSnapshot, tempOrder.getId(), CHANGED);
                //orderHandler.sendMessage(new Message());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getOrderLists(dataSnapshot, tempOrder.getId(), REMOVED);
                //orderHandler.sendMessage(new Message());
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private static void getOrderLists(DataSnapshot dataSnapshot, String orderId, ChildState state) {
        try {
            Order tempOrder = orderList.get(orderId);
            switch (dataSnapshot.getKey()) {
                case CONSUMER:
                    tempOrder.setFrom(new User().fromMap((Map<String, Object>) dataSnapshot.getValue()));
                    break;
                case DEADLINE:
                    tempOrder.setDeadline((Long) dataSnapshot.getValue());
                    break;
                case EXCHANGE_ITEM:
                    tempOrder.setExchange((Boolean) dataSnapshot.getValue());
                    break;
                case ORDER_ITEMS:
                    List<Object> iteratorItemObject = (List<Object>) dataSnapshot.getValue();
                    for (Object itemObject : iteratorItemObject) {
                        Item tempItem = new Item();
                        tempOrder.getItems().add(tempItem.fromMap((Map<String, Object>) itemObject));
                    }
                    break;
                case ORDER_PAYLOAD:
                    if (dataSnapshot.getValue() instanceof Long)
                        tempOrder.setWeight((float) (long) dataSnapshot.getValue());
                    else tempOrder.setWeight((float) (double) dataSnapshot.getValue());
                    break;
                case ORDER_STATUS:
                    tempOrder.setOrderStatus(OrderStatus.valueOf((String) dataSnapshot.getValue()));
                    break;
                case TIMESTAMP:
                    tempOrder.setTimestamp((Long) dataSnapshot.getValue());
                    break;

            }
            orderList.put(orderId, tempOrder);
            if (state.equals(ADDED) && dataSnapshot.getKey().equals(TIMESTAMP) && orderHandler != null)
                orderHandler.sendMessage(new Message());
        }  catch (Exception e) {
            e.getMessage();
        }
    }
}

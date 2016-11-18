package com.equinox.qikexpress.Utils;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.DRIVER;
import static com.equinox.qikexpress.Models.Constants.EXCHANGE_ITEM;
import static com.equinox.qikexpress.Models.Constants.LOCATION_LAT;
import static com.equinox.qikexpress.Models.Constants.LOCATION_LNG;
import static com.equinox.qikexpress.Models.Constants.ORDERS;
import static com.equinox.qikexpress.Models.Constants.ORDER_ITEMS;
import static com.equinox.qikexpress.Models.Constants.ORDER_PAYLOAD;
import static com.equinox.qikexpress.Models.Constants.ORDER_STATUS;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;
import static com.equinox.qikexpress.Models.Constants.TIMESTAMP;

/**
 * Created by mukht on 11/16/2016.
 */

public class GetOrders {

    public static synchronized void getOrders(final Handler orderHandler, final ProgressDialog pDialog) {
        DataHolder.ordersReference.addValueEventListener(new ValueEventListener() {
            Hashtable<String,Order> tempOrderTable = new Hashtable<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    synchronized (DataHolder.lock) {
                        DataHolder.orderList.clear();
                    }
                    final Iterator<DataSnapshot> iteratorUserOrders = dataSnapshot.getChildren().iterator();
                    while (iteratorUserOrders.hasNext()) {
                        DataSnapshot userOrderShot = iteratorUserOrders.next();
                        Order tempOrder = new Order();
                        tempOrder.setId(userOrderShot.getKey());
                        tempOrder.getShop().setPlaceId((String)userOrderShot.getValue());
                        tempOrderTable.put(tempOrder.getId(), tempOrder);
                        DataHolder.database.getReference(tempOrderTable.get(tempOrder.getId()).getShop().getPlaceId())
                                .child(ORDERS).child(tempOrderTable.get(tempOrder.getId()).getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.hasChildren()) {
                                        Order iteratorOrder = tempOrderTable.get(dataSnapshot.getKey());
                                        HashMap<String, Object> iteratorOrderObj = (HashMap<String, Object>) dataSnapshot.getValue();
                                        iteratorOrder.setExchange((Boolean) iteratorOrderObj.get(EXCHANGE_ITEM));
                                        if (iteratorOrderObj.get(ORDER_PAYLOAD) instanceof Long)
                                            iteratorOrder.setWeight((float) (long) iteratorOrderObj.get(ORDER_PAYLOAD));
                                        else
                                            iteratorOrder.setWeight((float) (double) iteratorOrderObj.get(ORDER_PAYLOAD));
                                        iteratorOrder.setStatus((String) iteratorOrderObj.get(ORDER_STATUS));
                                        iteratorOrder.setFrom(DataHolder.currentUser);
                                        if (iteratorOrderObj.containsKey(DRIVER))
                                            iteratorOrder.setDriver(new User().fromMap((HashMap<String, Object>) iteratorOrderObj.get(DRIVER)));
                                        iteratorOrder.setTimestamp((Long) iteratorOrderObj.get(TIMESTAMP));
                                        List<Item> tempItemList = new ArrayList<>();
                                        HashMap<String, Object> iteratorItemObject = (HashMap<String, Object>) iteratorOrderObj.get(ORDER_ITEMS);
                                        for (Map.Entry<String, Object> itemObject : iteratorItemObject.entrySet()) {
                                            Item tempItem = new Item();
                                            tempItemList.add(tempItem.fromMap((HashMap<String, Object>) itemObject.getValue()));
                                        }
                                        iteratorOrder.setItems(tempItemList);
                                        iteratorOrder.getShop().setName((String) iteratorOrderObj.get(PLACE_NAME));
                                        iteratorOrder.getShop().setLocation(new LatLng((Double) iteratorOrderObj.get(LOCATION_LAT), (Double) iteratorOrderObj.get(LOCATION_LNG)));
                                        if (DataHolder.getInstance().getPlaceMap().containsKey(iteratorOrder.getShop().getPlaceId()))
                                            iteratorOrder.setShop(DataHolder.getInstance().getPlaceMap().get(iteratorOrder.getShop().getPlaceId()));
                                        synchronized (DataHolder.lock) {
                                            DataHolder.orderList.put(iteratorOrder.getId(), iteratorOrder);
                                            if (!iteratorUserOrders.hasNext()) {
                                                if (orderHandler == null) DataHolder.ordersReference.removeEventListener(this);
                                                else orderHandler.sendMessage(new Message());
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}

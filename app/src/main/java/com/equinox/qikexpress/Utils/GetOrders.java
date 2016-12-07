package com.equinox.qikexpress.Utils;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.Models.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.BUSINESS;
import static com.equinox.qikexpress.Models.Constants.ORDERS;

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
                        Place place = new Place().fromMap((Map<String, Object>) userOrderShot.getValue());
                        if (DataHolder.getInstance().getPlaceMap().containsKey(place.getPlaceId()))
                            DataHolder.getInstance().getPlaceMap().get(place.getPlaceId()).mergePlace(place);
                        else DataHolder.getInstance().getPlaceMap().put(place.getPlaceId(), place);
                        tempOrder.setShop(DataHolder.getInstance().getPlaceMap().get(place.getPlaceId()));
                        tempOrderTable.put(tempOrder.getId(), tempOrder);
                        DataHolder.database.getReference(BUSINESS).child(tempOrder.getShop().getBasePath()).child(tempOrder.getShop().getPlaceId())
                                .child(ORDERS).child(tempOrder.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.hasChildren()) {
                                        Order iteratorOrder = tempOrderTable.get(dataSnapshot.getKey())
                                                .fromMap((Map<String, Object>) dataSnapshot.getValue());
                                        synchronized (DataHolder.lock) {
                                            DataHolder.orderList.put(iteratorOrder.getId(), iteratorOrder);
                                            if (!iteratorUserOrders.hasNext()) {
                                                if (orderHandler == null)
                                                    DataHolder.ordersReference.removeEventListener(this);
                                                else
                                                    orderHandler.sendMessage(new Message());
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
                } else synchronized (DataHolder.lock) {
                    DataHolder.orderList.clear();
                    orderHandler.sendMessage(new Message());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}

package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.equinox.qikexpress.Adapters.CheckoutListAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.FetchGeoAddress;
import com.equinox.qikexpress.Utils.GetPlaceDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.equinox.qikexpress.Enums.OrderStatus.INCOMING;
import static com.equinox.qikexpress.Enums.QikList.GROCERY;
import static com.equinox.qikexpress.Models.Constants.BUSINESS;
import static com.equinox.qikexpress.Models.Constants.CHECKOUT;
import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.Constants.ITEM_ID;
import static com.equinox.qikexpress.Models.Constants.ITEM_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_PRICE;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;
import static com.equinox.qikexpress.Models.Constants.ORDERS;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;
import static com.equinox.qikexpress.Models.Constants.PLACE_TYPE;
import static com.equinox.qikexpress.Models.Constants.SAVE_FOR_LATER;
import static com.equinox.qikexpress.Models.Constants.SHOP;
import static com.equinox.qikexpress.Models.Constants.WALLET;

public class CheckoutActivity extends AppCompatActivity {

    private ListView checkoutListView;
    private CheckoutListAdapter checkoutListAdapter;
    private List<Item> checkoutItemsList = new ArrayList<>();
    private DatabaseReference checkoutReference, cartReference, walletReference;
    private Float totalPrice = (float) 0.00;
    final Float[] walletAmount = {(float) 0.00};
    private Hashtable<String,FetchGeoAddress> fetchGeoAddress = new Hashtable<>();
    private Hashtable<String,Order> tempOrderTable = new Hashtable<>();
    private Hashtable<String,Integer> placeItemCount = new Hashtable<>();
    private GetPlaceDetails getPlaceDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPlaceDetails = new GetPlaceDetails(null, addressFetchHandler);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletReference = DataHolder.userDatabaseReference.child(WALLET).getRef();
                walletReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) walletAmount[0] = (float) 0.00;
                        else if (dataSnapshot.getValue() instanceof Long)  walletAmount[0] = (float) (long) dataSnapshot.getValue();
                        else walletAmount[0] = (float) (double) dataSnapshot.getValue();
                        List<String> placeList = new ArrayList<>();
                        for (Item item : checkoutItemsList) {
                            if (!placeList.contains(item.getPlaceId()))
                                placeList.add(item.getPlaceId());
                            if (!placeItemCount.containsKey(item.getPlaceId())) placeItemCount.put(item.getPlaceId(), 1);
                            else placeItemCount.put(item.getPlaceId(), placeItemCount.get(item.getPlaceId())+1);
                        }
                        for (String placeId : placeList) {
                            Message message = new Message();
                            message.obj = placeId;
                            if (!DataHolder.getInstance().getPlaceMap().containsKey(placeId))
                                getPlaceDetails.parseDetail(placeId, placeId);
                            else if (DataHolder.getInstance().getPlaceMap().get(placeId).getAddress() != null)
                                    addressFetchHandler.sendMessage(message);
                            else if (!fetchGeoAddress.containsKey(placeId)) {
                                fetchGeoAddress.put(placeId, new FetchGeoAddress());
                                Location tempLocation = new Location(LocationManager.GPS_PROVIDER);
                                tempLocation.setLatitude(DataHolder.getInstance().getPlaceMap().get(placeId).getLocation().latitude);
                                tempLocation.setLongitude(DataHolder.getInstance().getPlaceMap().get(placeId).getLocation().longitude);
                                fetchGeoAddress.get(placeId).fetchLocationGeoData(tempLocation, addressFetchHandler, message.obj);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {     }
                });
                startActivity(new Intent(CheckoutActivity.this, TrackingActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkoutListView = (ListView) findViewById(R.id.checkout_list_items);
        checkoutListAdapter = new CheckoutListAdapter(checkoutItemsList, this);
        checkoutListView.setAdapter(checkoutListAdapter);

        cartReference = DataHolder.userDatabaseReference.child(GROCERY_CART).getRef();
        checkoutReference = DataHolder.userDatabaseReference.child(CHECKOUT).getRef();
        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iteratorCart = dataSnapshot.getChildren().iterator();
                    HashMap<String, Object> iteratorObject;
                    Item cartItem;
                    totalPrice = (float) 0.00;
                    while (iteratorCart.hasNext()) {
                        iteratorObject = (HashMap<String, Object>) iteratorCart.next().getValue();
                        if (iteratorObject.containsKey(SAVE_FOR_LATER)) {
                            if ((Boolean) iteratorObject.get(SAVE_FOR_LATER)) continue;
                        }
                        cartItem = new Item();
                        cartItem.fromMap(iteratorObject);
                        totalPrice+=cartItem.getItemPriceValue();
                        synchronized (DataHolder.lock) {
                            if (!checkoutItemsList.contains(cartItem))checkoutItemsList.add(cartItem);
                            checkoutListAdapter.notifyDataSetChanged();
                        }
                        Map<String, Object> itemMap = cartItem.toMap();
                        Map<String, Object> checkoutItemAdd = new HashMap<>();
                        String node = cartItem.getPlaceId()+cartItem.getItemId();
                        //TODO remove the comment below:
                        //cartReference.child(node).removeValue();
                        checkoutItemAdd.put(node, itemMap);
                        checkoutReference.updateChildren(checkoutItemAdd);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {   }
        });
    }

    private Handler addressFetchHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            for (final Item item : checkoutItemsList) {
                if (!tempOrderTable.containsKey(item.getPlaceId()))
                    tempOrderTable.put(item.getPlaceId(), new Order());
                if (item.getPlaceId().equals(msg.obj)) {
                    Order tempOrder = tempOrderTable.get(item.getPlaceId());
                    if (fetchGeoAddress.containsKey(item.getPlaceId())) {
                        DataHolder.getInstance().getPlaceMap().get(item.getPlaceId())
                                .setAddress(fetchGeoAddress.get(item.getPlaceId()).getAddress());
                    }
                    tempOrder.setShop(DataHolder.getInstance().getPlaceMap().get(item.getPlaceId()));
                    DatabaseReference businessReference =
                            DataHolder.database.getReference(BUSINESS).child(tempOrder.getShop().getBasePath())
                                    .child(item.getPlaceId());
                    businessReference.child(PLACE_TYPE).setValue(GROCERY.getListName());
                    businessReference.child(SHOP).setValue(tempOrder.getShop().toMap());
                    DatabaseReference orderBusinessReference = businessReference.child(ORDERS);
                    if (tempOrder.getId() == null) {
                        tempOrder.setId(orderBusinessReference.push().getKey());
                        DataHolder.ordersReference.child(tempOrder.getId()).setValue(tempOrder.getShop().toMap());
                        tempOrder.setOrderStatus(INCOMING);
                        tempOrder.setFrom(DataHolder.currentUser);
                        Float tempWeight = (float) (new Random().nextInt(100 - 5) + 5);
                        tempOrder.setWeight(tempWeight);
                        long timestampTemp = System.currentTimeMillis();
                        tempOrder.setTimestamp(timestampTemp);
                        tempOrder.setDeadline(timestampTemp + 1000000000);
                        tempOrder.setExchange(true);
                    }
                    tempOrder.getItems().add(item);
                    checkoutReference.child(item.getPlaceId() + item.getItemId()).removeValue();
                    tempOrderTable.put(item.getPlaceId(), tempOrder);
                    if (tempOrder.getItems().size() == placeItemCount.get(item.getPlaceId()))
                        orderBusinessReference.child(tempOrder.getId()).setValue(tempOrder.toMap());
                    /*if (item.getItemPriceValue() != null) {
                        final DatabaseReference walletOutletReference = businessReference.child(WALLET);
                        final Float[] walletBusinessAmount = {(float) 0.00};
                        walletOutletReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null)
                                    walletBusinessAmount[0] = (float) (double) dataSnapshot.getValue();
                                walletBusinessAmount[0] += (item.getItemPriceValue()*item.getItemQuantity());
                                walletAmount[0] -= (item.getItemPriceValue()*item.getItemQuantity());
                                walletOutletReference.setValue(walletBusinessAmount[0]);
                                walletReference.setValue(walletAmount[0]);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {     }
                        });
                    }*/

                }
            }
            return false;
        }
    });
}

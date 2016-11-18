package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.CheckoutListAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.equinox.qikexpress.Enums.OrderStatus.INCOMING;
import static com.equinox.qikexpress.Enums.QikList.GROCERY;
import static com.equinox.qikexpress.Models.Constants.CHECKOUT;
import static com.equinox.qikexpress.Models.Constants.DEADLINE;
import static com.equinox.qikexpress.Models.Constants.EXCHANGE_ITEM;
import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.Constants.IS_PARTNER;
import static com.equinox.qikexpress.Models.Constants.ITEM_ID;
import static com.equinox.qikexpress.Models.Constants.ITEM_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_PRICE;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;
import static com.equinox.qikexpress.Models.Constants.LOCATION_LAT;
import static com.equinox.qikexpress.Models.Constants.LOCATION_LNG;
import static com.equinox.qikexpress.Models.Constants.ORDERS;
import static com.equinox.qikexpress.Models.Constants.ORDER_FROM;
import static com.equinox.qikexpress.Models.Constants.ORDER_ITEMS;
import static com.equinox.qikexpress.Models.Constants.ORDER_PAYLOAD;
import static com.equinox.qikexpress.Models.Constants.ORDER_STATUS;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;
import static com.equinox.qikexpress.Models.Constants.PLACE_TYPE;
import static com.equinox.qikexpress.Models.Constants.SAVE_FOR_LATER;
import static com.equinox.qikexpress.Models.Constants.TIMESTAMP;
import static com.equinox.qikexpress.Models.Constants.WALLET;

public class CheckoutActivity extends AppCompatActivity {

    private ListView checkoutListView;
    private CheckoutListAdapter checkoutListAdapter;
    private List<Item> checkoutItemsList = new ArrayList<>();
    private DatabaseReference checkoutReference, cartReference, walletReference;
    private Float totalPrice = (float) 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Float[] walletAmount = {(float) 0.00};
                walletReference = DataHolder.userDatabaseReference.child(WALLET).getRef();
                walletReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    Map<String,String> orderPlaceMap = new HashMap<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) walletAmount[0] = (float) 0.00;
                        else if (dataSnapshot.getValue() instanceof Long)  walletAmount[0] = (float) (long) dataSnapshot.getValue();
                        else walletAmount[0] = (float) (double) dataSnapshot.getValue();
                        for (final Item item : checkoutItemsList) {
                            DataHolder.database.getReference(item.getPlaceId()).child(IS_PARTNER)
                                    .setValue(DataHolder.getInstance().getPlaceMap().get(item.getPlaceId()).getPartner());
                            DataHolder.database.getReference(item.getPlaceId()).child(PLACE_TYPE).setValue(GROCERY.getListName());
                            DatabaseReference orderBusinessReference = DataHolder.database.getReference(item.getPlaceId()).child(ORDERS);
                            if (!orderPlaceMap.containsKey(item.getPlaceId())) {
                                String idTemp = orderBusinessReference.push().getKey();
                                orderPlaceMap.put(item.getPlaceId(), idTemp);
                                DataHolder.ordersReference.child(idTemp).setValue(item.getPlaceId());
                                orderBusinessReference.child(idTemp).child(ORDER_STATUS).setValue(INCOMING.getNodeName());
                                orderBusinessReference.child(idTemp).child(PLACE_NAME).setValue(item.getPlaceName());
                                orderBusinessReference.child(idTemp).child(ORDER_FROM).setValue(DataHolder.currentUser.toMap());
                                orderBusinessReference.child(idTemp).child(LOCATION_LAT)
                                        .setValue(DataHolder.getInstance().getPlaceMap().get(item.getPlaceId()).getLocation().latitude);
                                orderBusinessReference.child(idTemp).child(LOCATION_LNG)
                                        .setValue(DataHolder.getInstance().getPlaceMap().get(item.getPlaceId()).getLocation().longitude);
                                Float tempWeight = (float) (new Random().nextInt(100 - 5) + 5);
                                orderBusinessReference.child(idTemp).child(ORDER_PAYLOAD).setValue(tempWeight);
                                orderBusinessReference.child(idTemp).child(EXCHANGE_ITEM).setValue(true);
                                long timestampTemp = System.currentTimeMillis();
                                orderBusinessReference.child(idTemp).child(TIMESTAMP).setValue(timestampTemp);
                                if (!DataHolder.getInstance().getPlaceMap().get(item.getPlaceId()).getPartner())
                                    orderBusinessReference.child(idTemp).child(DEADLINE).setValue(timestampTemp);
                                else orderBusinessReference.child(idTemp).child(DEADLINE).setValue(timestampTemp + 1000000000);
                            }
                            String node = item.getPlaceId()+item.getItemId();
                            orderBusinessReference.child(orderPlaceMap.get(item.getPlaceId())).child(ORDER_ITEMS).child(node).setValue(item.toMap());
                            checkoutReference.child(node).removeValue();
                            if (item.getItemPriceValue() != null) {
                                final DatabaseReference walletOutletReference =
                                        DataHolder.database.getReference(item.getPlaceId()).child(WALLET);
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
                        cartItem.setPlaceId((String) iteratorObject.get(PLACE_ID));
                        cartItem.setPlaceName((String) iteratorObject.get(PLACE_NAME));
                        cartItem.setItemId((int) (long) iteratorObject.get(ITEM_ID));
                        cartItem.setItemName((String) iteratorObject.get(ITEM_NAME));
                        cartItem.setItemImage((String) iteratorObject.get(ITEM_IMAGE));
                        cartItem.setItemPriceValue(iteratorObject.containsKey(ITEM_PRICE)
                                ? (float) (double) iteratorObject.get(ITEM_PRICE) : null);
                        if (cartItem.getItemPriceValue() != null) totalPrice+=cartItem.getItemPriceValue();
                        cartItem.setItemQuantity(iteratorObject.containsKey(ITEM_QTY)
                                ? (int) (long) iteratorObject.get(ITEM_QTY) : 1);
                        synchronized (DataHolder.lock) {
                            if (!checkoutItemsList.contains(cartItem))checkoutItemsList.add(cartItem);
                            checkoutListAdapter.notifyDataSetChanged();
                        }
                        Map<String, Object> itemMap = cartItem.toMap();
                        Map<String, Object> checkoutItemAdd = new HashMap<>();
                        String node = cartItem.getPlaceId()+cartItem.getItemId();
                        cartReference.child(node).removeValue();
                        checkoutItemAdd.put(node, itemMap);
                        checkoutReference.updateChildren(checkoutItemAdd);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {   }
        });
    }
}

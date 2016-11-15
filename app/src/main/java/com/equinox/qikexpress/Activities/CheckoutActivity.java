package com.equinox.qikexpress.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.CheckoutListAdapter;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.equinox.qikexpress.Enums.QikList.GROCERY;

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
                walletReference = DataHolder.userDatabaseReference.child("wallet").getRef();
                walletReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    Map<String,String> orderPlaceMap = new HashMap<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() instanceof Long)
                            walletAmount[0] = (float) (long) dataSnapshot.getValue();
                        else walletAmount[0] = (float) (double) dataSnapshot.getValue();
                        if (walletAmount[0] == null) walletAmount[0] = (float) 0.00;
                        for (final Item item : checkoutItemsList) {
                            DataHolder.database.getReference(item.getPlaceId()).child("isPartner")
                                    .setValue(DataHolder.getInstance().getGroceryMap().get(item.getPlaceId()).getPartner());
                            DataHolder.database.getReference(item.getPlaceId()).child("placeType").setValue(GROCERY.getListName());
                            DatabaseReference orderReference = DataHolder.database.getReference(item.getPlaceId()).child("orders");
                            if (!orderPlaceMap.containsKey(item.getPlaceId())) {
                                orderPlaceMap.put(item.getPlaceId(), orderReference.push().getKey());
                                orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("orderStatus").setValue(Constants.ORDER_INCOMING);
                                orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("orderPayload").setValue(new Random().nextInt(100 - 5) + 5);
                                orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("exchangeItem").setValue(true);
                                orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("timestamp").setValue(System.currentTimeMillis());
                                orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("orderFrom").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                            String node = item.getPlaceId()+item.getItemId();
                            orderReference.child(orderPlaceMap.get(item.getPlaceId())).child("orderItems").child(node).setValue(item.toMapCheckout());
                            cartReference.child(node).removeValue();
                            if (item.getItemPriceValue() != null) {
                                final DatabaseReference walletOutletReference =
                                        DataHolder.database.getReference(item.getPlaceId()).child("wallet");
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
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkoutListView = (ListView) findViewById(R.id.checkout_list_items);
        checkoutListAdapter = new CheckoutListAdapter(checkoutItemsList, this);
        checkoutListView.setAdapter(checkoutListAdapter);

        cartReference = DataHolder.userDatabaseReference.child("grocery_cart").getRef();
        checkoutReference = DataHolder.userDatabaseReference.child("checkout").getRef();
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
                        if (iteratorObject.containsKey("saveForLater")) {
                            if ((Boolean) iteratorObject.get("saveForLater")) continue;
                        }
                        cartItem = new Item();
                        cartItem.setPlaceId((String) iteratorObject.get("placeId"));
                        cartItem.setPlaceName((String) iteratorObject.get("placeName"));
                        cartItem.setItemId((int) (long) iteratorObject.get("itemId"));
                        cartItem.setItemName((String) iteratorObject.get("itemName"));
                        cartItem.setItemImage((String) iteratorObject.get("itemImage"));
                        cartItem.setItemPriceValue(iteratorObject.containsKey("itemPriceValue")
                                ? (float) (double) iteratorObject.get("itemPriceValue") : null);
                        if (cartItem.getItemPriceValue() != null) totalPrice+=cartItem.getItemPriceValue();
                        cartItem.setItemQuantity(iteratorObject.containsKey("itemQuantity")
                                ? (int) (long) iteratorObject.get("itemQuantity") : 1);
                        checkoutItemsList.add(cartItem);
                        Map<String, Object> itemMap = cartItem.toMap();
                        Map<String, Object> checkoutItemAdd = new HashMap<>();
                        String node = cartItem.getPlaceId()+cartItem.getItemId();
                        cartReference.child(node).removeValue();
                        checkoutItemAdd.put(node, itemMap);
                        checkoutReference.updateChildren(checkoutItemAdd);
                    }
                    checkoutListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CheckoutActivity.this, "No items for checkout!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {   }
        });
    }
}

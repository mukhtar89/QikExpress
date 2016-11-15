package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.GroceryCartRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroceryShoppingCartActivity extends AppCompatActivity {

    private List<GroceryItemCart> groceryItemCartList = new ArrayList<>();
    private RecyclerView groceryShoppingList;
    private ProgressDialog progressDialog;
    private ValueEventListener oneTimeListener;
    private GroceryCartRecyclerAdapter groceryCartRecyclerAdapter;
    private DatabaseReference groceryCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroceryShoppingCartActivity.this, CheckoutActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Cart Items...");
        progressDialog.show();

        groceryCart = DataHolder.userDatabaseReference.child("grocery_cart").getRef();
        oneTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iteratorCart = dataSnapshot.getChildren().iterator();
                    HashMap<String, Object> iteratorObject;
                    GroceryItemCart groceryItemCart;
                    groceryItemCartList.clear();
                    while (iteratorCart.hasNext()) {
                        iteratorObject = (HashMap<String, Object>) iteratorCart.next().getValue();
                        groceryItemCart = new GroceryItemCart();
                        groceryItemCart.setPlaceId((String) iteratorObject.get("placeId"));
                        groceryItemCart.setPlaceName((String) iteratorObject.get("placeName"));
                        groceryItemCart.setItemId((int) (long) iteratorObject.get("itemId"));
                        groceryItemCart.setItemName((String) iteratorObject.get("itemName"));
                        groceryItemCart.setItemImage((String) iteratorObject.get("itemImage"));
                        groceryItemCart.setItemPriceValue(iteratorObject.containsKey("itemPriceValue")
                                ? (float) (double) iteratorObject.get("itemPriceValue") : null);
                        groceryItemCart.setSaveForLater(iteratorObject.containsKey("saveForLater")
                                ? (Boolean) iteratorObject.get("saveForLater") : false);
                        groceryItemCart.setItemQuantity(iteratorObject.containsKey("itemQuantity")
                                ? (int) (long) iteratorObject.get("itemQuantity") : 1);
                        groceryItemCartList.add(groceryItemCart);
                    }
                    groceryCartRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(GroceryShoppingCartActivity.this, "Swipe Left or Right to remove items from the Cart", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(GroceryShoppingCartActivity.this, "Cart is empty! Please shop for some items!", Toast.LENGTH_LONG).show();
                    finish();
                }
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroceryShoppingCartActivity.this, "Cannot fetch Grocery Cart Items now!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();  }
        };
        groceryCart.addListenerForSingleValueEvent(oneTimeListener);


        groceryShoppingList = (RecyclerView) findViewById(R.id.grocery_shopping_cart_list);
        groceryShoppingList.setLayoutManager(new LinearLayoutManager(this));
        groceryShoppingList.setHasFixedSize(true);
        groceryCartRecyclerAdapter = new GroceryCartRecyclerAdapter(groceryItemCartList, groceryCart);
        groceryShoppingList.setAdapter(groceryCartRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getLayoutPosition();
                final GroceryItemCart itemCart = groceryItemCartList.get(position);
                Snackbar removeCart = Snackbar.make(groceryShoppingList,
                        itemCart.getItemName()+" removed from Cart", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        });
                removeCart.show();
                removeCart.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            groceryCart.child(itemCart.getPlaceId() + itemCart.getItemId()).removeValue();
                            groceryItemCartList.remove(position);
                            groceryCartRecyclerAdapter.notifyItemRemoved(position);
                        }
                        groceryCartRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(groceryShoppingList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        groceryCart.addListenerForSingleValueEvent(oneTimeListener);
    }
}

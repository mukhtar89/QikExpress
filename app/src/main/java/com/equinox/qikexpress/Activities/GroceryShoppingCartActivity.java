package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    private GroceryCartRecyclerAdapter groceryCartRecyclerAdapter;

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

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Cart Items...");
        progressDialog.show();

        final DatabaseReference groceryCart = DataHolder.getInstance().getUserDatabaseReference().child("grocery_cart").getRef();
        groceryCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iteratorCart = dataSnapshot.getChildren().iterator();
                    HashMap<String, Object> iteratorObject;
                    GroceryItemCart groceryItemCart;
                    while (iteratorCart.hasNext()) {
                        iteratorObject = (HashMap<String, Object>) iteratorCart.next().getValue();
                        groceryItemCart = new GroceryItemCart();
                        groceryItemCart.setCatLevel((List<String>) iteratorObject.get("catLevel"));
                        groceryItemCart.setGroceryId((String) iteratorObject.get("groceryId"));
                        groceryItemCart.setGroceryName((String) iteratorObject.get("groceryName"));
                        groceryItemCart.setGroceryItemId((int) (long) iteratorObject.get("groceryItemId"));
                        groceryItemCart.setGroceryItemName((String) iteratorObject.get("groceryItemName"));
                        groceryItemCart.setGroceryItemImage((String) iteratorObject.get("groceryItemImage"));
                        groceryItemCart.setGroceryItemPriceValue(iteratorObject.containsKey("groceryItemPriceValue")
                                ? (float) (double) iteratorObject.get("groceryItemPriceValue") : null);
                        groceryItemCart.setSaveForLater(iteratorObject.containsKey("saveForLater")
                                ? (Boolean) iteratorObject.get("saveForLater") : false);
                        groceryItemCart.setItemQuantity(iteratorObject.containsKey("itemQuantity")
                                ? (int) (long) iteratorObject.get("itemQuantity") : 1);
                        groceryItemCartList.add(groceryItemCart);
                    }
                    groceryCartRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(GroceryShoppingCartActivity.this, "Swipe Left or Right to remove items from the Cart", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(GroceryShoppingCartActivity.this, "Cart is Empty! Shop and come again.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroceryShoppingCartActivity.this, "Cannot fetch Grocery Cart Items now!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();  }
        });


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
                        itemCart.getGroceryItemName()+" removed from Cart", Snackbar.LENGTH_LONG)
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
                            groceryCart.child(itemCart.getGroceryId() + itemCart.getGroceryItemId()).removeValue();
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
}

package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.GroceryCartRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.FetchGeoAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.Constants.ITEM_ID;
import static com.equinox.qikexpress.Models.Constants.ITEM_IMAGE;
import static com.equinox.qikexpress.Models.Constants.ITEM_NAME;
import static com.equinox.qikexpress.Models.Constants.ITEM_PRICE;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;
import static com.equinox.qikexpress.Models.Constants.PLACE_ID;
import static com.equinox.qikexpress.Models.Constants.PLACE_NAME;
import static com.equinox.qikexpress.Models.Constants.SAVE_FOR_LATER;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

public class GroceryShoppingCartActivity extends AppCompatActivity {

    private List<GroceryItemCart> groceryItemCartList = new ArrayList<>();
    private RecyclerView groceryShoppingList;
    private ProgressDialog progressDialog;
    private ValueEventListener oneTimeListener;
    private GroceryCartRecyclerAdapter groceryCartRecyclerAdapter;
    private DatabaseReference groceryCart;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        FloatingActionButton checkoutFAB = (FloatingActionButton) findViewById(R.id.fab);
        checkoutFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashSet<String> placesSet = new HashSet<>();
                for (GroceryItemCart itemCart : groceryItemCartList)
                    if (!placesSet.contains(itemCart.getPlaceId())) placesSet.add(itemCart.getPlaceId());
                if (placesSet.size() > 1) {
                    AlertDialog.Builder placeWarningDialog = new AlertDialog.Builder(context);
                    placeWarningDialog.setCancelable(false)
                            .setTitle("Multiple Stores")
                            .setIcon(R.drawable.logo)
                            .setMessage("Your cart have order items from multiple stores. " +
                                    "Checking out will generate multiple orders from different stores. " +
                                    "Are you sure you want to continue?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(GroceryShoppingCartActivity.this, CheckoutActivity.class));
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else startActivity(new Intent(GroceryShoppingCartActivity.this, CheckoutActivity.class));

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Cart Items...");
        progressDialog.show();

        if (DataHolder.userDatabaseReference != null)
            groceryCart = DataHolder.userDatabaseReference.child(GROCERY_CART).getRef();
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
                        groceryItemCart.fromMap(iteratorObject);
                        groceryItemCart.setSaveForLater(iteratorObject.containsKey(SAVE_FOR_LATER)
                                ? (Boolean) iteratorObject.get(SAVE_FOR_LATER) : false);
                        groceryItemCartList.add(groceryItemCart);
                    }
                    groceryCartRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(GroceryShoppingCartActivity.this,
                            "Swipe Left or Right to remove items from the Cart", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(GroceryShoppingCartActivity.this,
                            "Cart is empty! Please shop for some items!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroceryShoppingCartActivity.this,
                        "Cannot fetch Grocery Cart Items now!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();  }
        };
        if (groceryCart != null) groceryCart.addListenerForSingleValueEvent(oneTimeListener);


        groceryShoppingList = (RecyclerView) findViewById(R.id.grocery_shopping_cart_list);
        groceryShoppingList.setLayoutManager(new LinearLayoutManager(this));
        groceryShoppingList.setHasFixedSize(true);
        groceryCartRecyclerAdapter = new GroceryCartRecyclerAdapter(groceryItemCartList, groceryCart);
        groceryShoppingList.setAdapter(groceryCartRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getLayoutPosition();
                final GroceryItemCart itemCart = groceryItemCartList.get(position);
                groceryCart.child(itemCart.getPlaceId() + itemCart.getItemId()).removeValue();
                groceryItemCartList.remove(position);
                groceryCartRecyclerAdapter.notifyItemRemoved(position);
                Snackbar removeCart = Snackbar.make(groceryShoppingList,
                        itemCart.getItemName()+" removed from Cart", Snackbar.LENGTH_INDEFINITE)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                groceryCart.child(itemCart.getPlaceId() + itemCart.getItemId()).setValue(itemCart.toMapCart());
                                groceryItemCartList.add(position, itemCart);
                                groceryCartRecyclerAdapter.notifyItemInserted(position);
                            }
                        });
                removeCart.show();
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

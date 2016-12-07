package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.equinox.qikexpress.Adapters.GroceryItemRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.DataHolder.category1;
import static com.equinox.qikexpress.Models.DataHolder.category2;
import static com.equinox.qikexpress.Models.DataHolder.groceryItemCollectionCat2Mapping;

public class GroceryItemActivity extends AppCompatActivity {

    private List<GroceryItemCollection> groceryItemCollectionList;
    private RecyclerView groceryItemRecycler;
    private GroceryItemRecyclerAdapter groceryItemRecyclerAdapter;
    private HybridLayoutManager layoutManager;
    private TextView cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        String category1 = getIntent().getStringExtra("CATEGORY1");
        String category2 = getIntent().getStringExtra("CATEGORY2");
        String placeId = getIntent().getStringExtra("PLACE_ID");
        if (!DataHolder.getInstance().getPlaceMap().get(placeId).getPartner()) {
            Snackbar.make(findViewById(R.id.grocery_item_coordinator_layout),
                    "The price and availability is at the discretion of the outlet.", Snackbar.LENGTH_INDEFINITE).show();
        }

        groceryItemCollectionList = DataHolder.groceryItemCollectionCat2Mapping.get(category1).get(category2);

        layoutManager = new HybridLayoutManager(this);
        groceryItemRecycler = (RecyclerView) findViewById(R.id.grocery_item_recycler);
        groceryItemRecycler.setLayoutManager(layoutManager.getLayoutManager(150));
        groceryItemRecycler.setHasFixedSize(true);
        groceryItemRecyclerAdapter = new GroceryItemRecyclerAdapter(this, groceryItemCollectionList);
        groceryItemRecycler.setAdapter(groceryItemRecyclerAdapter);

        getSupportActionBar().setTitle(category1 + " -> " + category2);
        DataHolder.category1 = category1;
        DataHolder.category2 = category2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grocery_item, menu);
        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        cartCount = (TextView) menuCart.findViewById(R.id.cart_count);
        DataHolder.userDatabaseReference.child(GROCERY_CART).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = (int) dataSnapshot.getChildrenCount();
                if (count == 0) {
                    cartCount.setVisibility(View.INVISIBLE);
                } else {
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(count.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            Intent searchGroceryItemsIntent = new Intent(GroceryItemActivity.this, SearchGroceryItemActivity.class);
            searchGroceryItemsIntent.putExtra("CATEGORY1", category1);
            searchGroceryItemsIntent.putExtra("CATEGORY2", category2);
            startActivity(searchGroceryItemsIntent);
            return true;
        }
        if (id == R.id.action_cart) {
            Intent groceryShoppingCartIntent = new Intent(GroceryItemActivity.this, GroceryShoppingCartActivity.class);
            startActivity(groceryShoppingCartIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

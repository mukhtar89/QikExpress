package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.equinox.qikexpress.Fragments.GroceryItemOverviewFragment;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.DataHolder.category1;
import static com.equinox.qikexpress.Models.DataHolder.category2;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

public class GroceryItemOverviewActivity extends AppCompatActivity {

    private TextView cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_item_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        String category1 = getIntent().getStringExtra("CATEGORY1");
        String category2 = getIntent().getStringExtra("CATEGORY2");
        String placeId = getIntent().getStringExtra("PLACE_ID");

        getSupportActionBar().setTitle(category1 + " -> " + category2);
        if (!placeMap.get(placeId).getPartner()) {
            Snackbar.make(findViewById(R.id.grocery_item_coordinator_layout),
                    "The price and availability is at the discretion of the outlet.", Snackbar.LENGTH_INDEFINITE).show();
        }

        Bundle arguments = new Bundle();
        arguments.putString("CATEGORY1",category1);
        arguments.putString("CATEGORY2", category2);
        arguments.putString("PLACE_ID", placeId);
        GroceryItemOverviewFragment fragment = new GroceryItemOverviewFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.grocery_item_overview_container, fragment)
                .commit();
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
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groceryShoppingCartIntent = new Intent(GroceryItemOverviewActivity.this, GroceryShoppingCartActivity.class);
                startActivity(groceryShoppingCartIntent);
            }
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
            Intent searchGroceryItemsIntent = new Intent(GroceryItemOverviewActivity.this, SearchGroceryItemActivity.class);
            searchGroceryItemsIntent.putExtra("CATEGORY1", category1);
            searchGroceryItemsIntent.putExtra("CATEGORY2", category2);
            startActivity(searchGroceryItemsIntent);
            return true;
        }
        if (id == R.id.action_cart) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

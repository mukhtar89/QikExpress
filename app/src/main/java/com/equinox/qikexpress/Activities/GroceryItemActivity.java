package com.equinox.qikexpress.Activities;

import android.os.Bundle;
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
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroceryItemActivity extends AppCompatActivity {

    private List<GroceryItem> groceryItemList1, groceryItemList2;
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
        groceryItemList1 = DataHolder.getInstance().getGroceryItemMapping().get(category1);
        groceryItemList2 = new ArrayList<>();
        for (GroceryItem item : groceryItemList1) {
            if (item.getCatLevel().size() > 1){
                if (item.getCatLevel().get(1).equals(category2))
                    groceryItemList2.add(item);
            }
        }

        layoutManager = new HybridLayoutManager(this);
        groceryItemRecycler = (RecyclerView) findViewById(R.id.grocery_item_recycler);
        groceryItemRecycler.setLayoutManager(layoutManager.getLayoutManager(150));
        groceryItemRecycler.setHasFixedSize(true);
        groceryItemRecyclerAdapter = new GroceryItemRecyclerAdapter(this, groceryItemList2, category1 + " -> " + category2);
        groceryItemRecycler.setAdapter(groceryItemRecyclerAdapter);

        getSupportActionBar().setTitle(category1 + " -> " + category2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grocery_item, menu);
        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        cartCount = (TextView) menuCart.findViewById(R.id.cart_count);
        DataHolder.getInstance().getUserDatabaseReference().child("grocery_cart").getRef().addValueEventListener(new ValueEventListener() {
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
        if (id == R.id.action_cart) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

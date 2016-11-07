package com.equinox.qikexpress.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.equinox.qikexpress.Adapters.GroceryCartRecyclerAdapter;
import com.equinox.qikexpress.Adapters.GroceryItemRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroceryShoppingCartActivity extends AppCompatActivity {

    private List<GroceryItemCart> groceryItemCartList = new ArrayList<>();
    private RecyclerView groceryShoppingList;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference groceryCart = DataHolder.getInstance().getUserDatabaseReference().child("grocery_cart").getRef();
        groceryCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iteratorCart = dataSnapshot.getChildren().iterator();
                    while (iteratorCart.hasNext())
                        groceryItemCartList.add((GroceryItemCart) iteratorCart.next().getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {         }
        });

        groceryShoppingList = (RecyclerView) findViewById(R.id.grocery_shopping_cart_list);
        groceryShoppingList.setLayoutManager(new LinearLayoutManager(this));
        groceryShoppingList.setHasFixedSize(true);
        groceryShoppingList.setAdapter(new GroceryCartRecyclerAdapter(groceryItemCartList, groceryCart));
    }

}

package com.equinox.qikexpress.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.ViewHolders.GroceryCartViewHolder;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by mukht on 11/7/2016.
 */

public class GroceryCartRecyclerAdapter extends RecyclerView.Adapter<GroceryCartViewHolder> {

    private List<GroceryItemCart> groceryItemCartList;
    private DatabaseReference groceryCart;

    public GroceryCartRecyclerAdapter(List<GroceryItemCart> groceryItemCartList, DatabaseReference groceryCart) {
        this.groceryItemCartList = groceryItemCartList;
    }


    @Override
    public GroceryCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item_list_child_items, parent, false);
        return new GroceryCartViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(GroceryCartViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

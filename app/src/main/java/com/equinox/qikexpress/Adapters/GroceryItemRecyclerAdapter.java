package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.ViewHolders.GroceryItemRecyclerViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 11/5/2016.
 */
public class GroceryItemRecyclerAdapter extends RecyclerView.Adapter<GroceryItemRecyclerViewHolder>{

    private Activity activity;
    private List<GroceryItem> groceryList;
    private DatabaseReference userDatabaseReference = DataHolder.getInstance().getUserDatabaseReference();
    private DatabaseReference groceryItemCart;
    private String categoryChain;

    public GroceryItemRecyclerAdapter(Activity activity, List<GroceryItem> groceryList, String categoryChain) {
        this.activity = activity;
        this.groceryList = groceryList;
        this.categoryChain = categoryChain;
        groceryItemCart = userDatabaseReference.child("grocery_cart").getRef();
    }

    @Override
    public GroceryItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item_list_child_items, parent, false);
        return new GroceryItemRecyclerViewHolder(holder, activity);
    }

    @Override
    public void onBindViewHolder(final GroceryItemRecyclerViewHolder holder, final int position) {
        final GroceryItem groceryItem = groceryList.get(position);
        final Boolean[] addedToCart = {false};
        if (groceryItem.getGroceryItemImage() != null)
            holder.getItemImg().setImageUrl(groceryItem.getGroceryItemImage(), DataHolder.getInstance().getImageLoader());
        holder.getGroceryItemName().setText(groceryItem.getGroceryItemName());
        holder.getGroceryItemPrice().setText(groceryItem.getGroceryItemPriceValue() == null ? "N/A" : groceryItem.getGroceryItemPriceValue().toString());
        groceryItemCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(groceryItem.getGroceryId()+groceryItem.getGroceryItemId())) {
                    addedToCart[0] = true;
                    holder.getFabAddCart().setImageResource(R.drawable.ic_remove_shopping_cart_white_48dp);
                }
                else {
                    addedToCart[0] = false;
                    holder.getFabAddCart().setImageResource(R.drawable.ic_add_shopping_cart_white_48dp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        final Handler itemCartHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 0) {
                    Snackbar.make(holder.getItemCardGrocery(), "Added to Cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Map<String, Object> groceryItemMap = groceryItem.toMap();
                    Map<String, Object> cartItemAdd = new HashMap<>();
                    cartItemAdd.put(groceryItem.getGroceryId()+groceryItem.getGroceryItemId(), groceryItemMap);
                    groceryItemCart.updateChildren(cartItemAdd);
                    holder.getFabAddCart().setImageResource(R.drawable.ic_remove_shopping_cart_white_48dp);
                }
                else {
                    Snackbar.make(holder.getItemCardGrocery(), "Removed from Cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    groceryItemCart.child(groceryItem.getGroceryId()+groceryItem.getGroceryItemId()).removeValue();
                    holder.getFabAddCart().setImageResource(R.drawable.ic_add_shopping_cart_white_48dp);
                }
                return false;
            }
        });
        holder.getFabAddCart().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                if (!addedToCart[0]) message.arg1 = 0;
                else message.arg1 = 1;
                itemCartHandler.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }


}

package com.equinox.qikexpress.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCart;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.GroceryCartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by mukht on 11/7/2016.
 */

public class GroceryCartRecyclerAdapter extends RecyclerView.Adapter<GroceryCartViewHolder> {

    private List<GroceryItemCart> groceryItemCartList;
    private DatabaseReference groceryCart;

    public GroceryCartRecyclerAdapter(List<GroceryItemCart> groceryItemCartList, DatabaseReference groceryCart) {
        this.groceryCart = groceryCart;
        this.groceryItemCartList = groceryItemCartList;
    }

    @Override
    public GroceryCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_cart_list_item, parent, false);
        return new GroceryCartViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(GroceryCartViewHolder holder, final int position) {
        final GroceryItemCart groceryItemCart = groceryItemCartList.get(position);
        final String databaseKey = groceryItemCart.getPlaceId()+groceryItemCart.getItemId();
        holder.getGroceryName().setText(StringManipulation.CapsFirst(groceryItemCart.getPlaceName()));
        holder.getGroceryItemName().setText(groceryItemCart.getItemName());
        holder.getGroceryItemPrice().setText(groceryItemCart.getItemPriceValue() != null
                ? DataHolder.localCurrency + " " + groceryItemCart.getItemPriceValue().toString() : "N/A");
        holder.getGroceryItemImage().setImageUrl(groceryItemCart.getItemImage(),
                DataHolder.getInstance().getImageLoader());
        holder.getSaveForLaterSwitch().setChecked(groceryItemCart.getSaveForLater());
        holder.getSaveForLaterSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            groceryCart.child(databaseKey).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("itemId")) {
                        groceryCart.child(databaseKey).child("saveForLater").setValue(isChecked);
                        groceryItemCartList.get(position).setSaveForLater(isChecked);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {    }
            });
            }
        });
        holder.getQuantityFetcher().setMinValue(1);
        holder.getQuantityFetcher().setMaxValue(20);
        String [] degreesValues = new String [20];
        for(int i=0; i<20;i++)
            degreesValues[i] = String.valueOf(i+1);
        holder.getQuantityFetcher().setDisplayedValues(degreesValues);
        holder.getQuantityFetcher().setValue(groceryItemCart.getItemQuantity());
        holder.getQuantityFetcher().setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                groceryCart.child(databaseKey).child("itemQuantity").setValue(newVal);
                groceryItemCartList.get(position).setItemQuantity(newVal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryItemCartList.size();
    }
}

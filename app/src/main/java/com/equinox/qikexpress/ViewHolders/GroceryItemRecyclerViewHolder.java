package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Activities.GroceryItemsMainActivity;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryItemRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CardView itemCardGrocery;
    private TextView groceryItemName, groceryItemPrice;
    private NetworkImageView itemImg;
    private Activity activity;
    private FloatingActionButton fabAddCart;

    public GroceryItemRecyclerViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        itemCardGrocery = (CardView) itemView.findViewById(R.id.grocery_item_card);
        groceryItemName = (TextView) itemView.findViewById(R.id.grocery_item_name);
        groceryItemPrice = (TextView) itemView.findViewById(R.id.grocery_item_price);
        itemImg = (NetworkImageView) itemView.findViewById(R.id.grocery_item_image);
        fabAddCart = (FloatingActionButton) itemView.findViewById(R.id.fab_add_cart);
        itemImg.setOnClickListener(this);
    }

    public CardView getItemCardGrocery() {
        return itemCardGrocery;
    }
    public TextView getGroceryItemName() {
        return groceryItemName;
    }
    public TextView getGroceryItemPrice() {
        return groceryItemPrice;
    }
    public NetworkImageView getItemImg() {
        return itemImg;
    }
    public FloatingActionButton getFabAddCart() { return fabAddCart; }

    @Override
    public void onClick(View v) {
        int itemPosition = getLayoutPosition();
        Intent itemIntent = new Intent(activity, GroceryItemsMainActivity.class);
        itemIntent.putExtra("PLACE_ID", DataHolder.groceryList.get(itemPosition).getPlaceId());
        activity.startActivity(itemIntent);
    }
}

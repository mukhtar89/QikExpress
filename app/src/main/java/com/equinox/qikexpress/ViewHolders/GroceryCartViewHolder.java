package com.equinox.qikexpress.ViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.R;

/**
 * Created by mukht on 11/7/2016.
 */
public class GroceryCartViewHolder extends RecyclerView.ViewHolder {

    private CardView groceryCardView;
    private NetworkImageView groceryItemImage;
    private TextView groceryName, groceryItemName, groceryItemPrice;
    private NumberPicker quantityFetcher;

    public GroceryCartViewHolder(View itemView) {
        super(itemView);
        groceryCardView = (CardView) itemView.findViewById(R.id.grocery_cart_card_view);
        groceryItemImage = (NetworkImageView) itemView.findViewById(R.id.grocery_cart_item_image);
        groceryName = (TextView) itemView.findViewById(R.id.grocery_cart_grocery_name);
        groceryItemName = (TextView) itemView.findViewById(R.id.grocery_cart_item_name);
        groceryItemPrice = (TextView) itemView.findViewById(R.id.grocery_cart_item_price);
        quantityFetcher = (NumberPicker) itemView.findViewById(R.id.grocery_cart_quantity);
    }
}

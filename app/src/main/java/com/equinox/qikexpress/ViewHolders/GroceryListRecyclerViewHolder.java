package com.equinox.qikexpress.ViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryListRecyclerViewHolder extends RecyclerView.ViewHolder {

    private CardView listCardGrocery;
    private TextView groceryName, groceryDist;
    private NetworkImageView backImg, profileImg;

    public GroceryListRecyclerViewHolder(View itemView) {
        super(itemView);
        listCardGrocery = (CardView) itemView.findViewById(R.id.list_card_grocery);
        groceryName = (TextView) itemView.findViewById(R.id.grocery_name);
        groceryDist = (TextView) itemView.findViewById(R.id.grocery_dist);
        backImg = (NetworkImageView) itemView.findViewById(R.id.back_image);
        profileImg = (NetworkImageView) itemView.findViewById(R.id.profile_img);
    }

    public CardView getListCardGrocery() {
        return listCardGrocery;
    }

    public TextView getGroceryName() {
        return groceryName;
    }

    public TextView getGroceryDist() {
        return groceryDist;
    }

    public NetworkImageView getBackImg() {
        return backImg;
    }

    public NetworkImageView getProfileImg() {
        return profileImg;
    }
}

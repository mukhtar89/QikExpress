package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.content.Intent;
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
public class GroceryListRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CardView listCardGrocery;
    private TextView groceryName, groceryDist;
    private NetworkImageView backImg, profileImg;
    private Activity activity;

    public GroceryListRecyclerViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        listCardGrocery = (CardView) itemView.findViewById(R.id.list_card_grocery);
        groceryName = (TextView) itemView.findViewById(R.id.grocery_name);
        groceryDist = (TextView) itemView.findViewById(R.id.grocery_dist);
        backImg = (NetworkImageView) itemView.findViewById(R.id.back_image);
        profileImg = (NetworkImageView) itemView.findViewById(R.id.profile_img);
        itemView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int itemPosition = getLayoutPosition();
        Intent itemIntent = new Intent(activity, GroceryItemsMainActivity.class);
        itemIntent.putExtra("PLACE_ID", DataHolder.groceryList.get(itemPosition).getPlaceId());
        activity.startActivity(itemIntent);
    }
}

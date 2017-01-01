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
public class RestaurantListRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CardView listCardRestaurant;
    private TextView restaurantName, restaurantDist;
    private NetworkImageView backImg, profileImg;
    private Activity activity;

    public RestaurantListRecyclerViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        listCardRestaurant = (CardView) itemView.findViewById(R.id.list_card_restaurant);
        restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
        restaurantDist = (TextView) itemView.findViewById(R.id.restaurant_dist);
        backImg = (NetworkImageView) itemView.findViewById(R.id.back_image);
        profileImg = (NetworkImageView) itemView.findViewById(R.id.profile_img);
        itemView.setOnClickListener(this);
    }

    public CardView getListCardRestaurant() {
        return listCardRestaurant;
    }
    public TextView getRestaurantName() {
        return restaurantName;
    }
    public TextView getRestaurantDist() {
        return restaurantDist;
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
        itemIntent.putExtra("PLACE_ID", DataHolder.restaurantList.get(itemPosition).getPlaceId());
        activity.startActivity(itemIntent);
    }
}

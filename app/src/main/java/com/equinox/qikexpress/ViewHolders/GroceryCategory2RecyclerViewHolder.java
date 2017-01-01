package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Activities.GroceryItemOverviewActivity;
import com.equinox.qikexpress.Activities.GroceryItemsMainActivity;
import com.equinox.qikexpress.Fragments.GroceryItemOverviewFragment;
import com.equinox.qikexpress.R;

import java.util.List;

import static com.equinox.qikexpress.Models.DataHolder.mTwoPane;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryCategory2RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CardView category2Card;
    private TextView groceryCategory2Name;
    private NetworkImageView category2Img;
    private Activity activity;
    private String category1;
    private List<String> category2List;
    private String placeId;

    public GroceryCategory2RecyclerViewHolder(View itemView, Activity activity,
                                              List<String> category2List, String category1, String placeId) {
        super(itemView);
        this.activity = activity;
        this.category1 = category1;
        this.category2List = category2List;
        this.placeId = placeId;
        category2Card = (CardView) itemView.findViewById(R.id.grocery_category2_card);
        groceryCategory2Name = (TextView) itemView.findViewById(R.id.grocery_category2_name);
        category2Img = (NetworkImageView) itemView.findViewById(R.id.grocery_category2_image);
        itemView.setOnClickListener(this);
    }

    public CardView getCategory2Card() {
        return category2Card;
    }
    public TextView getGroceryCategory2Name() {
        return groceryCategory2Name;
    }
    public NetworkImageView getCategory2Img() {
        return category2Img;
    }

    @Override
    public void onClick(View v) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString("CATEGORY1", category1);
            arguments.putString("CATEGORY2", category2List.get(getLayoutPosition()));
            arguments.putString("PLACE_ID", placeId);
            GroceryItemOverviewFragment fragment = new GroceryItemOverviewFragment();
            fragment.setArguments(arguments);
            ((GroceryItemsMainActivity)activity).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.grocery_item_overview_container, fragment)
                    .commit();
            if (!placeMap.get(placeId).getPartner()) {
                Snackbar.make(v,
                        "The price and availability is at the discretion of the outlet.", Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {
            Intent itemIntent = new Intent(activity, GroceryItemOverviewActivity.class);
            itemIntent.putExtra("CATEGORY1", category1);
            itemIntent.putExtra("CATEGORY2", category2List.get(getLayoutPosition()));
            itemIntent.putExtra("PLACE_ID", placeId);
            activity.startActivity(itemIntent);
        }
    }
}

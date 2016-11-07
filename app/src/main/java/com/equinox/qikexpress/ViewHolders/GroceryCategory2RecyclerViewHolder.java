package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Activities.GroceryItemActivity;
import com.equinox.qikexpress.R;

import java.util.List;

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

    public GroceryCategory2RecyclerViewHolder(View itemView, Activity activity, List<String> category2List, String category1) {
        super(itemView);
        this.activity = activity;
        this.category1 = category1;
        this.category2List = category2List;
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
        Intent itemIntent = new Intent(activity, GroceryItemActivity.class);
        itemIntent.putExtra("CATEGORY1", category1);
        itemIntent.putExtra("CATEGORY2", category2List.get(getLayoutPosition()));
        activity.startActivity(itemIntent);
    }
}

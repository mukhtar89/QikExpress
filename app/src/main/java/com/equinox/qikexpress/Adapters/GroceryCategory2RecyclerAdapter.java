package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.GroceryCategory2RecyclerViewHolder;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryCategory2RecyclerAdapter extends RecyclerView.Adapter<GroceryCategory2RecyclerViewHolder> {

    private Activity activity;
    private Map<String,String> categoryImageMap;
    private List<String> category2List;
    private String category1;

    public GroceryCategory2RecyclerAdapter(Activity groceryListActivity, Map<String,String> categoryImageMap,
                                           List<String> category2List, String category1) {
        this.activity = groceryListActivity;
        this.categoryImageMap = categoryImageMap;
        this.category2List = category2List;
        this.category1 = category1;
    }

    @Override
    public GroceryCategory2RecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_category2_child_items, parent, false);
            return new GroceryCategory2RecyclerViewHolder(holder, activity, category2List, category1);
    }

    @Override
    public void onBindViewHolder(final GroceryCategory2RecyclerViewHolder holder, int position) {
        String category = category2List.get(position);
        String catImageURL = categoryImageMap.get(category);
        holder.getGroceryCategory2Name().setText(category);
        if (catImageURL != null)
            holder.getCategory2Img().setImageUrl(catImageURL, DataHolder.getInstance().getImageLoader());
    }

    @Override
    public int getItemCount() {
        return category2List.size();
    }
}

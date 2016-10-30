package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.equinox.qikexpress.Activities.GroceryListActivity;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.Utils.MapUtils.GMapUtils;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.equinox.qikexpress.ViewHolders.MainListViewHolder;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryListRecyclerAdapter extends RecyclerView.Adapter<GroceryListRecyclerViewHolder> {

    private Activity activity;
    private List<Grocery> groceryList;
    private Location location;
    private Handler handleDistance;
    private DistanceRequest distanceRequest;
    private ImageLoader imageLoader = AppVolleyController.getInstance().getImageLoader();

    public GroceryListRecyclerAdapter(Activity groceryListActivity, List<Grocery> groceryList, Location location) {
        this.activity = groceryListActivity;
        this.groceryList = groceryList;
        this.location = location;
    }

    @Override
    public GroceryListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_main_list, parent, false);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int itemPosition = recyclerView.indexOfChild(v);
                /*activity.startActivity(new Intent(activity, GroceryListActivity.class));
                activity.finish();*/
            }
        });
        return new GroceryListRecyclerViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(final GroceryListRecyclerViewHolder holder, int position) {
        Grocery grocery = groceryList.get(position);
        if (imageLoader == null)
            imageLoader = AppVolleyController.getInstance().getImageLoader();
        holder.getGroceryName().setText(grocery.getGroceryName());
        if (grocery.getBackImg() != null)
            holder.getBackImg().setImageUrl(grocery.getBackImg(), imageLoader);
        if (grocery.getProfileImg() != null)
            holder.getProfileImg().setImageUrl(grocery.getProfileImg(), imageLoader);
        handleDistance = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String[] params = (String[]) msg.obj;
                holder.getGroceryDist().setText(params[0] +"   |   " + params[1]);
                return false;
            }
        });
        distanceRequest = new DistanceRequest(handleDistance);
        distanceRequest.execute(new LatLng(location.getLatitude(), location.getLongitude()),
                new LatLng(grocery.getLatitude().doubleValue(), grocery.getLongitude().doubleValue()));
        /*new GMapUtils().execute(location.getLatitude(), location.getLongitude(),
                grocery.getLatitude().doubleValue(), grocery.getLongitude().doubleValue(), handleDistance);*/
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }
}

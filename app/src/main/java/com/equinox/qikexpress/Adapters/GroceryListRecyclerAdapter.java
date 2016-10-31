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
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.Utils.MapUtils.GMapUtils;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.equinox.qikexpress.ViewHolders.LoadMoreViewHolder;
import com.equinox.qikexpress.ViewHolders.MainListViewHolder;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Grocery> groceryList;
    private Location location;
    private Handler handleDistance;
    private DistanceRequest distanceRequest;
    //private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 2;
    private Handler loadMoreAction;
    private ImageLoader imageLoader = AppVolleyController.getInstance().getImageLoader();

    public GroceryListRecyclerAdapter(Activity groceryListActivity, List<Grocery> groceryList, Location location, Handler loadMoreAction) {
        this.activity = groceryListActivity;
        this.groceryList = groceryList;
        this.location = location;
        this.loadMoreAction = loadMoreAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
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
            case TYPE_FOOTER:
                View loadMoreButton = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_button, parent, false);
                return new LoadMoreViewHolder(loadMoreButton);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case TYPE_ITEM:
                if (holder instanceof GroceryListRecyclerViewHolder) {
                    final Grocery grocery = groceryList.get(position);
                    if (imageLoader == null)
                        imageLoader = AppVolleyController.getInstance().getImageLoader();
                    ((GroceryListRecyclerViewHolder) holder).getGroceryName().setText(StringManipulation.CapsFirst(grocery.getName()));
                    String photoURL = grocery.getPhoto().returnApiUrl(Constants.PLACES_API_KEY);
                    if (!photoURL.isEmpty())
                        ((GroceryListRecyclerViewHolder) holder).getBackImg().setImageUrl(photoURL, imageLoader);
                    handleDistance = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            String[] params = (String[]) msg.obj;
                            ((GroceryListRecyclerViewHolder) holder).getGroceryDist().setText(params[0] +"   |   " + params[1]);
                            grocery.setDistanceFromCurrent(params[0]);
                            grocery.setTimeFromCurrent(params[1]);
                            return false;
                        }
                    });
                    distanceRequest = new DistanceRequest(handleDistance);
                    distanceRequest.execute(new LatLng(location.getLatitude(), location.getLongitude()),
                            new LatLng(grocery.getLocation().latitude, grocery.getLocation().longitude));
                }

                break;
            case TYPE_FOOTER:
                ((LoadMoreViewHolder) holder).getLoadMoreButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMoreAction.sendMessage(new Message());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return groceryList.size()+1;
    }
}

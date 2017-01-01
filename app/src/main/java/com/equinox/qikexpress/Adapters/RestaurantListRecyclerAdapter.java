package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.equinox.qikexpress.Filters.GroceryFilter;
import com.equinox.qikexpress.Filters.RestaurantFilter;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Restaurant;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.equinox.qikexpress.ViewHolders.LoadMoreViewHolder;
import com.equinox.qikexpress.ViewHolders.RestaurantListRecyclerViewHolder;

import java.util.List;

/**
 * Created by mukht on 10/29/2016.
 */
public class RestaurantListRecyclerAdapter extends BaseListRecyclerAdapterFilter {

    private Activity activity;
    private List<Restaurant> restaurantList;
    private Handler loadMoreAction;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 2;
    private RestaurantFilter restaurantFilter;

    public RestaurantListRecyclerAdapter(Activity groceryListActivity, List<Restaurant> restaurantList, Handler loadMoreAction) {
        this.activity = groceryListActivity;
        this.restaurantList = restaurantList;
        this.loadMoreAction = loadMoreAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_main_list, parent, false);
                return new RestaurantListRecyclerViewHolder(holder, activity);
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
                if (holder instanceof RestaurantListRecyclerViewHolder) {
                    final Restaurant restaurant = restaurantList.get(position);
                    ((RestaurantListRecyclerViewHolder) holder).getRestaurantName().setText(StringManipulation.CapsFirst(restaurant.getName()));
                    if (restaurant.getPhoto() != null) {
                        String photoURL = restaurant.getPhoto().returnApiUrl(Constants.PLACES_API_KEY);
                        if (!photoURL.isEmpty())
                            ((RestaurantListRecyclerViewHolder) holder).getBackImg().setImageUrl(photoURL, DataHolder.getInstance().getImageLoader());
                    }
                    if (restaurant.getDistanceFromCurrent() != null)
                        ((RestaurantListRecyclerViewHolder) holder).getRestaurantDist()
                                .setText(restaurant.getDistanceFromCurrent() + " km   |   " + restaurant.getTimeFromCurrent() + " min");
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
        return restaurantList.size()+1;
    }

    @Override
    public Filter getFilter() {
        if (restaurantFilter == null)
            restaurantFilter = new RestaurantFilter(restaurantList, this);
        return restaurantFilter;
    }
}

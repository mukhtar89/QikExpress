package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.equinox.qikexpress.Filters.GroceryFilter;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.equinox.qikexpress.ViewHolders.LoadMoreViewHolder;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mukht on 10/29/2016.
 */
public class GroceryListRecyclerAdapter extends BaseListRecyclerAdapterFilter {

    private Activity activity;
    private List<Grocery> groceryList;
    private Handler loadMoreAction;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 2;
    private GroceryFilter groceryFilter;

    public GroceryListRecyclerAdapter(Activity groceryListActivity, List<Grocery> groceryList, Handler loadMoreAction) {
        this.activity = groceryListActivity;
        this.groceryList = groceryList;
        this.loadMoreAction = loadMoreAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_main_list, parent, false);
                return new GroceryListRecyclerViewHolder(holder, activity);
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
                    ((GroceryListRecyclerViewHolder) holder).getGroceryName().setText(StringManipulation.CapsFirst(grocery.getName()));
                    if (grocery.getPhoto() != null) {
                        String photoURL = grocery.getPhoto().returnApiUrl(Constants.PLACES_API_KEY);
                        if (!photoURL.isEmpty())
                            ((GroceryListRecyclerViewHolder) holder).getBackImg().setImageUrl(photoURL, DataHolder.getInstance().getImageLoader());
                    }
                    if (grocery.getDistanceFromCurrent() != null)
                        ((GroceryListRecyclerViewHolder) holder).getGroceryDist()
                                .setText(grocery.getDistanceFromCurrent() + " km   |   " + grocery.getTimeFromCurrent() + " min");
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

    @Override
    public Filter getFilter() {
        if (groceryFilter == null)
            groceryFilter = new GroceryFilter(groceryList, this);
        return groceryFilter;
    }
}

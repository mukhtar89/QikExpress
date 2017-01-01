package com.equinox.qikexpress.Filters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 12/19/2016.
 */

public class RestaurantFilter extends Filter {

    private List<Restaurant> restaurantList, fullRestaurantList;
    private RecyclerView.Adapter groceryListAdapter;

    public RestaurantFilter(List<Restaurant> restaurantList, RecyclerView.Adapter groceryListAdapter) {
        this.restaurantList = restaurantList;
        fullRestaurantList = new ArrayList<>();
        this.groceryListAdapter = groceryListAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (restaurantList.size() > fullRestaurantList.size()) {
            fullRestaurantList.clear();
            fullRestaurantList.addAll(restaurantList);
        }
        FilterResults filterResults = new FilterResults();
        if (constraint !=null && constraint.length() > 0) {
            List<Restaurant> tempList = new ArrayList<>();
            for (Restaurant restaurant : fullRestaurantList) {
                if (restaurant.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    tempList.add(restaurant);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
        } else {
            filterResults.count = fullRestaurantList.size();
            filterResults.values = fullRestaurantList;
        }
        return filterResults;
    }

    /**
     * Notify about filtered list to ui
     * @param constraint text
     * @param results filtered result
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        restaurantList.clear();
        restaurantList.addAll((ArrayList<Restaurant>) results.values);
        groceryListAdapter.notifyDataSetChanged();
    }
}

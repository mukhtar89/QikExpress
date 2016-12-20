package com.equinox.qikexpress.Filters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import com.equinox.qikexpress.Models.Grocery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 12/19/2016.
 */

public class GroceryFilter extends Filter {

    private List<Grocery> groceryList, fullGroceryList;
    private RecyclerView.Adapter groceryListAdapter;

    public GroceryFilter(List<Grocery> groceryList, RecyclerView.Adapter groceryListAdapter) {
        this.groceryList = groceryList;
        fullGroceryList = new ArrayList<>();
        this.groceryListAdapter = groceryListAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (groceryList.size() > fullGroceryList.size()) {
            fullGroceryList.clear();
            fullGroceryList.addAll(groceryList);
        }
        FilterResults filterResults = new FilterResults();
        if (constraint !=null && constraint.length() > 0) {
            List<Grocery> tempList = new ArrayList<>();
            for (Grocery grocery : fullGroceryList) {
                if (grocery.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    tempList.add(grocery);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
        } else {
            filterResults.count = fullGroceryList.size();
            filterResults.values = fullGroceryList;
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
        groceryList.clear();
        groceryList.addAll((ArrayList<Grocery>) results.values);
        groceryListAdapter.notifyDataSetChanged();
    }
}

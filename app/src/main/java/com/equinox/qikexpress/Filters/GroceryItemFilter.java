package com.equinox.qikexpress.Filters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import com.equinox.qikexpress.Models.GroceryItemCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 12/19/2016.
 */

public class GroceryItemFilter extends Filter {

    private List<GroceryItemCollection> groceryItemList, fullGroceryItemList;
    private RecyclerView.Adapter groceryItemListAdapter;

    public GroceryItemFilter(List<GroceryItemCollection> groceryItemList, RecyclerView.Adapter groceryItemListAdapter) {
        this.groceryItemList = groceryItemList;
        fullGroceryItemList = new ArrayList<>();
        fullGroceryItemList.addAll(groceryItemList);
        this.groceryItemListAdapter = groceryItemListAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (constraint !=null && constraint.length() > 0) {
            List<GroceryItemCollection> tempList = new ArrayList<>();
            // search content in friend list
            for (GroceryItemCollection groceryItemCollection : fullGroceryItemList) {
                if (groceryItemCollection.getItemName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    tempList.add(groceryItemCollection);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
        } else {
            filterResults.count = fullGroceryItemList.size();
            filterResults.values = fullGroceryItemList;
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
        groceryItemList.clear();
        groceryItemList.addAll((ArrayList<GroceryItemCollection>) results.values);
        groceryItemListAdapter.notifyDataSetChanged();
    }
}

package com.equinox.qikexpress.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.equinox.qikexpress.Filters.GroceryFilter;

/**
 * Created by mukht on 12/31/2016.
 */

public abstract class BaseListRecyclerAdapterFilter extends RecyclerView.Adapter implements Filterable {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}

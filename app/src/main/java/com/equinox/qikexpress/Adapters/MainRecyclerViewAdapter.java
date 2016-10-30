package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equinox.qikexpress.Activities.GroceryListActivity;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.ViewHolders.MainListViewHolder;

/**
 * Created by mukht on 10/29/2016.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainListViewHolder> {

    private Activity activity;

    public MainRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_list, parent, false);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int itemPosition = recyclerView.indexOfChild(v);
                activity.startActivity(new Intent(activity, GroceryListActivity.class));
            }
        });
        return new MainListViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(MainListViewHolder holder, int position) {
        holder.getCardText().setText(QikList.values()[position].getListName());
        holder.getCardIcon().setImageDrawable(activity.getResources().getDrawable(QikList.values()[position].getIcon()));
        holder.getListCard().setBackground(activity.getResources().getDrawable(QikList.values()[position].getBackground()));
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

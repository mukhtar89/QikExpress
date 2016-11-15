package com.equinox.qikexpress.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/31/2016.
 */

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout loadMoreButton;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        loadMoreButton = (LinearLayout) itemView.findViewById(R.id.load_more_items);
    }

    public LinearLayout getLoadMoreButton() {
        return loadMoreButton;
    }

    public void setLoadMoreButton(LinearLayout loadMoreButton) {
        this.loadMoreButton = loadMoreButton;
    }
}

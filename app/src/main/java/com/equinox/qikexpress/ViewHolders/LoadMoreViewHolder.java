package com.equinox.qikexpress.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/31/2016.
 */

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    private Button loadMoreButton;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        loadMoreButton = (Button) itemView.findViewById(R.id.load_more_items);
    }

    public Button getLoadMoreButton() {
        return loadMoreButton;
    }

    public void setLoadMoreButton(Button loadMoreButton) {
        this.loadMoreButton = loadMoreButton;
    }
}

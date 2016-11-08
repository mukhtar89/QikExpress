package com.equinox.qikexpress.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.RatingsManager;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.equinox.qikexpress.ViewHolders.ReviewViewHolder;

import java.util.ArrayList;

/**
 * Created by mukht on 11/2/2016.
 */

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private ArrayList<RatingsManager> ratings;

    public ReviewRecyclerAdapter(ArrayList<RatingsManager> ratings) {
        this.ratings = ratings;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        RatingsManager manager = ratings.get(position);
        holder.getAuthorName().setText(manager.getAuthorName());
        if (!manager.getRatingReview().isEmpty())
            holder.getReviewText().setText("\"" + manager.getRatingReview() + "\"");
        holder.getTimeSubmitted().setText(StringManipulation.getFormattedDate(manager.getTimeSubmitted()));
        double ratingValue = (float) manager.getRatingValue();
        holder.getRatingBar().setRating((float) ratingValue);
        if (manager.getAuthorPhotoURL() != null)
            holder.getAuthorImage().setImageUrl(manager.getAuthorPhotoURL().substring(2), DataHolder.getInstance().getImageLoader());
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }
}

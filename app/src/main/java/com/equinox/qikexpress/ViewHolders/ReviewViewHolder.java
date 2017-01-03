package com.equinox.qikexpress.ViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.R;

import org.w3c.dom.Text;

/**
 * Created by mukht on 11/2/2016.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout reviewCard;
    private NetworkImageView authorImage;
    private TextView authorName, timeSubmitted, reviewText;
    private RatingBar ratingBar;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        reviewCard = (LinearLayout) itemView.findViewById(R.id.list_card_review);
        authorImage= (NetworkImageView) itemView.findViewById(R.id.author_image);
        authorName = (TextView) itemView.findViewById(R.id.author_name);
        timeSubmitted = (TextView) itemView.findViewById(R.id.time_submitted);
        reviewText = (TextView) itemView.findViewById(R.id.rating_text);
        ratingBar = (RatingBar) itemView.findViewById(R.id.rating_value);
    }

    public LinearLayout getReviewCard() {
        return reviewCard;
    }

    public NetworkImageView getAuthorImage() {
        return authorImage;
    }

    public TextView getAuthorName() {
        return authorName;
    }

    public TextView getTimeSubmitted() {
        return timeSubmitted;
    }

    public TextView getReviewText() {
        return reviewText;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }
}

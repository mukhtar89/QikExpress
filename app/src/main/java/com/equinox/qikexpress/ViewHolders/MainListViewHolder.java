package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.equinox.qikexpress.Activities.ShopListActivity;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/29/2016.
 */

public class MainListViewHolder extends RecyclerView.ViewHolder {

    private CardView listCard;
    private TextView cardText;
    private ImageView cardIcon;
    private Activity activity;

    public MainListViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        listCard = (CardView) itemView.findViewById(R.id.listCard);
        cardText = (TextView) itemView.findViewById(R.id.card_text);
        cardIcon = (ImageView) itemView.findViewById(R.id.card_icon);
    }

    public CardView getListCard() {
        return listCard;
    }
    public TextView getCardText() {
        return cardText;
    }
    public ImageView getCardIcon() {
        return cardIcon;
    }
}

package com.equinox.qikexpress.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Item;
import com.equinox.qikexpress.R;

import java.util.List;

/**
 * Created by mukht on 11/9/2016.
 */

public class CheckoutListAdapter extends BaseAdapter {

    private List<Item> checkoutList;
    private Context context;

    public CheckoutListAdapter(List<Item> checkoutItemsList, Context context) {
        this.checkoutList = checkoutItemsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return checkoutList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = checkoutList.get(position);
        CheckoutViewHolder holder;
        final View result;
        if (convertView == null) {
            holder = new CheckoutViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.checkout_list_item, parent, false);
            holder.itemName = (TextView) convertView.findViewById(R.id.checkout_item_name);
            holder.itemPrice = (TextView) convertView.findViewById(R.id.checkout_item_price);
            holder.itemQuantity = (TextView) convertView.findViewById(R.id.checkout_item_quantity);
            holder.placeName = (TextView) convertView.findViewById(R.id.checkout_grocery_name);
            holder.itemImage = (NetworkImageView) convertView.findViewById(R.id.checkout_item_image);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (CheckoutViewHolder) convertView.getTag();
            result = convertView;
        }
        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText(item.getItemPriceValue() != null ?
                DataHolder.localCurrency + " " + item.getItemPriceValue() : "N/A");
        holder.itemQuantity.setText(item.getItemQuantity().toString());
        holder.placeName.setText(item.getPlaceName());
        holder.itemImage.setImageUrl(item.getItemImage(), DataHolder.getInstance().getImageLoader());
        return convertView;
    }

    public static class CheckoutViewHolder {
        public TextView itemName, itemPrice, placeName, itemQuantity;
        public NetworkImageView itemImage;
    }
}

package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Activities.GroceryItemDetailActivity;
import com.equinox.qikexpress.Activities.GroceryItemsMainActivity;
import com.equinox.qikexpress.Activities.SearchGroceryItemActivity;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.CartQuantityHandler;
import com.equinox.qikexpress.Utils.StringManipulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 12/7/2016.
 */
public class SearchGroceryItemAdapter extends RecyclerView.Adapter<SearchGroceryItemAdapter.GroceryItemListViewHolder>
        implements Filterable {

    private List<GroceryItemCollection> groceryItemList, fullGroceryItemList;
    private GroceryItemFilter groceryItemFilter;
    private Activity activity;

    public SearchGroceryItemAdapter(Activity activity, List<GroceryItemCollection> groceryItemList) {
        this.activity = activity;
        this.groceryItemList = groceryItemList;
        fullGroceryItemList = groceryItemList;
    }

    @Override
    public GroceryItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_grocery_item_list_layout, parent, false);
        return new GroceryItemListViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(GroceryItemListViewHolder holder, int position) {
        final GroceryItemCollection groceryItemCollection = groceryItemList.get(position);
        holder.groceryItemName.setText(groceryItemCollection.getItemName());
        if (groceryItemCollection.getCollectionSize() == 1
                && !groceryItemCollection.getItemVolLoose() && !groceryItemCollection.getItemWeightLoose()) {
            groceryItemCollection.setDefaults(0);
            if (groceryItemCollection.getItemPriceValue() != null)
                holder.groceryItemPrice.setText(groceryItemCollection.getCurrencyCode() + " " + groceryItemCollection.getItemPriceValue());
            holder.groceryItemImage.setImageUrl(groceryItemCollection.getItemImage(), DataHolder.getInstance().getImageLoader());
        } else {
            holder.groceryItemPrice.setVisibility(View.GONE);
            holder.groceryItemCartAction.setVisibility(View.GONE);
            holder.groceryItemImage.setImageUrl(groceryItemCollection.getItemImageList().get(0), DataHolder.getInstance().getImageLoader());
        }
        CartQuantityHandler cartQuantityHandler = new CartQuantityHandler(groceryItemCollection,
                holder.groceryItemCartAction, holder.cardView, activity);
        cartQuantityHandler.execute();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    class GroceryItemListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView groceryItemName, groceryItemPrice;
        NetworkImageView groceryItemImage;
        FloatingActionButton groceryItemCartAction;

        GroceryItemListViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.search_grocery_item_card);
            groceryItemPrice = (TextView) itemView.findViewById(R.id.search_grocery_item_price);
            groceryItemName = (TextView) itemView.findViewById(R.id.search_grocery_item_name);
            groceryItemImage = (NetworkImageView) itemView.findViewById(R.id.search_grocery_item_image);
            groceryItemCartAction = (FloatingActionButton) itemView.findViewById(R.id.search_grocery_item_cart_action);
        }

        @Override
        public void onClick(View v) {
            Intent groceryItemDetailIntent = new Intent(activity, GroceryItemDetailActivity.class);
            groceryItemDetailIntent.putExtra("ITEM_NAME", groceryItemList.get(getAdapterPosition()).getItemName());
            activity.startActivity(groceryItemDetailIntent);
        }
    }

    @Override
    public Filter getFilter() {
        if (groceryItemFilter == null)
            groceryItemFilter = new GroceryItemFilter();
        return groceryItemFilter;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class GroceryItemFilter extends Filter {

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
            groceryItemList = (ArrayList<GroceryItemCollection>) results.values;
            notifyDataSetChanged();
        }
    }
}


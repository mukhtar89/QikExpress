package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Activities.SearchGroceryItemActivity;
import com.equinox.qikexpress.Filters.GroceryItemFilter;
import com.equinox.qikexpress.Fragments.GroceryItemDetailFragment;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.CartQuantityHandler;

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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchGroceryItemActivity callingActivity = (SearchGroceryItemActivity) activity;
                FragmentManager fragmentManager = callingActivity.getSupportFragmentManager();
                GroceryItemDetailFragment groceryItemDetailFragment =
                        GroceryItemDetailFragment.newInstance("ITEM_NAME", groceryItemCollection.getItemName());
                groceryItemDetailFragment.show(fragmentManager, "GroceryItemDetailFragment");
            }
        });
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

    class GroceryItemListViewHolder extends RecyclerView.ViewHolder {

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
    }

    @Override
    public Filter getFilter() {
        if (groceryItemFilter == null)
            groceryItemFilter = new GroceryItemFilter(groceryItemList, this);
        return groceryItemFilter;
    }

}


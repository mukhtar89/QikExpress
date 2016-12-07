package com.equinox.qikexpress.Adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.CartQuantityHandler;
import com.equinox.qikexpress.ViewHolders.GroceryItemRecyclerViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.Constants.ITEM_QTY;

/**
 * Created by mukht on 11/5/2016.
 */
public class GroceryItemRecyclerAdapter extends RecyclerView.Adapter<GroceryItemRecyclerViewHolder>{

    private Activity activity;
    private List<GroceryItemCollection> groceryItemCollectionList;
    private CartQuantityHandler cartQuantityHandler;

    public GroceryItemRecyclerAdapter(Activity activity, List<GroceryItemCollection> groceryItemCollectionList) {
        this.activity = activity;
        this.groceryItemCollectionList = groceryItemCollectionList;
    }

    @Override
    public GroceryItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item_list_child_items, parent, false);
        return new GroceryItemRecyclerViewHolder(holder, activity);
    }

    @Override
    public void onBindViewHolder(final GroceryItemRecyclerViewHolder holder, final int position) {
        final GroceryItemCollection groceryItemCollection = groceryItemCollectionList.get(position);
        cartQuantityHandler = new CartQuantityHandler(groceryItemCollection, holder.getFabAddCart(),
                holder.getItemCardGrocery(), activity);
        if (groceryItemCollection.getItemImageList().get(0) != null)
            holder.getItemImg().setImageUrl(groceryItemCollection.getItemImageList().get(0)
                    , DataHolder.getInstance().getImageLoader());
        holder.getGroceryItemName().setText(groceryItemCollection.getItemName());
        if (groceryItemCollection.getCollectionSize() == 1
                && !groceryItemCollection.getItemVolLoose() && !groceryItemCollection.getItemWeightLoose()) {
            holder.getGroceryItemPrice().setText(groceryItemCollection.getItemPriceValueList().get(0) == null
                    ? "N/A" : groceryItemCollection.getCurrencyCode() + " "
                    + groceryItemCollection.getItemPriceValueList().get(0).toString());
            groceryItemCollection.setDefaults(0);
            cartQuantityHandler.execute();
        } else holder.getFabAddCart().setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return groceryItemCollectionList.size();
    }


}

package com.equinox.qikexpress.Fragments;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.CartQuantityHandler;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Models.DataHolder.currentGroceryItemCollections;
import static com.equinox.qikexpress.Models.DataHolder.groceryItemCollectionCat1Mapping;
import static com.equinox.qikexpress.Models.DataHolder.groceryItemCollectionCat2Mapping;

/**
 * Created by mukht on 12/22/2016.
 */

public class GroceryItemDetailFragment extends DialogFragment {

    private static final String TYPE = "type", VALUE = "value";
    private GroceryItemCollection groceryItemCollection;
    private NetworkImageView groceryItemImage, groceryItemBrandImage;
    private TextView groceryItemCategory, groceryItemBrandName;
    private TextView groceryItemPrice, groceryItemWeight, groceryItemVol, getGroceryItemCustomSize, groceryItemTitle;
    private Spinner groceryItemPriceList, groceryItemWeightList, groceryItemVolList, getGroceryItemCustomSizeList;
    private EditText groceryItemWeightLoose, groceryItemVolLoose;
    private FloatingActionButton cartFab;
    private CartQuantityHandler cartQuantityHandler;

    public static GroceryItemDetailFragment newInstance(String type, Object value) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putString(VALUE, value.toString());
        GroceryItemDetailFragment fragment = new GroceryItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder detailBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailView = inflater.inflate(R.layout.fragment_grocery_item_detail, null);

        switch (getArguments().getString(TYPE)) {
            case "ITEM_POS_CAT2":
                groceryItemCollection = groceryItemCollectionCat2Mapping.get(DataHolder.category1)
                        .get(DataHolder.category2).get(Integer.valueOf(getArguments().getString(VALUE)));
                break;
            case "ITEM_POS_CAT1":
                groceryItemCollection = groceryItemCollectionCat1Mapping.get(DataHolder.category1)
                        .get(Integer.valueOf(getArguments().getString(VALUE)));
                break;
            case "ITEM_NAME":
                groceryItemCollection = currentGroceryItemCollections.get(getArguments().getString(VALUE));
                break;
        }

        groceryItemTitle = (TextView) detailView.findViewById(R.id.grocery_item_detail_title);
        groceryItemTitle.setText(groceryItemCollection.getItemName());
        groceryItemImage = (NetworkImageView) detailView.findViewById(R.id.grocery_item_image_view);
        groceryItemBrandImage = (NetworkImageView) detailView.findViewById(R.id.grocery_item_brand_image);
        if (groceryItemCollection.getItemBrandImage() != null)
            groceryItemBrandImage.setImageUrl(groceryItemCollection.getItemBrandImage(), DataHolder.getInstance().getImageLoader());
        else groceryItemBrandImage.setVisibility(View.GONE);

        groceryItemBrandName = (TextView) detailView.findViewById(R.id.grocery_item_brand);
        if (groceryItemCollection.getItemBrandName() != null)
            groceryItemBrandName.setText("Brand: " + groceryItemCollection.getItemBrandName());
        else groceryItemBrandName.setVisibility(View.GONE);

        groceryItemCategory = (TextView) detailView.findViewById(R.id.grocery_item_category_chain);
        StringBuilder categoryChain = new StringBuilder();
        for (String category : groceryItemCollection.getCatLevel())
            categoryChain.append(" -> " + category);
        groceryItemCategory.setText("Category Chain: " + categoryChain.toString().substring(4));

        groceryItemPrice = (TextView) detailView.findViewById(R.id.grocery_item_price);
        groceryItemPriceList = (Spinner) detailView.findViewById(R.id.grocery_item_price_spinner);
        getGroceryItemCustomSize = (TextView) detailView.findViewById(R.id.grocery_item_custom_size);
        getGroceryItemCustomSizeList = (Spinner) detailView.findViewById(R.id.grocery_item_custom_size_spinner);
        groceryItemWeight = (TextView) detailView.findViewById(R.id.grocery_item_weight);
        groceryItemWeightLoose = (EditText) detailView.findViewById(R.id.grocery_item_weight_loose);
        groceryItemWeightList = (Spinner) detailView.findViewById(R.id.grocery_item_weight_spinner);
        groceryItemVol = (TextView) detailView.findViewById(R.id.grocery_item_vol);
        groceryItemVolLoose = (EditText) detailView.findViewById(R.id.grocery_item_vol_loose);
        groceryItemVolList = (Spinner) detailView.findViewById(R.id.grocery_item_vol_spinner);
        if (groceryItemCollection.getCollectionSize() == 1) {
            groceryItemCollection.setDefaults(0);
            if (groceryItemCollection.getItemImageList().get(0) != null)
                groceryItemImage.setImageUrl(groceryItemCollection.getItemImageList().get(0), DataHolder.getInstance().getImageLoader());
            getGroceryItemCustomSize.setVisibility(View.GONE);
            getGroceryItemCustomSizeList.setVisibility(View.GONE);
            groceryItemPriceList.setVisibility(View.GONE);
            groceryItemWeightList.setVisibility(View.GONE);
            groceryItemVolList.setVisibility(View.GONE);
            if (groceryItemCollection.getItemPriceValueList().get(0) != null)
                groceryItemPrice.setText("Price: " + groceryItemCollection.getCurrencyCode() + " " + groceryItemCollection.getItemPriceValueList().get(0));
            if (groceryItemCollection.getItemWeightLoose()) {
                if (groceryItemCollection.getItemPricePerWeight() != null)
                    groceryItemPrice.setText("Price: " + groceryItemCollection.getCurrencyCode() + " "
                            + groceryItemCollection.getItemPricePerWeight());
                else groceryItemPrice.setVisibility(View.GONE);
                groceryItemCollection.setItemWeight(1f);
                groceryItemCollection.setItemPriceValue(groceryItemCollection.getItemPricePerWeight());
                groceryItemWeight.setText("Enter Weight (in kg): ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    groceryItemWeight.setLabelFor(R.id.grocery_item_weight_loose);
                groceryItemWeightLoose.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().isEmpty()) {
                            if (groceryItemCollection.getItemPricePerWeight() != null) {
                                groceryItemPrice.setText("Price: " + groceryItemCollection.getCurrencyCode() + " "
                                        + (groceryItemCollection.getItemPricePerWeight() * Float.valueOf(s.toString())));
                                groceryItemCollection.setItemPriceValue(groceryItemCollection.getItemPricePerWeight()*Float.valueOf(s.toString()));
                            }
                            groceryItemCollection.setItemWeight(Float.valueOf(s.toString()));
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
            else {
                groceryItemWeightLoose.setVisibility(View.GONE);
                if (groceryItemCollection.getItemWeightList().get(0) == 0)
                    groceryItemWeight.setVisibility(View.GONE);
                else groceryItemWeight.setText("Weight: " + groceryItemCollection.getItemWeightList().get(0)
                        + " " + groceryItemCollection.getItemWeightUnitList().get(0));
            }
            if (groceryItemCollection.getItemVolLoose()) {
                if (groceryItemCollection.getItemPricePerVol() != null)
                    groceryItemPrice.setText("Price: " + groceryItemCollection.getCurrencyCode() + " "
                            + groceryItemCollection.getItemPricePerVol());
                else groceryItemPrice.setVisibility(View.GONE);
                groceryItemCollection.setItemVol(1f);
                groceryItemCollection.setItemPriceValue(groceryItemCollection.getItemPricePerVol());
                groceryItemVol.setText("Enter Volume (in litre): ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    groceryItemVol.setLabelFor(R.id.grocery_item_vol_loose);
                groceryItemVolLoose.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().isEmpty()) {
                            if (groceryItemCollection.getItemPricePerVol() != null) {
                                groceryItemPrice.setText("Price: " + groceryItemCollection.getCurrencyCode() + " "
                                        + (groceryItemCollection.getItemPricePerVol() * Float.valueOf(s.toString())));
                                groceryItemCollection.setItemPriceValue(groceryItemCollection.getItemPricePerVol()*Float.valueOf(s.toString()));
                            }
                            groceryItemCollection.setItemVol(Float.valueOf(s.toString()));
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
            else {
                groceryItemVolLoose.setVisibility(View.GONE);
                if (groceryItemCollection.getItemVolList().get(0) == 0)
                    groceryItemVol.setVisibility(View.GONE);
                else groceryItemVol.setText("Volume: " + groceryItemCollection.getItemVolList().get(0)
                        + " " + groceryItemCollection.getItemVolUnitList().get(0));
            }
        } else {
            List<String> customSizeListCombined = new ArrayList<>();
            for (int i=0; i<groceryItemCollection.getCollectionSize(); i++) {
                if (groceryItemCollection.getItemCustomSizeList().get(i) != null)
                    customSizeListCombined.add(groceryItemCollection.getItemCustomSizeList().get(i));
            }
            if (!customSizeListCombined.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    getGroceryItemCustomSize.setLabelFor(R.id.grocery_item_custom_size_spinner);
                ArrayAdapter<String> customSizeListAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, customSizeListCombined);
                getGroceryItemCustomSizeList.setAdapter(customSizeListAdapter);
                getGroceryItemCustomSizeList.setOnItemSelectedListener(metaSelectListener);
            } else {
                getGroceryItemCustomSize.setVisibility(View.GONE);
                getGroceryItemCustomSizeList.setVisibility(View.GONE);
            }

            List<String> priceListCombined = new ArrayList<>();
            for (int i=0; i<groceryItemCollection.getCollectionSize(); i++) {
                if (groceryItemCollection.getItemPriceValueList().get(i) != null)
                    priceListCombined.add(groceryItemCollection.getCurrencyCode() + " "
                            + groceryItemCollection.getItemPriceValueList().get(i));
            }
            if (priceListCombined.isEmpty()) {
                groceryItemPrice.setVisibility(View.GONE);
                groceryItemPriceList.setVisibility(View.GONE);
            }
            else {
                groceryItemPrice.setText("Select Price: ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    groceryItemPrice.setLabelFor(R.id.grocery_item_price_spinner);
                ArrayAdapter<String> priceListAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, priceListCombined);
                groceryItemPriceList.setAdapter(priceListAdapter);
                groceryItemPriceList.setOnItemSelectedListener(metaSelectListener);
            }

            groceryItemWeightLoose.setVisibility(View.GONE);
            List<String> weightListCombined = new ArrayList<>();
            for (int i=0; i<groceryItemCollection.getCollectionSize(); i++) {
                if (groceryItemCollection.getItemWeightList().get(i) != 0)
                    weightListCombined.add(groceryItemCollection.getItemWeightList().get(i) + " "
                            + groceryItemCollection.getItemWeightUnitList().get(i));
            }
            if (weightListCombined.isEmpty()) {
                groceryItemWeight.setVisibility(View.GONE);
                groceryItemWeightList.setVisibility(View.GONE);
            }
            else {
                groceryItemWeight.setText("Select Weight: ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    groceryItemWeight.setLabelFor(R.id.grocery_item_weight_spinner);
                ArrayAdapter<String> weightListAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, weightListCombined);
                groceryItemWeightList.setAdapter(weightListAdapter);
                groceryItemWeightList.setOnItemSelectedListener(metaSelectListener);
            }

            groceryItemVolLoose.setVisibility(View.GONE);
            List<String> volumeListCombined = new ArrayList<>();
            for (int i=0; i<groceryItemCollection.getCollectionSize(); i++) {
                if (groceryItemCollection.getItemVolList().get(i) != 0)
                    volumeListCombined.add(groceryItemCollection.getItemVolList().get(i) + " "
                            + groceryItemCollection.getItemVolUnitList().get(i));
            }
            if (volumeListCombined.isEmpty()) {
                groceryItemVol.setVisibility(View.GONE);
                groceryItemVolList.setVisibility(View.GONE);
            }
            else {
                groceryItemVol.setText("Select Volume: ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    groceryItemVol.setLabelFor(R.id.grocery_item_price_spinner);
                ArrayAdapter<String> volumeListAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, volumeListCombined);
                groceryItemVolList.setAdapter(volumeListAdapter);
                groceryItemVolList.setOnItemSelectedListener(metaSelectListener);
            }
        }

        CoordinatorLayout mainCardView = (CoordinatorLayout) detailView.findViewById(R.id.content_grocery_item_detail);
        cartFab = (FloatingActionButton) detailView.findViewById(R.id.fab_add_cart);
        cartQuantityHandler = new CartQuantityHandler(groceryItemCollection, cartFab, mainCardView, getActivity());
        cartQuantityHandler.execute();

        detailBuilder.setView(detailView);
        return detailBuilder.create();
    }

    private AdapterView.OnItemSelectedListener metaSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            groceryItemCollection.setDefaults(position);
            groceryItemPriceList.setSelection(position);
            getGroceryItemCustomSizeList.setSelection(position);
            groceryItemWeightList.setSelection(position);
            groceryItemVolList.setSelection(position);
            if (groceryItemCollection.getItemImageList().get(position) != null)
                groceryItemImage.setImageUrl(groceryItemCollection.getItemImageList().get(position), DataHolder.getInstance().getImageLoader());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (groceryItemCollection.getItemImageList().get(0) != null)
                groceryItemImage.setImageUrl(groceryItemCollection.getItemImageList().get(0), DataHolder.getInstance().getImageLoader());
            groceryItemCollection.setDefaults(0);
        }
    };
}

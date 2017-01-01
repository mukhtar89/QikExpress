package com.equinox.qikexpress.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.equinox.qikexpress.Adapters.GroceryItemRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.HybridLayoutManager;

import java.util.List;

import static com.equinox.qikexpress.Models.DataHolder.mTwoPane;

/**
 * Created by mukht on 12/22/2016.
 */

public class GroceryItemOverviewFragment extends Fragment {

    private List<GroceryItemCollection> groceryItemCollectionList;
    private RecyclerView groceryItemRecycler;
    private GroceryItemRecyclerAdapter groceryItemRecyclerAdapter;
    private HybridLayoutManager layoutManager;
    private String category1, category2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category1 = getArguments().getString("CATEGORY1");
        category2 = getArguments().getString("CATEGORY2");
        groceryItemCollectionList = DataHolder.groceryItemCollectionCat2Mapping.get(category1).get(category2);
        DataHolder.category1 = category1;
        DataHolder.category2 = category2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grocery_item_overview, container, false);

        if (mTwoPane) {
            rootView.findViewById(R.id.category_title).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.grocery_item_category_chain)).setText(category1 + " -> " + category2);
        }
        else rootView.findViewById(R.id.category_title).setVisibility(View.GONE);

        layoutManager = new HybridLayoutManager(getActivity());
        groceryItemRecycler = (RecyclerView) rootView.findViewById(R.id.grocery_item_recycler);
        groceryItemRecycler.setLayoutManager(layoutManager.getLayoutManager(150));
        groceryItemRecycler.setHasFixedSize(true);
        groceryItemRecyclerAdapter = new GroceryItemRecyclerAdapter(getActivity(), groceryItemCollectionList);
        groceryItemRecycler.setAdapter(groceryItemRecyclerAdapter);

        return rootView;
    }


}

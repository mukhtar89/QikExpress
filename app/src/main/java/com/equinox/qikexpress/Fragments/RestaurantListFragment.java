package com.equinox.qikexpress.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.equinox.qikexpress.Activities.ShopListActivity;
import com.equinox.qikexpress.Adapters.BaseListRecyclerAdapterFilter;
import com.equinox.qikexpress.Adapters.GroceryListRecyclerAdapter;
import com.equinox.qikexpress.Adapters.RestaurantListRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.Restaurant;
import com.equinox.qikexpress.Models.ShopListCommunicator;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetGooglePlaces;
import com.equinox.qikexpress.Utils.HybridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Enums.QikList.GROCERY;
import static com.equinox.qikexpress.Enums.QikList.RESTAURANT;

/**
 * Created by mukht on 12/31/2016.
 */

public class RestaurantListFragment extends Fragment implements ShopListCommunicator {

    private ProgressDialog pDialog;
    private GetGooglePlaces<Restaurant> getGooglePlaces;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private RestaurantListRecyclerAdapter listRecyclerAdapter;
    private Integer pagination;


    public static RestaurantListFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantListFragment fragment = new RestaurantListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View groceryView = inflater.inflate(R.layout.fragment_restaurant_list, null);
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading Restaurants Nearby...");
        pDialog.setCancelable(false);
        pDialog.show();

        pagination = 1;
        getGooglePlaces = new GetGooglePlaces<>(RESTAURANT, new Handler[]{loopUntilLoad, updateDataListView, dismissDialog});
        getGooglePlaces.parsePlaces(DataHolder.location, pagination);

        HybridLayoutManager layoutManager = new HybridLayoutManager(getActivity());
        listRecyclerAdapter = new RestaurantListRecyclerAdapter(getActivity(), restaurantList, loadMoreAction);
        RecyclerView recyclerView = (RecyclerView) groceryView.findViewById(R.id.restaurant_grid_view);
        recyclerView.setLayoutManager(layoutManager.getLayoutManager(300));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listRecyclerAdapter);

        return groceryView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ShopListActivity)getActivity()).shopListCommunicator = this;
    }

    private Handler loadMoreAction = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            showpDialog();
            pagination++;
            if (pagination < 200) getGooglePlaces.parsePlaces(DataHolder.location, pagination);
            else {
                hidePDialog();
                Toast.makeText(getContext(), "No more Groceries can be Loaded!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private Handler loopUntilLoad = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pagination++;
            getGooglePlaces.parsePlaces(DataHolder.location, pagination);
            return false;
        }
    });

    private Handler updateDataListView = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dismissDialog.sendMessage(new Message());
            restaurantList.clear();
            restaurantList.addAll(getGooglePlaces.returnPlaceList());
            listRecyclerAdapter.notifyDataSetChanged();
            DataHolder.restaurantList = restaurantList;
            DataHolder.getInstance().setRestaurantMap();
            return false;
        }
    });

    private Handler dismissDialog = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hidePDialog();
            if (getGooglePlaces.returnPlaceList().isEmpty())
                Snackbar.make(getActivity().findViewById(R.id.shop_list_main_layout), "Could not load nearby groceries for now. Sorry!",
                        Snackbar.LENGTH_LONG)
                        .setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showpDialog();
                                getGooglePlaces.parsePlaces(DataHolder.location, pagination);
                            }
                        }).show();
            return false;
        }
    });

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    private void showpDialog() {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    @Override
    public List<? extends Place> getShopList() {
        return restaurantList;
    }

    @Override
    public BaseListRecyclerAdapterFilter getListAdapter() {
        return listRecyclerAdapter;
    }
}

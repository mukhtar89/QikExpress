package com.equinox.qikexpress.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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
import com.equinox.qikexpress.Adapters.RestaurantListRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.Restaurant;
import com.equinox.qikexpress.Models.ShopListCommunicator;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetGooglePlaces;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Enums.QikList.RESTAURANT;
import static com.equinox.qikexpress.Models.Constants.CURRENT_ADDRESS;
import static com.equinox.qikexpress.Models.Constants.SELECTED_ADDRESS;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.location;

/**
 * Created by mukht on 12/31/2016.
 */

public class RestaurantListFragment extends Fragment implements ShopListCommunicator {

    private static final String PAGE = "page", LIST = "list";
    private ProgressDialog pDialog;
    private GetGooglePlaces<Restaurant> getGooglePlaces;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private RestaurantListRecyclerAdapter listRecyclerAdapter;
    private Integer pagination;
    private Gson restaurantGson;
    private Location location;

    public static RestaurantListFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantListFragment fragment = new RestaurantListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagination = 1;
        getGooglePlaces = new GetGooglePlaces<>(RESTAURANT, new Handler[]{loopUntilLoad, updateDataListView, dismissDialog});
        restaurantGson = new Gson();
        if (savedInstanceState == null) pagination = 1;
        else {
            pagination = savedInstanceState.getInt(PAGE);
            for (String groceryGsonString : savedInstanceState.getStringArrayList(LIST))
                restaurantList.add(restaurantGson.fromJson(groceryGsonString, Restaurant.class));
            getGooglePlaces.setPlaceList(restaurantList);
        }
        if (getActivity().getIntent().getStringExtra(SELECTED_ADDRESS).equals(CURRENT_ADDRESS))
            location = DataHolder.location;
        else {
            location  = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(currentUser.getSelectedAddress().getLocation().latitude);
            location.setLongitude(currentUser.getSelectedAddress().getLocation().longitude);
        }
        getGooglePlaces.parsePlaces(location, pagination);
        getGooglePlaces.addFinishedListener();
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

        HybridLayoutManager layoutManager = new HybridLayoutManager(getActivity());
        listRecyclerAdapter = new RestaurantListRecyclerAdapter(getActivity(), restaurantList, loadMoreAction);
        RecyclerView recyclerView = (RecyclerView) groceryView.findViewById(R.id.restaurant_grid_view);
        recyclerView.setLayoutManager(layoutManager.getLayoutManager(300));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listRecyclerAdapter);

        return groceryView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE, pagination);
        ArrayList<String> tempGsonList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
            tempGsonList.add(restaurantGson.toJson(restaurant));
        outState.putStringArrayList(LIST, tempGsonList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        getGooglePlaces.removeFinishedListener();
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
            if (pagination < 200) getGooglePlaces.parsePlaces(location, pagination);
            else {
                hidePDialog();
                Toast.makeText(getContext(), "No more restaurants can be Loaded!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private Handler loopUntilLoad = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pagination++;
            getGooglePlaces.parsePlaces(location, pagination);
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
                Snackbar.make(getActivity().findViewById(R.id.shop_list_main_layout), "Could not load nearby restaurants for now. Sorry!",
                        Snackbar.LENGTH_LONG)
                        .setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showpDialog();
                                getGooglePlaces.parsePlaces(location, pagination);
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

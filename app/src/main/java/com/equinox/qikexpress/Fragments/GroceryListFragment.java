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
import com.equinox.qikexpress.Adapters.GroceryListRecyclerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.ShopListCommunicator;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetGooglePlaces;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Enums.QikList.GROCERY;
import static com.equinox.qikexpress.Models.Constants.CURRENT_ADDRESS;
import static com.equinox.qikexpress.Models.Constants.SELECTED_ADDRESS;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;

/**
 * Created by mukht on 12/31/2016.
 */

public class GroceryListFragment extends Fragment implements ShopListCommunicator {

    private static final String PAGE = "page", LIST = "list";
    private ProgressDialog pDialog;
    private GetGooglePlaces<Grocery> getGooglePlaces;
    private List<Grocery> groceryList = new ArrayList<>();
    private GroceryListRecyclerAdapter listRecyclerAdapter;
    private Integer pagination;
    private Gson groceryGson;

    public static GroceryListFragment newInstance() {
        Bundle args = new Bundle();
        GroceryListFragment fragment = new GroceryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGooglePlaces = new GetGooglePlaces<>(GROCERY, new Handler[]{loopUntilLoad, updateDataListView, dismissDialog});
        groceryGson = new Gson();
        if (savedInstanceState == null) pagination = 1;
        else {
            pagination = savedInstanceState.getInt(PAGE);
            for (String groceryGsonString : savedInstanceState.getStringArrayList(LIST))
                groceryList.add(groceryGson.fromJson(groceryGsonString, Grocery.class));
            getGooglePlaces.setPlaceList(groceryList);
        }
        if (getActivity().getIntent().getStringExtra(SELECTED_ADDRESS).equals(CURRENT_ADDRESS))
            getGooglePlaces.parsePlaces(DataHolder.location, pagination);
        else {
            Location location  = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(currentUser.getSelectedAddress().getLocation().latitude);
            location.setLongitude(currentUser.getSelectedAddress().getLocation().longitude);
            getGooglePlaces.parsePlaces(location, pagination);
        }
        getGooglePlaces.addFinishedListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View groceryView = inflater.inflate(R.layout.fragment_grocery_list, null);
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading Groceries Nearby...");
        pDialog.setCancelable(false);
        pDialog.show();

        HybridLayoutManager layoutManager = new HybridLayoutManager(getActivity());
        listRecyclerAdapter = new GroceryListRecyclerAdapter(getActivity(), groceryList, loadMoreAction);
        RecyclerView recyclerView = (RecyclerView) groceryView.findViewById(R.id.grocery_grid_view);
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
        for (Grocery grocery : groceryList)
            tempGsonList.add(groceryGson.toJson(grocery));
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
            groceryList.clear();
            groceryList.addAll(getGooglePlaces.returnPlaceList());
            listRecyclerAdapter.notifyDataSetChanged();
            DataHolder.groceryList = groceryList;
            DataHolder.getInstance().setGroceryMap();
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
        return groceryList;
    }

    @Override
    public BaseListRecyclerAdapterFilter getListAdapter() {
        return listRecyclerAdapter;
    }
}

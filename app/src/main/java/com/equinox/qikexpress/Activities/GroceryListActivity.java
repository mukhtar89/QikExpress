package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.equinox.qikexpress.Adapters.GroceryListRecyclerAdapter;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetGooglePlaces;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.equinox.qikexpress.Utils.ListSortFunc;
import com.equinox.qikexpress.Utils.LocationPermission;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private HybridLayoutManager layoutManager;
    private GetGooglePlaces<Grocery> getGooglePlaces;
    private RecyclerView recyclerView;
    private static Integer pagination = 1;
    private List<Grocery> groceryList = new ArrayList<>();
    private ProgressDialog pDialog;
    private GroceryListRecyclerAdapter listRecyclerAdapter;
    private LocationPermission locationPermission;
    private final Location[] location = new Location[1];
    private LocationManager locationManager;
    private Handler handlerLocation;
    private Handler.Callback locationCallback;
    private TextView sortBy, filterBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        getGooglePlaces = new GetGooglePlaces<>(QikList.GROCERY, pDialog, new Handler[]{loopUntilLoad, updateDataListView});

        locationPermission = new LocationPermission(this, this);
        final Location[] locationFetched = {getMyLocation()};
        if (locationFetched[0] != null) {
            getGooglePlaces.parsePlaces(locationFetched[0], pagination);
            location[0] = locationFetched[0];
        }
        locationCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    if (locationPermission.checkLocationPermission()) {
                        location[0] = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        locationFetched[0] = location[0];
                        getGooglePlaces.parsePlaces(locationFetched[0], pagination);
                    }
                }
                else location[0] = null;
                return false;
            }
        };

        layoutManager = new HybridLayoutManager(this);
        listRecyclerAdapter = new GroceryListRecyclerAdapter(this, groceryList, location[0], loadMoreAction);
        recyclerView = (RecyclerView) findViewById(R.id.grocery_grid_view);
        recyclerView.setLayoutManager(layoutManager.getLayoutManager(300));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listRecyclerAdapter);

        sortBy = (TextView) findViewById(R.id.sort_by);
        sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortHandler.sendMessage(new Message());
            }
        });
    }

    private Handler sortHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            List<Grocery> tempList = new ListSortFunc<>(Grocery.class, groceryList.size(), sortHandler).sortByDistance(groceryList.toArray());
            groceryList.clear();
            groceryList.addAll(tempList);
            listRecyclerAdapter.notifyDataSetChanged();
            return false;
        }
    });

    private Handler loadMoreAction = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!pDialog.isShowing())
                pDialog.show();
            pagination++;
            getGooglePlaces.parsePlaces(location[0], pagination);
            return false;
        }
    });

    private Handler loopUntilLoad = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pagination++;
            getGooglePlaces.parsePlaces(location[0], pagination);
            return false;
        }
    });

    private Handler updateDataListView = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            groceryList.addAll(getGooglePlaces.returnPlaceList());
            listRecyclerAdapter.notifyDataSetChanged();
            return false;
        }
    });


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Location getMyLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        handlerLocation = new Handler(locationCallback);
        if (locationPermission.checkLocationPermission())
            location[0] = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        else {
            hidePDialog();
            locationPermission.getLocationPermission(handlerLocation);
        }
        return location[0];
    }
}

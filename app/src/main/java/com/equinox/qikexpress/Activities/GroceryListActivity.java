package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Adapters.GroceryListRecyclerAdapter;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.equinox.qikexpress.Utils.LocationPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private static final String TAG = GroceryListActivity.class.getSimpleName();
    private static final String url = "https://1-dot-qikexpress.appspot.com/_ah/api/groceryoperations/v1/grocery/nearby?";

    private HybridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Button loadMore;
    private static Integer pagination = 0;
    private List<Grocery> groceryList = new ArrayList<>();
    private ProgressDialog pDialog;
    private GroceryListRecyclerAdapter listRecyclerAdapter;
    private LocationPermission locationPermission;
    private final Location[] location = new Location[1];
    private LocationManager locationManager;
    private Handler handlerLocation;
    private Handler.Callback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        locationPermission = new LocationPermission(this, this);
        final Location[] locationFetched = {getMyLocation()};
        if (locationFetched[0] != null) {
            JSONdecode(locationFetched[0]);
            location[0] = locationFetched[0];
            pagination = 0;
        }
        locationCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    if (locationPermission.checkLocationPermission()) {
                        location[0] = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        locationFetched[0] = location[0];
                        JSONdecode(locationFetched[0]);
                        pagination = 0;
                    }
                }
                else location[0] = null;
                return false;
            }
        };

        layoutManager = new HybridLayoutManager(this);
        listRecyclerAdapter = new GroceryListRecyclerAdapter(this, groceryList, location[0]);
        recyclerView = (RecyclerView) findViewById(R.id.grocery_grid_view);
        recyclerView.setLayoutManager(layoutManager.getLayoutManager(300));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listRecyclerAdapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        loadMore = (Button) findViewById(R.id.load_more_items);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                pagination++;
                JSONdecode(locationFetched[0]);
            }
        });
    }

    private Handler loopUntilLoad = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pagination++;
            JSONdecode(location[0]);
            return false;
        }
    });

    /*private void loopUntilLoad(Location location) {
        int currentItems = groceryList.size();
        while (groceryList.size() >= currentItems) {
            pagination++;
            JSONdecode(location);
        }
    }*/

    private void JSONdecode(final Location location) {
        String urlArguments = "lat="+location.getLatitude()+"&lng="+location.getLongitude()+"&mult="+pagination;
        JsonObjectRequest groceryReq = new JsonObjectRequest(url+urlArguments, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("items")) {
                        JSONArray groceryListObjects = response.getJSONArray("items");
                        hidePDialog();
                        // Parsing json
                        for (int i = 0; i < groceryListObjects.length(); i++) {
                            JSONObject obj = groceryListObjects.getJSONObject(i);
                            Grocery grocery = new Grocery();
                            grocery.setId(obj.getInt("id"));
                            grocery.setCityId(obj.getInt("cityId"));
                            if (obj.has("brandId"))
                                grocery.setBrandId(obj.getInt("brandId"));
                            grocery.setGroceryName(obj.getString("groceryName"));
                            if (obj.has("backImg"))
                                grocery.setBackImg(obj.getString("backImg"));
                            if (obj.has("profileImg"))
                                grocery.setProfileImg(obj.getString("profileImg"));
                            grocery.setLatitude((float) obj.getDouble("latitude"));
                            grocery.setLongitude((float) obj.getDouble("longitude"));

                            // adding movie to groceries array
                            groceryList.add(grocery);

                        }
                    }
                    else  {
                        pagination++;
                        Message message = new Message();
                        loopUntilLoad.sendMessage(message);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                listRecyclerAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });
        // Adding request to request queue
        AppVolleyController.getInstance().addToRequestQueue(groceryReq);
    }

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

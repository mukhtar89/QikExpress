package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Adapters.GroceryExpandableListAdapter;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.GroceryItemDBHandler;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryItemsMainActivity extends AppCompatActivity {

    private String TAG = GroceryItemsMainActivity.class.getSimpleName();
    private TextView openNow, vicinity, distance, time, cartCount;
    private NetworkImageView groceryImage, profileImage;
    private Grocery grocery;

    private GroceryExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private ProgressDialog pDialog;
    private Map<String,String> groceryCategoriesMapping = new HashMap<>();
    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<GroceryItem>> listDataChild = new HashMap<>();
    private GroceryItemDBHandler groceryItemDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_item_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groceryImage = (NetworkImageView) findViewById(R.id.imgPoster);
        profileImage = (NetworkImageView) findViewById(R.id.profile_img);
        openNow = (TextView) findViewById(R.id.open_now);
        vicinity = (TextView) findViewById(R.id.vicinity);
        distance = (TextView) findViewById(R.id.dist);
        time = (TextView) findViewById(R.id.time);

        grocery = DataHolder.getInstance().getGroceryMap().get(getIntent().getStringExtra("PLACE_ID"));
        getSupportActionBar().setTitle(StringManipulation.CapsFirst(grocery.getName()));
        Log.d(TAG, "Place ID: " + grocery.getPlaceId());

        if (grocery.getOpenNow() != null) {
            openNow.setText(grocery.getOpenNow() ? "OPEN" : "CLOSED");
            openNow.getBackground().setColorFilter(getResources()
                    .getColor(grocery.getOpenNow() ? R.color.green: R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
        distance.setText(grocery.getDistanceFromCurrent().toString()+" km");
        time.setText(grocery.getTimeFromCurrent().toString()+" min");
        vicinity.setText(grocery.getVicinity());
        getGroceryData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewInfoIntent = new Intent(GroceryItemsMainActivity.this, InfoReviewActivity.class);
                reviewInfoIntent.putExtra("PLACE_ID", grocery.getPlaceId());
                startActivity(reviewInfoIntent);
            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        DataHolder.getInstance().isPartner = true;
        expListView = (ExpandableListView) findViewById(R.id.grocery_items_expandable);
        listAdapter = new GroceryExpandableListAdapter(listDataHeader, listDataChild, groceryCategoriesMapping, this);
        expListView.setAdapter(listAdapter);
        groceryItemDBHandler = new GroceryItemDBHandler(pDialog, groceryDBItemsCallbackHandler);
        if (DataHolder.getInstance().getCategoryImageMapping().isEmpty())
            groceryItemDBHandler.getCategoryMapping();
        else groceryCategoriesMapping = DataHolder.getInstance().getCategoryImageMapping();
        groceryItemDBHandler.parseChildren(grocery.getPlaceId(), true);
    }

    public void getGroceryData() {
        String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/groceryoperations/v1/grocery/select?placeid=";
        JsonObjectRequest placeReq = new JsonObjectRequest(baseURL+grocery.getPlaceId(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("items")) {
                        JSONArray listObjects = response.getJSONArray("items");
                        JSONObject groceryObject = listObjects.getJSONObject(0);
                        if (groceryObject.has("brandId"))
                            grocery.setBrandId(groceryObject.getInt("brandId"));
                        if (groceryObject.has("profileImage"))
                            grocery.setProfileImageURL(groceryObject.getString("profileImage"));
                        if (groceryObject.has("photoReference")) {
                            grocery.getPhoto().setPhotoReference(groceryObject.getString("photoReference"));
                            grocery.getPhoto().setWidth(groceryObject.getInt("photoWidth"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                groceryDBHeaderHanlder.sendMessage(new Message());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                groceryDBHeaderHanlder.sendMessage(new Message());
            }
        });
        AppVolleyController.getInstance().addToRequestQueue(placeReq);
    }

    private Handler groceryDBHeaderHanlder = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String photoURL = grocery.getPhoto().returnApiUrl(Constants.PLACES_API_KEY);
            if (!photoURL.isEmpty())
                groceryImage.setImageUrl(photoURL, DataHolder.getInstance().getImageLoader());
            if (grocery.getProfileImageURL() != null)
                profileImage.setImageUrl(grocery.getProfileImageURL(), DataHolder.getInstance().getImageLoader());
            else {
                profileImage.setVisibility(View.GONE);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(80, 0, 0, 0);
                TableLayout tableLayout = (TableLayout) findViewById(R.id.header_detail);
                tableLayout.setLayoutParams(layoutParams);
            }
            return false;
        }
    });


    private Handler groceryDBItemsCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                groceryCategoriesMapping.clear();
                groceryCategoriesMapping.putAll(groceryItemDBHandler.returnCategories());
                DataHolder.getInstance().setCategoryImageMapping(groceryCategoriesMapping);
                if (!listDataChild.isEmpty())
                    listAdapter.notifyDataSetChanged();
            }
            else {
                listDataChild.clear();
                listDataChild.putAll(groceryItemDBHandler.returnDataChildren());
                if (listDataChild.isEmpty()) {
                    groceryItemDBHandler.parseChildren(grocery.getPlaceId(), false);
                    DataHolder.getInstance().isPartner = false;
                    pDialog.show();
                    return false;
                }
                DataHolder.getInstance().setGroceryItemMapping(listDataChild);
                listDataHeader.clear();
                listDataHeader.addAll(listDataChild.keySet());
                if (!groceryCategoriesMapping.isEmpty())
                    listAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grocery_item_main, menu);
        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        cartCount = (TextView) menuCart.findViewById(R.id.cart_count);
        DataHolder.getInstance().getUserDatabaseReference().child("grocery_cart").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = (int) dataSnapshot.getChildrenCount();
                if (count == 0) {
                    cartCount.setVisibility(View.INVISIBLE);
                } else {
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(count.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_cart) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

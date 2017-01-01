package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Adapters.GroceryExpandableListAdapter;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.CacheRequest;
import com.equinox.qikexpress.Utils.GroceryItemDBHandler;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.DataHolder.categoryImageMapping;
import static com.equinox.qikexpress.Models.DataHolder.groceryMap;
import static com.equinox.qikexpress.Models.DataHolder.mTwoPane;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

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
    private HashMap<String,List<String>> listDataChild = new HashMap<>();
    private GroceryItemDBHandler groceryItemDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_item_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.grocery_item_overview_container) != null) mTwoPane = true;
        else mTwoPane = false;

        groceryImage = (NetworkImageView) findViewById(R.id.imgPoster);
        profileImage = (NetworkImageView) findViewById(R.id.profile_img);
        openNow = (TextView) findViewById(R.id.open_now);
        vicinity = (TextView) findViewById(R.id.vicinity);
        distance = (TextView) findViewById(R.id.dist);
        time = (TextView) findViewById(R.id.time);

        grocery = groceryMap.get(getIntent().getStringExtra("PLACE_ID"));
        getSupportActionBar().setTitle(StringManipulation.CapsFirst(grocery.getName()));
        Log.d(TAG, "Place ID: " + grocery.getPlaceId());

        if (grocery.getOpenNow() != null) {
            openNow.setText(grocery.getOpenNow() ? "OPEN" : "CLOSED");
            openNow.getBackground().setColorFilter(getResources()
                    .getColor(grocery.getOpenNow() ? R.color.green: R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
        if (grocery.getDistanceFromCurrent() != null && grocery.getTimeFromCurrent() != null) {
            distance.setText(grocery.getDistanceFromCurrent().toString() + " km");
            time.setText(grocery.getTimeFromCurrent().toString() + " min");
        }
        vicinity.setText(grocery.getVicinity());
        if (grocery.getBrandImage() == null) getGroceryData();
        else groceryDBHeaderHandler.sendMessage(new Message());

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

        grocery.setPartner(true);
        placeMap.get(grocery.getPlaceId()).setPartner(true);
        groceryMap.get(grocery.getPlaceId()).setPartner(true);
        expListView = (ExpandableListView) findViewById(R.id.grocery_items_expandable);
        listAdapter = new GroceryExpandableListAdapter(listDataHeader, listDataChild, groceryCategoriesMapping, this, grocery.getPlaceId());
        expListView.setAdapter(listAdapter);
        groceryItemDBHandler = new GroceryItemDBHandler(pDialog, groceryDBItemsCallbackHandler);
        if (categoryImageMapping.isEmpty())
            groceryItemDBHandler.getCategoryMapping();
        else groceryCategoriesMapping = categoryImageMapping;
        groceryItemDBHandler.parseChildren(grocery.getPlaceId(), true);
    }

    public void getGroceryData() {
        String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/groceryoperations/v1/grocery/select?placeid=" + grocery.getPlaceId();
        CacheRequest placeReq = new CacheRequest(0, baseURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse responseNetwork) {
                try {
                    String jsonString = new String(responseNetwork.data, HttpHeaderParser.parseCharset(responseNetwork.headers));
                    JSONObject response = new JSONObject(jsonString);
                    Log.d(TAG, response.toString());
                    if (response.has("items")) {
                        JSONArray listObjects = response.getJSONArray("items");
                        JSONObject groceryObject = listObjects.getJSONObject(0);
                        if (groceryObject.has("brandName"))
                            grocery.setBrandName(groceryObject.getString("brandName"));
                        if (groceryObject.has("brandImage"))
                            grocery.setBrandImage(groceryObject.getString("brandImage"));
                        if (groceryObject.has("photoReference")) {
                            grocery.getPhoto().setPhotoReference(groceryObject.getString("photoReference"));
                        }
                        grocery.setCountryCode(groceryObject.getString("countryCode"));
                        groceryMap.put(grocery.getPlaceId(),
                                groceryMap.get(grocery.getPlaceId()).mergeGrocery(grocery));
                        placeMap.put(grocery.getPlaceId(),
                                placeMap.get(grocery.getPlaceId()).mergePlace(grocery));
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                groceryDBHeaderHandler.sendMessage(new Message());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                groceryDBHeaderHandler.sendMessage(new Message());
            }
        });
        AppVolleyController.getInstance().addToRequestQueue(placeReq);
    }

    private Handler groceryDBHeaderHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (grocery.getPhoto() != null) {
                String photoURL = grocery.getPhoto().returnApiUrl(Constants.PLACES_API_KEY);
                if (!photoURL.isEmpty())
                    groceryImage.setImageUrl(photoURL, DataHolder.getInstance().getImageLoader());
            }
            if (grocery.getBrandImage() != null)
                profileImage.setImageUrl(grocery.getBrandImage(), DataHolder.getInstance().getImageLoader());
            else {
                profileImage.setVisibility(View.GONE);
                findViewById(R.id.profile_img_layout).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.header_main_layout)).setWeightSum(6f);
            }
            return false;
        }
    });


    private Handler groceryDBItemsCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "In groceryDBItemsCallbackHandler");
            if (msg.arg1 == 0) {
                Log.d(TAG, "In groceryDBItemsCallbackHandler, categories loaded!");
                groceryCategoriesMapping.clear();
                groceryCategoriesMapping.putAll(groceryItemDBHandler.returnCategories());
                categoryImageMapping = groceryCategoriesMapping;
                if (!listDataChild.isEmpty())
                    listAdapter.notifyDataSetChanged();
            }
            else {
                listDataChild.clear();
                listDataChild.putAll(groceryItemDBHandler.returnDataChildren());
                if (listDataChild.isEmpty()) {
                    Log.d(TAG, "In groceryDBItemsCallbackHandler, no items loaded, reloading again!");
                    groceryItemDBHandler.parseChildren(grocery.getPlaceId(), false);
                    grocery.setPartner(false);
                    placeMap.get(grocery.getPlaceId()).setPartner(false);
                    groceryMap.get(grocery.getPlaceId()).setPartner(false);
                    pDialog.show();
                    return false;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataHolder.runGroceryItemCatMapping();
                    }
                }).start();
                listDataHeader.clear();
                listDataHeader.addAll(listDataChild.keySet());
                Log.d(TAG, "In groceryDBItemsCallbackHandler, items loaded!");
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
        DataHolder.userDatabaseReference.child(GROCERY_CART).getRef().addValueEventListener(new ValueEventListener() {
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
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groceryShoppingCartIntent = new Intent(GroceryItemsMainActivity.this, GroceryShoppingCartActivity.class);
                startActivity(groceryShoppingCartIntent);
            }
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
        if (id == R.id.action_search) {
            Intent searchGroceryItemsIntent = new Intent(GroceryItemsMainActivity.this, SearchGroceryItemActivity.class);
            startActivity(searchGroceryItemsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

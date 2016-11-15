package com.equinox.qikexpress.Models;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.LocationPermission;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by mukht on 11/2/2016.
 */
public class DataHolder {

    private static DataHolder ourInstance = new DataHolder();
    public static DataHolder getInstance() {
        return ourInstance;
    }

    private static String TAG = "DataHolder";
    private Map<String,Grocery> groceryMap = new HashMap<>();
    private ImageLoader imageLoader = AppVolleyController.getInstance().getImageLoader();
    public static List<Grocery> groceryList = new ArrayList<>();
    public static HashMap<String, List<GroceryItem>> groceryItemMapping;
    public static Map<String,String> categoryImageMapping = new HashMap<>();
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference userDatabaseReference = null;
    public static Boolean isPartner;
    public static Address currentAddress = null;
    public static String localCurrency = null, localCurrencySymbol = null;
    public static Location location = null;

    public void setGroceryMap() {
        groceryMap.clear();
        for (int i=0; i<groceryList.size(); i++)
            groceryMap.put(groceryList.get(i).getPlaceId(), groceryList.get(i));
    }
    public Map<String, Grocery> getGroceryMap() {
        return groceryMap;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null) imageLoader = AppVolleyController.getInstance().getImageLoader();
        return imageLoader;
    }

    public String getGroceryName(String groceryId) {
        for (Grocery grocery : DataHolder.groceryList) {
            if (grocery.getPlaceId().equals(groceryId))
                return grocery.getName();
        }
        return null;
    }

    public static void fetchLocationMetadata(final Handler fetchHandler, final Location location, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Geocoder coder = new Geocoder(context, Locale.ENGLISH);
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    currentAddress = results.get(0);
                    String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/countryoperations/v1/country/search?countryCode=";
                    JsonObjectRequest ratingsReq = new JsonObjectRequest(baseURL+currentAddress.getCountryCode(), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                if (response.has("items")) {
                                    JSONArray countryArray = response.getJSONArray("items");
                                    for (int i = 0; i < countryArray.length(); i++) {
                                        JSONObject countryItemObject = countryArray.getJSONObject(i);
                                        localCurrency =  countryItemObject.getString("currencyCode");
                                        localCurrencySymbol = countryItemObject.getString("currencySymbol");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (fetchHandler != null) {
                                Message message = new Message();
                                message.arg1 = 1;
                                fetchHandler.sendMessage(message);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    AppVolleyController.getInstance().addToRequestQueue(ratingsReq);
                } catch (IOException e) {     }
                return null;
            }
        }.execute();
    }

    public void setRole(final String role) {
        userDatabaseReference.child("roles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userRoles = (List<String>) dataSnapshot.getValue();
                if (userRoles == null)
                    userRoles = new ArrayList<>();
                if (!userRoles.contains(role))
                    userDatabaseReference.child("roles").child(String.valueOf(userRoles.size())).setValue(role);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {          }
        });
    }

    public void generateMetadata() {
        userDatabaseReference.child("user_metadata").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> userData = new HashMap<>();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    userData.put("name", currentUser.getDisplayName());
                    userData.put("email", currentUser.getEmail());
                    userData.put("photo", currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "");
                }
                if (location != null) {
                    userData.put("latitude", String.valueOf(location.getLatitude()));
                    userData.put("longitude", String.valueOf(location.getLongitude()));
                }
                //TODO add phone number and Address here too
                userDatabaseReference.child("user_metadata").setValue(userData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}

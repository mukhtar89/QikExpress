package com.equinox.qikexpress.Models;

import android.content.SharedPreferences;
import android.location.Location;

import com.android.volley.toolbox.ImageLoader;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.equinox.qikexpress.Models.Constants.ROLES;
import static com.equinox.qikexpress.Models.Constants.USER_METADATA;

/**
 * Created by mukht on 11/2/2016.
 */
public class DataHolder {

    private static DataHolder ourInstance = new DataHolder();
    public static DataHolder getInstance() {
        return ourInstance;
    }

    public static Map<String,Place> placeMap = new Hashtable<>();
    public static Map<String,Grocery> groceryMap = new Hashtable<>();
    public static List<Grocery> groceryList = new ArrayList<>();
    public static Map<String,Restaurant> restaurantMap = new Hashtable<>();
    public static List<Restaurant> restaurantList = new ArrayList<>();

    public static Hashtable<String,GroceryItemCollection> currentGroceryItemCollections = new Hashtable<>();
    public static Hashtable<String,List<GroceryItemCollection>> groceryItemCollectionCat1Mapping = new Hashtable<>();
    public static Hashtable<String,Hashtable<String,List<GroceryItemCollection>>> groceryItemCollectionCat2Mapping = new Hashtable<>();
    public static Map<String,String> categoryImageMapping = new HashMap<>();
    public static Hashtable<String,Order> orderList = new Hashtable<>();
    public static String category1, category2;
    public static Boolean mTwoPane = false;

    private ImageLoader imageLoader = AppVolleyController.getInstance().getImageLoader();
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference userDatabaseReference = null;
    public static DatabaseReference ordersReference = null;

    public static Location location = null;
    public static User currentUser = null;
    public static HashMap<String,UserPlace> userPlaceHashMap = new HashMap<>();
    public static final Boolean lock = true;

    public void setGroceryMap() {
        synchronized (lock) {
            for (int i = 0; i < groceryList.size(); i++) {
                if (!placeMap.containsKey(groceryList.get(i).getPlaceId()))
                    placeMap.put(groceryList.get(i).getPlaceId(), groceryList.get(i));
                groceryMap.put(groceryList.get(i).getPlaceId(), groceryList.get(i));
            }
        }
    }
    public void setRestaurantMap() {
        synchronized (lock) {
            for (int i = 0; i < restaurantList.size(); i++) {
                if (!placeMap.containsKey(restaurantList.get(i).getPlaceId()))
                    placeMap.put(restaurantList.get(i).getPlaceId(), restaurantList.get(i));
                restaurantMap.put(restaurantList.get(i).getPlaceId(), restaurantList.get(i));
            }
        }
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

    public void setRole(final String role) {
        userDatabaseReference.child(ROLES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userRoles = (List<String>) dataSnapshot.getValue();
                if (userRoles == null)
                    userRoles = new ArrayList<>();
                if (!userRoles.contains(role))
                    userDatabaseReference.child(ROLES).child(String.valueOf(userRoles.size())).setValue(role);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {          }
        });
    }

    public void generateMetadata() {
        userDatabaseReference.child(USER_METADATA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                currentUser.setPhotoURL(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                currentUser.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                userDatabaseReference.child(USER_METADATA).setValue(currentUser.toMap());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void runGroceryItemCatMapping() {
        groceryItemCollectionCat1Mapping.clear();
        groceryItemCollectionCat2Mapping.clear();
        for (GroceryItemCollection item : currentGroceryItemCollections.values()) {
            String catLevel1 = item.getCatLevel().get(0);
            if (!groceryItemCollectionCat1Mapping.containsKey(catLevel1))
                groceryItemCollectionCat1Mapping.put(catLevel1, new ArrayList<GroceryItemCollection>());
            groceryItemCollectionCat1Mapping.get(catLevel1).add(item);
            if (!groceryItemCollectionCat2Mapping.containsKey(catLevel1))
                groceryItemCollectionCat2Mapping.put(catLevel1, new Hashtable<String, List<GroceryItemCollection>>());
            if (item.getCatLevel().size() > 1) {
                String catLevel2 = item.getCatLevel().get(1);
                if (!groceryItemCollectionCat2Mapping.get(catLevel1).containsKey(catLevel2))
                    groceryItemCollectionCat2Mapping.get(catLevel1).put(catLevel2, new ArrayList<GroceryItemCollection>());
                groceryItemCollectionCat2Mapping.get(catLevel1).get(catLevel2).add(item);
            }
        }
    }
}

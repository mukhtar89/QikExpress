package com.equinox.qikexpress.Models;

import com.android.volley.toolbox.ImageLoader;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 11/2/2016.
 */
public class DataHolder {

    private static DataHolder ourInstance = new DataHolder();
    public static DataHolder getInstance() {
        return ourInstance;
    }

    private List<Grocery> groceryList = new ArrayList<>();
    private Map<String,Grocery> groceryMap = new HashMap<>();
    private HashMap<String, List<GroceryItem>> groceryItemMapping;
    private ImageLoader imageLoader = AppVolleyController.getInstance().getImageLoader();
    private Map<String,String> categoryImageMapping = new HashMap<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabaseReference = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public DatabaseReference getUserDatabaseReference() {return userDatabaseReference;}

    public void setGroceryMap() {
        groceryMap.clear();
        for (int i=0; i<groceryList.size(); i++)
            groceryMap.put(groceryList.get(i).getPlaceId(), groceryList.get(i));
    }

    public List<Grocery> getGroceryList() {
        return groceryList;
    }
    public void setGroceryList(List<Grocery> groceryList) {DataHolder.getInstance().groceryList = groceryList;}

    public Map<String, Grocery> getGroceryMap() {
        return groceryMap;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null) imageLoader = AppVolleyController.getInstance().getImageLoader();
        return imageLoader;
    }

    public void setCategoryImageMapping(Map<String,String> categoryImageMapping) {DataHolder.getInstance().categoryImageMapping = categoryImageMapping;}
    public Map<String,String> getCategoryImageMapping() {
        return categoryImageMapping;
    }

    public void setGroceryItemMapping(HashMap<String, List<GroceryItem>> groceryItemMapping) {this.groceryItemMapping = groceryItemMapping;}
    public HashMap<String, List<GroceryItem>> getGroceryItemMapping() {return groceryItemMapping;}
}

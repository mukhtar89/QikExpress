package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItem;
import com.equinox.qikexpress.Models.GroceryItemCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.DataHolder.currentGroceryItemCollections;
import static com.equinox.qikexpress.Models.DataHolder.groceryMap;

/**
 * Created by mukht on 11/4/2016.
 */

public class GroceryItemDBHandler {

    private String TAG = GroceryItemDBHandler.class.getSimpleName();
    private Map<String,String> groceryCategoriesMapping;
    private HashMap<String,List<String>> listDataChild;
    private Handler groceryItemHandler;
    private Dialog pDialog;

    public GroceryItemDBHandler(Dialog pDialog, Handler groceryItemHandler) {
        this.pDialog = pDialog;
        this.groceryItemHandler = groceryItemHandler;
        listDataChild = new HashMap<>();
    }

    public void getCategoryMapping() {
        String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/grocerycategoryoperations/v1/groceryCategory/all?detail=false";
        JsonObjectRequest categoriesReq = new JsonObjectRequest(baseURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                groceryCategoriesMapping = new HashMap<>();
                try {
                    if (response.has("items")) {
                        JSONArray categoryArray = response.getJSONArray("items");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject categoryMap = categoryArray.getJSONObject(i);
                            groceryCategoriesMapping.put(categoryMap.getString("categoryName"),
                                    categoryMap.has("categoryImageURL") ? categoryMap.getString("categoryImageURL") : null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.arg1 = 0;
                groceryItemHandler.sendMessage(message);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        AppVolleyController.getInstance().addToRequestQueue(categoriesReq);
    }

    public void parseChildren(final String placeId, final Boolean isPartner) {
        String baseURL;
        if (isPartner) baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/groceryitemsoperations/v1/groceryItems/select?placeid="+placeId;
        else {
            baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/groceryitemsoperations/v1/groceryItems/all";
            listDataChild.clear();
        }
        final JsonObjectRequest groceryItemsReq = new JsonObjectRequest(baseURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("items")) {
                        JSONArray categoryArray = response.getJSONArray("items");
                        hidePDialog();
                        // Parsing json
                        currentGroceryItemCollections.clear();
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject groceryItemObject = categoryArray.getJSONObject(i);
                            GroceryItem groceryItem = new GroceryItem();
                            groceryItem.setPlaceId(placeId);
                            if (isPartner) {
                                groceryItem.setItemPriceValue((float) groceryItemObject.getDouble("priceValue"));
                                groceryItem.setItemPricePerWeight((float) groceryItemObject.getDouble("pricePerWeight"));
                                groceryItem.setItemPricePerVol((float) groceryItemObject.getDouble("pricePerVol"));
                                groceryItem.setCurrencySymbol(groceryItemObject.getString("currencySymbol"));
                                groceryItem.setCountryCode(groceryItemObject.getString("countryCode"));
                                groceryItem.setCurrencyCode(groceryItemObject.getString("currencyCode"));
                            }
                            groceryItem.setItemId(groceryItemObject.getInt("itemId"));
                            groceryItem.setItemName(groceryItemObject.getString("name"));
                            if (groceryItemObject.has("image"))
                                groceryItem.setItemImage(groceryItemObject.getString("image"));
                            if (groceryItemObject.has("brandName"))
                                groceryItem.setItemBrandName(groceryItemObject.getString("brandName"));
                            if (groceryItemObject.has("brandImage"))
                                groceryItem.setItemBrandImage(groceryItemObject.getString("brandImage"));
                            List<String> categories = new ArrayList<>();
                            for (int j=1; j<=4; j++) {
                                if (groceryItemObject.has("catLev" + j))
                                    categories.add(groceryItemObject.getString("catLev" + j));
                            }
                            groceryItem.setCatLevel(categories);
                            if (groceryItemObject.has("customSize"))
                                groceryItem.setItemCustomSize(groceryItemObject.getString("customSize"));
                            groceryItem.setItemWeight((float) groceryItemObject.getDouble("weight"));
                            groceryItem.setItemVol((float) groceryItemObject.getDouble("volume"));
                            if (groceryItemObject.has("weightUnit"))
                                groceryItem.setItemWeightUnit(groceryItemObject.getString("weightUnit"));
                            if (groceryItemObject.has("volumeUnit"))
                                groceryItem.setItemVolumeUnit(groceryItemObject.getString("volumeUnit"));
                            groceryItem.setItemVolLoose(groceryItemObject.getBoolean("volLoose"));
                            groceryItem.setItemWeightLoose(groceryItemObject.getBoolean("weightLoose"));
                            groceryItem.setItemWeightScaleMultiplicand((float) groceryItemObject.getDouble("weightScaleMultiplicand"));
                            groceryItem.setItemVolumeScaleMultiplicand((float) groceryItemObject.getDouble("volumeScaleMultiplicand"));
                            groceryItem.setPlaceName(groceryMap.get(placeId).getName());

                            if (!currentGroceryItemCollections.containsKey(groceryItem.getItemName()))
                                currentGroceryItemCollections.put(groceryItem.getItemName(), new GroceryItemCollection().insert(groceryItem));
                            else currentGroceryItemCollections.get(groceryItem.getItemName()).insert(groceryItem);

                            if (!listDataChild.containsKey(categories.get(0)))
                                listDataChild.put(categories.get(0), new ArrayList<String>());
                            if (!listDataChild.get(categories.get(0)).contains(categories.get(1)))
                                listDataChild.get(categories.get(0)).add(categories.get(1));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.arg1 = 1;
                groceryItemHandler.sendMessage(message);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        AppVolleyController.getInstance().addToRequestQueue(groceryItemsReq);
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    public Map<String,String> returnCategories() {
        return groceryCategoriesMapping;
    }

    public Map<String, List<String>> returnDataChildren() {
        return listDataChild;
    }
}

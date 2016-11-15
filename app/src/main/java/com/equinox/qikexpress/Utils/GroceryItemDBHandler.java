package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Models.GroceryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 11/4/2016.
 */

public class GroceryItemDBHandler {

    private String TAG = GroceryItemDBHandler.class.getSimpleName();
    private Map<String,String> groceryCategoriesMapping;
    private HashMap<String, List<GroceryItem>> listDataChild;
    private Handler groceryItemHandler;
    private Dialog pDialog;

    public GroceryItemDBHandler(Dialog pDialog, Handler groceryItemHandler) {
        this.pDialog = pDialog;
        this.groceryItemHandler = groceryItemHandler;
        listDataChild = new HashMap<>();
    }

    public void getCategoryMapping() {
        String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/grocerycategoryoperations/v1/groceryCategory/all";
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
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject groceryItemObject = categoryArray.getJSONObject(i);
                            GroceryItem groceryItem = new GroceryItem();
                            groceryItem.setPlaceId(placeId);
                            if (isPartner) groceryItem.setItemPriceValue((float) groceryItemObject.getDouble("groceryItemPriceValue"));
                            groceryItem.setItemId(groceryItemObject.getInt("groceryItemId"));
                            groceryItem.setItemName(groceryItemObject.getString("groceryItemName"));
                            if (groceryItemObject.has("groceryItemImage"))
                                groceryItem.setItemImage(groceryItemObject.getString("groceryItemImage"));
                            List<String> categories = new ArrayList<>();
                            for (int j=1; j<=4; j++) {
                                if (groceryItemObject.has("catLev" + j))
                                    categories.add(groceryItemObject.getString("catLev" + j));
                            }
                            groceryItem.setCatLevel(categories);
                            if (!listDataChild.containsKey(categories.get(0)))
                                listDataChild.put(categories.get(0), new ArrayList<GroceryItem>());
                            listDataChild.get(categories.get(0)).add(groceryItem);
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

    public Map<String, List<GroceryItem>> returnDataChildren() {
        return listDataChild;
    }
}

package com.equinox.qikexpress.Utils;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GeoAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.equinox.qikexpress.Models.Constants.GEOCODE_API_KEY;

/**
 * Created by mukht on 11/18/2016.
 */

public class FetchGeoAddress {

    private String TAG = FetchGeoAddress.class.getSimpleName();
    private GeoAddress address;

    public void fetchLocationGeoData(final Location location, final Handler geoAddressHandler, final Object argument) {
        String baseURL = "https://maps.googleapis.com/maps/api/geocode/json?&latlng=";
        String urlArguments = location.getLatitude() + "," + location.getLongitude() + "&key=" + GEOCODE_API_KEY;
        Response.Listener<JSONObject> placeJSONListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("results")) {
                        JSONArray resultObj = response.getJSONArray("results");
                        JSONArray addressObj = resultObj.getJSONObject(0).getJSONArray("address_components");
                        address = new GeoAddress();
                        for (int i=addressObj.length()-1; i>=0; i--) {
                            JSONObject addressElement = addressObj.getJSONObject(i);
                            GeoAddress.GeoElement tempAddressElement = address.new GeoElement();
                            tempAddressElement.setName(addressElement.getString("short_name"));
                            JSONArray addressElementTypes = addressElement.getJSONArray("types");
                            for (int j=0; j<addressElementTypes.length(); j++)
                                if (!addressElementTypes.getString(j).equals("political"))
                                    tempAddressElement.getTypes().add(addressElementTypes.getString(j));
                            address.getAddressElements().add(tempAddressElement);
                        }
                        Message message = new Message();
                        if (argument != null) message.obj = argument;
                        geoAddressHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener placeJSONErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        };
        JsonObjectRequest placeDetailsReq = new JsonObjectRequest(baseURL+urlArguments, null, placeJSONListener, placeJSONErrorListener);
        AppVolleyController.getInstance().addToRequestQueue(placeDetailsReq);
    }

    public void fetchCurrencyMetadata() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String baseURL = "https://1-dot-qikexpress.appspot.com/_ah/api/countryoperations/v1/country/search?countryCode=";
                JsonObjectRequest ratingsReq = null;
                try {
                    ratingsReq = new JsonObjectRequest(baseURL + DataHolder.currentUser.getCurrentAddress()
                            .getAddressElements().get(0).getName(), null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, response.toString());
                                    try {
                                        if (response.has("items")) {
                                            JSONArray countryArray = response.getJSONArray("items");
                                            for (int i = 0; i < countryArray.length(); i++) {
                                                JSONObject countryItemObject = countryArray.getJSONObject(i);
                                                DataHolder.currentUser.setLocalCurrency(countryItemObject.getString("currencyCode"));
                                                DataHolder.currentUser.setLocalCurrencySymbol(countryItemObject.getString("currencySymbol"));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                                }
                            });
                } catch (NullPointerException e) {
                    fetchCurrencyMetadata();
                } if (ratingsReq != null)
                    AppVolleyController.getInstance().addToRequestQueue(ratingsReq);
                return null;
            }
        }.execute();
    }

    public GeoAddress getAddress() {
        return address;
    }
}

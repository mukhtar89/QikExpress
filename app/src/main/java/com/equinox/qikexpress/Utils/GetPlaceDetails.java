package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.Photo;
import com.equinox.qikexpress.Models.RatingsManager;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mukht on 11/2/2016.
 */

public class GetPlaceDetails {

    private String TAG = GetPlaceDetails.class.getSimpleName();
    private Dialog pDialog;
    private Handler placeHandler;
    private List<RatingsManager> ratingsList = new ArrayList<>();

    public GetPlaceDetails(Dialog pDialog, Handler placeHandler) {
        this.pDialog = pDialog;
        this.placeHandler = placeHandler;
    }

    public void parseDetail(final String placeId) {
        String baseURL = "https://maps.googleapis.com/maps/api/place/details/json?";
        String urlArguments = "placeid=" + placeId+ "&key=" + Constants.PLACES_API_KEY;
        JsonObjectRequest ratingsReq = new JsonObjectRequest(baseURL+urlArguments, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("result")) {
                        JSONObject resultObj = response.getJSONObject("result");
                        JSONArray ratingsArray = resultObj.getJSONArray("reviews");
                        hidePDialog();
                        // Parsing json
                        for (int i = 0; i < ratingsArray.length(); i++) {
                            JSONObject review = ratingsArray.getJSONObject(i);
                            RatingsManager rating = new RatingsManager(review.getString("author_name"),
                                    review.has("profile_photo_url") ? review.getString("profile_photo_url") : null,
                                    review.getInt("rating"), review.has("text") ? review.getString("text") : null, review.getInt("time"));
                            ratingsList.add(rating);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                placeHandler.sendMessage(new Message());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppVolleyController.getInstance().addToRequestQueue(ratingsReq);
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    public List<RatingsManager> returnRatingsList() {
        return ratingsList;
    }
}

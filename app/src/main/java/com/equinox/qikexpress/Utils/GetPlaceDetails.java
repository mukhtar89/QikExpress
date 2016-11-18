package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Periods;
import com.equinox.qikexpress.Models.Photo;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.RatingsManager;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 11/2/2016.
 */

public class GetPlaceDetails {

    private String TAG = GetPlaceDetails.class.getSimpleName();
    private Dialog pDialog;
    private Handler placeHandler;

    public GetPlaceDetails(Dialog pDialog, Handler placeHandler) {
        this.pDialog = pDialog;
        this.placeHandler = placeHandler;
    }

    public void parseDetail(final String... placeIds) {
        String baseURL = "https://maps.googleapis.com/maps/api/place/details/json?";
        for (String placeId :  placeIds) {
            String urlArguments = "placeid=" + placeId + "&key=" + Constants.PLACES_API_KEY;
            JsonObjectRequest placeDetailsReq = new JsonObjectRequest(baseURL+urlArguments, null, placeJSONListener, placeJSONErrorListener);
            AppVolleyController.getInstance().addToRequestQueue(placeDetailsReq);
        }
        AppVolleyController.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                hidePDialog();
                if (placeHandler != null) placeHandler.sendMessage(new Message());
                AppVolleyController.getInstance().getRequestQueue().removeRequestFinishedListener(this);
            }
        });
    }

    private Response.Listener<JSONObject> placeJSONListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, response.toString());
            try {
                Place place = new Place();
                if (response.has("result")) {
                    JSONObject resultObj = response.getJSONObject("result");
                    JSONObject geometry = resultObj.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    place.setLocation(new LatLng(location.getDouble("lat"), location.getDouble("lng")));
                    place.setIconURL(resultObj.getString("icon"));
                    place.setName(resultObj.getString("name"));
                    if (resultObj.has("opening_hours")) {
                        JSONObject openingHours = resultObj.getJSONObject("opening_hours");
                        place.setOpenNow(openingHours.getBoolean("open_now"));
                        Periods tempPeriods = new Periods();
                        Periods.CloseOpen[] tempCloseOpen;
                        JSONArray periodArray = openingHours.getJSONArray("periods");
                        for (int j = 0; j < periodArray.length(); j++) {
                            JSONObject periodObj = periodArray.getJSONObject(j);
                            JSONObject closeObj = periodObj.getJSONObject("close");
                            tempCloseOpen = tempPeriods.getNewCloseOpen();
                            tempCloseOpen[0].setDay(closeObj.getInt("day"));
                            tempCloseOpen[0].setTime(closeObj.getInt("time"));
                            JSONObject openObj = periodObj.getJSONObject("open");
                            tempCloseOpen = tempPeriods.getNewCloseOpen();
                            tempCloseOpen[1].setDay(openObj.getInt("day"));
                            tempCloseOpen[1].setTime(openObj.getInt("time"));
                            tempPeriods.getPeriods().add(tempCloseOpen);
                        }
                        place.setPeriods(tempPeriods);
                    }
                    if (resultObj.has("photos")) {
                        JSONArray photos = resultObj.getJSONArray("photos");
                        JSONObject photoObj = photos.getJSONObject(0);
                        Photo tempPhoto = new Photo(photoObj.getInt("width"), photoObj.getInt("height"),
                                null, photoObj.getString("photo_reference"));
                        place.setPhoto(tempPhoto);
                    }
                    place.setPlaceId(resultObj.getString("place_id"));
                    if (resultObj.has("rating")) place.setTotalRating(resultObj.getDouble("rating"));
                    JSONArray ratingsArray = resultObj.getJSONArray("reviews");
                    List<RatingsManager> tempRatingsList = new ArrayList<>();
                    for (int j = 0; j < ratingsArray.length(); j++) {
                        JSONObject review = ratingsArray.getJSONObject(j);
                        RatingsManager rating = new RatingsManager(review.getString("author_name"),
                                review.has("profile_photo_url") ? review.getString("profile_photo_url") : null,
                                review.getInt("rating"), review.has("text") ? review.getString("text") : null, review.getInt("time"));
                        tempRatingsList.add(rating);
                    }
                    place.setIndividualRatings(tempRatingsList);
                    place.setgMapURL(resultObj.getString("url"));
                    place.setVicinity(resultObj.getString("vicinity"));
                    if (resultObj.has("website")) place.setWebURL(resultObj.getString("website"));
                    place.setAddress(resultObj.getString("formatted_address"));
                    place.setPhoneNumber(resultObj.getString("international_phone_number"));
                    if (DataHolder.getInstance().getPlaceMap().containsKey(place.getPlaceId()))
                        DataHolder.getInstance().getPlaceMap().put(place.getPlaceId(),
                                DataHolder.getInstance().getPlaceMap().get(place.getPlaceId()).mergePlace(place));
                    else DataHolder.getInstance().getPlaceMap().put(place.getPlaceId(), place);
                    //TODO add place to the type of Place: example, grocery, restaurant, etc
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener placeJSONErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hidePDialog();
        }
    };

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    public List<RatingsManager> returnRatingsList(String placeId) {
        return DataHolder.getInstance().getPlaceMap().get(placeId).getIndividualRatings();
    }
}

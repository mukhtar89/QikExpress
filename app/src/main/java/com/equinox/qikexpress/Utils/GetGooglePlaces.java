package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Photo;
import com.equinox.qikexpress.Models.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mukht on 10/30/2016.
 */

public class GetGooglePlaces<T extends Place> {

    private String TAG = GetGooglePlaces.class.getSimpleName();
    private String NORMAL = "1", SECONDARY = "2";
    private QikList placeType;
    private Dialog pDialog;
    private Handler[] placeHandlers;
    private List<T> placeList = new ArrayList<>();
    private HashSet<String> loadedPlaces;

    public GetGooglePlaces(QikList placeType, Dialog pDialog, Handler[] placeHandlers) {
        this.placeType = placeType;
        this.pDialog = pDialog;
        this.placeHandlers = placeHandlers;
        loadedPlaces = new HashSet<>();
    }

    public synchronized void parsePlaces(final Location location, final Integer pagination) {
        placeList = new ArrayList<>();
        String baseURL = "https://maps.googleapis.com/maps/api/place/search/json?";
        String urlArguments = "location="+location.getLatitude()+","+location.getLongitude()+"&radius="+pagination*1000
                + "&type=" + placeType.getTypeName() + "&sensor=true_or_false&key=" + Constants.PLACES_API_KEY;
        JsonObjectRequest placeReq = new JsonObjectRequest(baseURL+urlArguments, null, placesListener, placesErrorListener);
        AppVolleyController.getInstance().addToRequestQueue(placeReq, NORMAL);
        for (String keyword : Arrays.asList(placeType.getKeyword())) {
            baseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            urlArguments = "location="+location.getLatitude()+","+location.getLongitude()+"&radius="+pagination*1000
                    + "&keyword=" + keyword+ "&sensor=true_or_false&key=" + Constants.PLACES_API_KEY;
            JsonObjectRequest placeReqSecondary = new JsonObjectRequest(baseURL+urlArguments, null, placesListener, placesErrorListener);
            AppVolleyController.getInstance().addToRequestQueue(placeReqSecondary, SECONDARY);
        }
        AppVolleyController.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (placeList.isEmpty())  {
                            AppVolleyController.getInstance().cancelPendingRequests(NORMAL);
                            AppVolleyController.getInstance().cancelPendingRequests(SECONDARY);
                            Message message = new Message();
                            message.arg1 = pagination+1;
                            placeHandlers[0].sendMessage(message);
                            return;
                        }
                        hidePDialog();
                        placeHandlers[1].sendMessage(new Message());
                    }
                },3000);
                AppVolleyController.getInstance().getRequestQueue().removeRequestFinishedListener(this);
            }
        });
    }

    private Response.Listener<JSONObject> placesListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, response.toString());
            try {
                if (response.has("results")) {
                    JSONArray listObjects = response.getJSONArray("results");
                    // Parsing json
                    for (int i = 0; i < listObjects.length(); i++) {
                        JSONObject obj = listObjects.getJSONObject(i);
                        //Place place = (Place) Class.forName(placeType.getListName()).newInstance();
                        Class<T> type = (Class<T>) Class.forName("com.equinox.qikexpress.Models."+placeType.getListName());
                        T place = type.newInstance();
                        if (!loadedPlaces.contains(obj.getString("place_id"))){
                            JSONObject location = obj.getJSONObject("geometry").getJSONObject("location");
                            place.setLocation(new LatLng(location.getDouble("lat"), location.getDouble("lng")));
                            place.setIconURL(obj.getString("icon"));
                            place.setName(obj.getString("name"));
                            if (obj.has("opening_hours")) {
                                JSONObject openingHours = obj.getJSONObject("opening_hours");
                                place.setOpenNow(openingHours.getBoolean("open_now"));
                            }
                            if (obj.has("photos")) {
                                JSONArray photos = obj.getJSONArray("photos");
                                JSONObject photo = photos.getJSONObject(0);
                                Photo tempPhoto = new Photo(photo.getInt("width"), photo.getInt("height"), null, photo.getString("photo_reference"));
                                place.setPhoto(tempPhoto);
                            }
                            place.setPlaceId(obj.getString("place_id"));
                            place.setVicinity(obj.getString("vicinity"));
                            if (DataHolder.getInstance().getPlaceMap().containsKey(place.getPlaceId()))
                                DataHolder.getInstance().getPlaceMap().put(place.getPlaceId(),
                                        DataHolder.getInstance().getPlaceMap().get(place.getPlaceId()).mergePlace(place));
                            else DataHolder.getInstance().getPlaceMap().put(place.getPlaceId(), place);
                            synchronized (DataHolder.lock) {
                                placeList.add(place);
                                loadedPlaces.add(obj.getString("place_id"));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener placesErrorListener = new Response.ErrorListener() {
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

    public List<T> returnPlaceList() {
        return placeList;
    }
}

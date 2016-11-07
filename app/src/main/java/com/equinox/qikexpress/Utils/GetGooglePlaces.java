package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Photo;
import com.equinox.qikexpress.Models.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mukht on 10/30/2016.
 */

public class GetGooglePlaces<T extends Place> {

    private String TAG = GetGooglePlaces.class.getSimpleName();
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

    public void parsePlaces(final Location location, final Integer pagination) {
        placeList = new ArrayList<>();
        String baseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String urlArguments = "location="+location.getLatitude()+","+location.getLongitude()+"&radius="+pagination*2000
                 + "&type=" + placeType.getTypeName() + "&sensor=true_or_false&key=" + Constants.PLACES_API_KEY;
        JsonObjectRequest placeReq = new JsonObjectRequest(baseURL+urlArguments, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    if (response.has("results")) {
                        JSONArray listObjects = response.getJSONArray("results");
                        hidePDialog();
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
                                JSONArray photos = obj.getJSONArray("photos");
                                JSONObject photo = photos.getJSONObject(0);
                                Photo tempPhoto = new Photo(photo.getInt("width"), photo.getInt("height"), null, photo.getString("photo_reference"));
                                place.setPhoto(tempPhoto);
                                place.setPlaceId(obj.getString("place_id"));
                                place.setVicinity(obj.getString("vicinity"));
                                placeList.add(place);
                                loadedPlaces.add(obj.getString("place_id"));
                            }
                        }
                    }
                    else  {
                        Message message = new Message();
                        message.arg1 = pagination+1;
                        placeHandlers[0].sendMessage(message);
                        return;
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
                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                placeHandlers[1].sendMessage(new Message());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppVolleyController.getInstance().addToRequestQueue(placeReq);
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    public List<T> returnPlaceList() {
        return placeList;
    }
}

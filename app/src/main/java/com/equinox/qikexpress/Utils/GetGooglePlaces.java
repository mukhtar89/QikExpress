package com.equinox.qikexpress.Utils;

import android.app.Dialog;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.Constants;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Photo;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.ViewHolders.GroceryListRecyclerViewHolder;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.equinox.qikexpress.Models.DataHolder.location;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

/**
 * Created by mukht on 10/30/2016.
 */

public class GetGooglePlaces<T extends Place> {

    private String TAG = GetGooglePlaces.class.getSimpleName();
    private String NORMAL = "1", SECONDARY = "2";
    private QikList placeType;
    private Handler[] placeHandlers;
    private List<T> placeList;
    private HashSet<String> loadedPlaces;
    private Integer pagination;

    public GetGooglePlaces(QikList placeType, Handler[] placeHandlers) {
        placeList = new ArrayList<>();
        this.placeType = placeType;
        this.placeHandlers = placeHandlers;
        loadedPlaces = new HashSet<>();
    }

    public synchronized void parsePlaces(final Location location, final Integer pagination) {
        this.pagination = pagination;
        String baseURL = "https://maps.googleapis.com/maps/api/place/search/json?";
        String urlArguments = "location="+location.getLatitude()+","+location.getLongitude()+"&radius="+pagination*250
                + "&type=" + placeType.getTypeName() + "&sensor=true_or_false&key=" + Constants.PLACES_API_KEY;
        CacheRequest placeReqCacheRequest = new CacheRequest(0, baseURL+urlArguments, placesListener, placesErrorListener);
        AppVolleyController.getInstance().addToRequestQueue(placeReqCacheRequest, NORMAL);
        for (String keyword : Arrays.asList(placeType.getKeyword())) {
            baseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            urlArguments = "location="+location.getLatitude()+","+location.getLongitude()+"&radius="+pagination*250
                    + "&keyword=" + keyword+ "&sensor=true_or_false&key=" + Constants.PLACES_API_KEY;
            CacheRequest placeReqSecCacheRequest = new CacheRequest(0, baseURL+urlArguments, placesListener, placesErrorListener);
            AppVolleyController.getInstance().addToRequestQueue(placeReqSecCacheRequest, SECONDARY);
        }
    }

    private Response.Listener<NetworkResponse> placesListener = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse responseNetwork) {
            try {
                final String jsonString = new String(responseNetwork.data, HttpHeaderParser.parseCharset(responseNetwork.headers));
                JSONObject response = new JSONObject(jsonString);
                Log.d(TAG, response.toString());
                if (response.has("results")) {
                    JSONArray listObjects = response.getJSONArray("results");
                    // Parsing json
                    for (int i = 0; i < listObjects.length(); i++) {
                        JSONObject obj = listObjects.getJSONObject(i);
                        //Place place = (Place) Class.forName(placeType.getListName()).newInstance();
                        Class<T> type = (Class<T>) Class.forName("com.equinox.qikexpress.Models."+placeType.getListName());
                        final T place = type.newInstance();
                        if (!loadedPlaces.contains(obj.getString("place_id"))){
                            JSONObject locationObj = obj.getJSONObject("geometry").getJSONObject("location");
                            place.setLocation(new LatLng(locationObj.getDouble("lat"), locationObj.getDouble("lng")));
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
                            if (placeMap.containsKey(place.getPlaceId()))
                                placeMap.put(place.getPlaceId(),
                                        placeMap.get(place.getPlaceId()).mergePlace(place));
                            else placeMap.put(place.getPlaceId(), place);
                            synchronized (DataHolder.lock) {
                                placeList.add(place);
                                loadedPlaces.add(obj.getString("place_id"));
                            }
                            Handler handleDistance = new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message msg) {
                                    String[] params = (String[]) msg.obj;
                                    place.setDistanceFromCurrent(params[0]);
                                    place.setTimeFromCurrent(params[1]);
                                    placeMap.put(place.getPlaceId(),
                                            placeMap.get(place.getPlaceId()).mergePlace(place));
                                    placeHandlers[1].sendMessage(new Message());
                                    return false;
                                }
                            });
                            DistanceRequest distanceRequest = new DistanceRequest(handleDistance);
                            distanceRequest.execute(new LatLng(location.getLatitude(), location.getLongitude()),
                                    new LatLng(place.getLocation().latitude, place.getLocation().longitude));
                        }
                    }
                }
            } catch (JSONException | UnsupportedEncodingException e) {
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
            placeHandlers[2].sendMessage(new Message());
        }
    };

    private RequestQueue.RequestFinishedListener<Object> finishedListener = new RequestQueue.RequestFinishedListener<Object>() {
        @Override
        public void onRequestFinished(final Request<Object> request) {
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
                }
            },1000);
        }
    };

    public void addFinishedListener() {
        AppVolleyController.getInstance().getRequestQueue().addRequestFinishedListener(finishedListener);
    }

    public void removeFinishedListener() {
        AppVolleyController.getInstance().getRequestQueue().removeRequestFinishedListener(finishedListener);
    }

    public List<T> returnPlaceList() {
        return placeList;
    }

    public void setPlaceList(List<T> placeList) {
        this.placeList.addAll(placeList);
        for (Place place : placeList)
            loadedPlaces.add(place.getPlaceId());
    }

}

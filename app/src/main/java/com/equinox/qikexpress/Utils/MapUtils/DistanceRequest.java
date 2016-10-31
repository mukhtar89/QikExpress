package com.equinox.qikexpress.Utils.MapUtils;

import android.os.Handler;
import android.os.Message;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mukht on 10/30/2016.
 */

public class DistanceRequest  {

    private String baseURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private String origins, destinations;
    private String mode, language;
    private String key = "AIzaSyBrHwdxsz9UFHuOrl5PBkQ91ZjTLT1d7_s";
    private Handler distanceHandler;
    final String[] params = new String[2];

    public DistanceRequest(Handler distanceHandler) {
        this.distanceHandler = distanceHandler;
    }

    public void execute(LatLng... coordinates) {
        origins = coordinates[0].latitude+ "," + coordinates[0].longitude;
        destinations = coordinates[1].latitude + "," + coordinates[1].longitude;
        mode = "driving";
        language = "en-US";
        String url = baseURL + "&origins=" + origins
                + "&destinations=" + destinations
                + "&mode=" + mode
                + "&language=" + language
                + "&key=" + key;
        JsonObjectRequest distanceReq = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")) {
                        if (response.getString("status").equals("OK")) {
                            JSONArray rows = response.getJSONArray("rows");
                            JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");
                            JSONObject element = elements.getJSONObject(0);
                            if (element.getString("status").equals("OK")) {
                                JSONObject distance = element.getJSONObject("distance");
                                params[0] = distance.getString("text");
                                JSONObject duration = element.getJSONObject("duration");
                                params[1] = duration.getString("text");
                                Message message = new Message();
                                message.obj = params;
                                distanceHandler.sendMessage(message);
                            }
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("DistanceRequest", "Error: " + error.getMessage());
            }
        });
        AppVolleyController.getInstance().addToRequestQueue(distanceReq);
    }
}

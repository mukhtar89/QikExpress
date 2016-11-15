package com.equinox.qikexpress.Utils;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.equinox.qikexpress.Models.DataHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by mukht on 11/13/2016.
 */

public class FusedLocationService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Activity activity;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationPermission locationPermission;
    private Handler locationChangedListener;

    public FusedLocationService(Activity activity, LocationPermission locationPermission, Handler locationChangedListener) {
        this.activity = activity;
        this.locationPermission = locationPermission;
        this.locationChangedListener = locationChangedListener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
        if (locationPermission.checkLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                locationChangedListener.sendMessage(new Message());
                DataHolder.fetchLocationMetadata(null, mLocation, activity);
            }
        }
        else if (!locationPermission.getDialogStatus()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationPermission.getLocationPermission();
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO Implement Out-of-Date or missing Google Play Services
    }

    @Override
    public void onLocationChanged(Location location) {
        locationChangedListener.sendMessage(new Message());
    }

    public synchronized GoogleApiClient buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        return mGoogleApiClient;
    }

    public Location returnLocation() {
        return mLocation;
    }
}

package com.equinox.qikexpress.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.FusedLocationService;
import com.equinox.qikexpress.Utils.LocationPermission;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.ORDER_ID;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;

public class DeliveryTrackingActivity extends BaseOrderActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationPermission locationPermission;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationService fusedLocationService;
    private BottomSheetBehavior trackingBottomSheet;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentOrder = DataHolder.orderList.get(getIntent().getStringExtra(ORDER_ID));

        locationPermission = new LocationPermission(this, this);
        fusedLocationService = new FusedLocationService(this, locationPermission, locationChangedListener);
        mGoogleApiClient = fusedLocationService.buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        List<Marker> markers = new ArrayList<>();
        MarkerOptions markerUserOptions = new MarkerOptions();
        markerUserOptions.position(currentUser.getSelectedAddress().getLocation())
                .title(currentUser.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_black_24dp));
        Marker markerUser = mMap.addMarker(markerUserOptions);
        markerUser.showInfoWindow();
        markers.add(markerUser);
        MarkerOptions markerBusinessOptions = new MarkerOptions();
        markerBusinessOptions.position(currentOrder.getShop().getLocation())
                .title(currentOrder.getShop().getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store_black_24dp));
        Marker markerBusiness = mMap.addMarker(markerBusinessOptions);
        markerBusiness.showInfoWindow();
        markers.add(markerBusiness);
        if (currentOrder.getDriver() != null) {
            MarkerOptions markerDriverOptions = new MarkerOptions();
            markerDriverOptions.position(currentOrder.getDriver().getCurrentLocation())
                    .title(currentOrder.getDriver().getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp));
            Marker markerDriver = mMap.addMarker(markerDriverOptions);
            markerDriver.showInfoWindow();
            markers.add(markerDriver);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentOrder.getFrom().getPermLocation(), 14));
    }

    private Handler locationChangedListener = new Handler(new Handler.Callback() {
        LatLng oldLocation = null;
        @Override
        public boolean handleMessage(Message msg) {
            /*if (fusedLocationService.returnLocation() != null) {
                if (oldLocation == null) {
                    oldLocation = new LatLng(fusedLocationService.returnLocation().getLatitude(), fusedLocationService.returnLocation().getLongitude());
                    getOrders(oldLocation);
                } else if (SphericalUtil.computeDistanceBetween(oldLocation, new LatLng(fusedLocationService.returnLocation().getLatitude(), fusedLocationService.returnLocation().getLongitude()))
                        > showRange.getSelectedItemPosition()*1000) {
                    getOrders(oldLocation);
                    oldLocation = new LatLng(fusedLocationService.returnLocation().getLatitude(), fusedLocationService.returnLocation().getLongitude())
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fusedLocationService.returnLocation().getLatitude(), fusedLocationService.returnLocation().getLongitude()), 13));
            }*/
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMap() {
        if (locationPermission.checkLocationPermission())
            mMap.setMyLocationEnabled(true);
        else locationPermission.getLocationPermission();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            supportMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


}

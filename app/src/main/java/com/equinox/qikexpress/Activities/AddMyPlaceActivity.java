package com.equinox.qikexpress.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.MainRecyclerViewAdapter;
import com.equinox.qikexpress.Adapters.UserPlaceParaAdapter;
import com.equinox.qikexpress.Models.GeoAddress;
import com.equinox.qikexpress.Models.UserPlace;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.LocationPermission;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.MY_PLACES;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.userDatabaseReference;

public class AddMyPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = AddMyPlaceActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 2;
    private static final String ADDRESS = "address";
    private static final String LOCATION = "location";
    private static final String USER_PLACE = "userPlace";
    private GoogleMap mMap;
    private TextView addressView;
    private Gson placeGson;
    private Context context;
    private UserPlace addUserPlace;
    private Spinner userPlaceParaSpinner;
    private TextInputEditText userPlaceText;
    private List<String[]> userPlaceParaList;
    private UserPlaceParaAdapter userPlaceParaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
        addUserPlace = new UserPlace();

        LinearLayout searchPlaceButton = (LinearLayout) findViewById(R.id.search_place_button);
        searchPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setCountry(currentUser.getCurrentAddress().getCountry())
                            .build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(autocompleteFilter)
                            .build((Activity) context);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                } catch (GooglePlayServicesRepairableException e) {
                    GoogleApiAvailability.getInstance().getErrorDialog(getParent(), e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
                    Log.e(TAG, message);
                    Toast.makeText(getParent(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayout placePickButton = (LinearLayout) findViewById(R.id.place_picker_button);
        placePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build((Activity) context), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    GoogleApiAvailability.getInstance().getErrorDialog(getParent(), e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
                    Log.e(TAG, message);
                    Toast.makeText(getParent(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        placeGson = new Gson();
        if (savedInstanceState != null)
            addUserPlace = placeGson.fromJson(savedInstanceState.getString(USER_PLACE), UserPlace.class);
        userPlaceParaList = new ArrayList<>();
        Iterator<Map.Entry<String,Object>> paraListIterator = addUserPlace.toMapEdit().entrySet().iterator();
        while (paraListIterator.hasNext()) {
            Map.Entry<String,Object> currentEntry = paraListIterator.next();
            if (currentEntry.getValue() != null)
                userPlaceParaList.add(new String[]{currentEntry.getKey(), currentEntry.getValue().toString()});
        }
        userPlaceParaAdapter = new UserPlaceParaAdapter(userPlaceParaList, addUserPlace);
        userPlaceParaSpinner = (Spinner) findViewById(R.id.user_place_properties);
        final ArrayAdapter<String> userPlaceSpinnerAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(addUserPlace.getList()));
        userPlaceParaSpinner.setAdapter(userPlaceSpinnerAdapter);
        userPlaceText = (TextInputEditText) findViewById(R.id.user_place_text);
        final FloatingActionButton addUserPlacePara = (FloatingActionButton) findViewById(R.id.add_user_place_para);
        addUserPlacePara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                for (String[] para: userPlaceParaList) {
                    if (para[0].equals(userPlaceParaSpinner.getSelectedItem().toString())) {
                        userPlaceParaList.remove(para);
                        userPlaceParaAdapter.notifyItemRemoved(count);
                        break;
                    }
                    count++;
                }
                userPlaceParaList.add(0,new String[]{userPlaceParaSpinner.getSelectedItem().toString(), userPlaceText.getText().toString()});
                addUserPlace.putValue(userPlaceParaSpinner.getSelectedItem().toString(), userPlaceText.getText().toString());
                userPlaceParaAdapter.notifyItemInserted(0);
                userPlaceText.setText("");
                userPlaceParaSpinner.setSelection(0);
            }
        });
        RecyclerView userPlacePropertiesList = (RecyclerView) findViewById(R.id.user_place_properties_added_list);
        userPlacePropertiesList.setLayoutManager(new LinearLayoutManager(this));
        userPlacePropertiesList.setHasFixedSize(true);
        userPlacePropertiesList.setAdapter(userPlaceParaAdapter);

        addressView = (TextView) findViewById(R.id.address_view);
        if (getIntent().hasExtra(ADDRESS)) {
            GeoAddress address = placeGson.fromJson(getIntent().getStringExtra(ADDRESS), GeoAddress.class);
            addressView.setText(Html.fromHtml("Address : <b>" + address.getFullAddress() + "</b>"));
            addUserPlace.setAddress(address);
        }

        final RadioGroup placeTypeGroup = (RadioGroup) findViewById(R.id.place_type_radio_group);
        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.fab_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButton = placeTypeGroup.getCheckedRadioButtonId();
                switch (checkedRadioButton) {
                    case R.id.place_home_type:
                        userDatabaseReference.child(MY_PLACES).child("Home").setValue(addUserPlace.toMap());
                        finish();
                        break;
                    case R.id.place_work_type:
                        userDatabaseReference.child(MY_PLACES).child("Work").setValue(addUserPlace.toMap());
                        finish();
                        break;
                    case R.id.place_other_type:
                        TextInputEditText otherPlaceName = (TextInputEditText) findViewById(R.id.place_other_type_text);
                        if (!otherPlaceName.getText().toString().isEmpty()) {
                            userDatabaseReference.child(MY_PLACES).child(otherPlaceName.getText().toString())
                                    .setValue(addUserPlace.toMap());   finish();
                        }
                        else otherPlaceName.setError("Please enter a name for place");
                        break;
                    default:
                        Snackbar.make(placeTypeGroup, "Please select a PlaceType!", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_PLACE, placeGson.toJson(addUserPlace));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        mMap.clear();
        LatLng myLocation = placeGson.fromJson(getIntent().getStringExtra(LOCATION), LatLng.class);
        addUserPlace.setLocation(myLocation);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
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

    private void setUpMap() {
        LocationPermission locationPermission = new LocationPermission(this, this);
        if (locationPermission.checkLocationPermission())
            mMap.setMyLocationEnabled(true);
        else locationPermission.getLocationPermission();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                addUserPlace.setLocation(place.getLatLng());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));
                addressView.setText(Html.fromHtml("Address: <b>" + place.getAddress() + "</b>"));
                addUserPlace.setFullAddressText(place.getAddress().toString());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                addUserPlace.setLocation(place.getLatLng());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));
                addressView.setText(Html.fromHtml("Address: <b>" + place.getAddress() + "</b>"));
                addUserPlace.setFullAddressText(place.getAddress().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }
}

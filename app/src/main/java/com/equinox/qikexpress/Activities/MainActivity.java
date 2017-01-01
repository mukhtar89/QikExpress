package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Adapters.MainRecyclerViewAdapter;
import com.equinox.qikexpress.Fragments.AddPlaceFragment;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GeoAddress;
import com.equinox.qikexpress.Models.User;
import com.equinox.qikexpress.Models.UserPlace;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.FetchGeoAddress;
import com.equinox.qikexpress.Utils.FusedLocationService;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.equinox.qikexpress.Utils.LocationPermission;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.CONSUMER;
import static com.equinox.qikexpress.Models.Constants.MY_PLACES;
import static com.equinox.qikexpress.Models.Constants.ORDERS;
import static com.equinox.qikexpress.Models.Constants.USER;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.database;
import static com.equinox.qikexpress.Models.DataHolder.location;
import static com.equinox.qikexpress.Models.DataHolder.userDatabaseReference;
import static com.equinox.qikexpress.Models.DataHolder.userPlaceHashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button loginButton;
    private TextView loginName, loginEmail;
    private NetworkImageView loginImage;
    private FrameLayout profileFrame;
    private RecyclerView recyclerView;
    private HybridLayoutManager layoutManager;
    private LocationPermission locationPermission;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationService fusedLocationService;
    private FetchGeoAddress fetchGeoAddress;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.drawable.logo);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d("AUTH", "OnAuthState: signed out");
                    loginButton.setVisibility(View.VISIBLE);
                    profileFrame.setVisibility(View.GONE);
                    loginEmail.setVisibility(View.GONE);
                    loginName.setVisibility(View.GONE);
                } else {
                    Log.d("AUTH", "OnAuthState: Signed in " + user.getUid());
                    prepFirebaseData();
                    loginButton.setVisibility(View.GONE);
                    loginEmail.setVisibility(View.VISIBLE);
                    loginName.setVisibility(View.VISIBLE);
                    loginName.setText(user.getDisplayName());
                    loginEmail.setText(user.getEmail());
                    profileFrame.setVisibility(View.GONE);
                    if (user.getPhotoUrl() != null){
                        profileFrame.setVisibility(View.VISIBLE);
                        loginImage.setImageUrl(user.getPhotoUrl().toString(), DataHolder.getInstance().getImageLoader());
                    }
                }
            }
        };

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeaderView = navigationView.getHeaderView(0);
        profileFrame =(FrameLayout) navigationHeaderView.findViewById(R.id.profile_bundle);
        loginButton = (Button) navigationHeaderView.findViewById(R.id.login_button);
        loginName = (TextView) navigationHeaderView.findViewById(R.id.login_name);
        loginEmail = (TextView) navigationHeaderView.findViewById(R.id.login_email);
        loginImage = (NetworkImageView) navigationHeaderView.findViewById(R.id.login_image);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        loginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        layoutManager = new HybridLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.main_grid_view);
        recyclerView.setLayoutManager(layoutManager.getLayoutManager(150));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MainRecyclerViewAdapter(this));

        locationPermission = new LocationPermission(this, this);
        fusedLocationService = new FusedLocationService(this, locationPermission, locationHandler);
        mGoogleApiClient = fusedLocationService.buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.nav_wallet:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_wallet) {
            startActivity(new Intent(MainActivity.this, WalletActivity.class));
        } else if (id == R.id.nav_my_places) {
            startActivity(new Intent(MainActivity.this, MyPlacesActivity.class));
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_offer) {

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Handler locationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (location == null) {
                location = fusedLocationService.returnLocation();
                fetchGeoAddress = new FetchGeoAddress();
                fetchGeoAddress.fetchLocationGeoData(DataHolder.location, addressFetchHandler, null);
            }
            return true;
        }
    });

    private Handler addressFetchHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null && location != null && fetchGeoAddress != null) {
                GeoAddress address = fetchGeoAddress.getAddress();
                currentUser.setCurrentAddress(address);
                currentUser.setCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                userDatabaseReference.child(MY_PLACES).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean flag = false;
                        if (dataSnapshot.hasChildren()) {
                            Iterator<Map.Entry<String,Object>> iterator
                                    = ((Map<String,Object>) dataSnapshot.getValue()).entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String,Object> entry = iterator.next();
                                userPlaceHashMap.put(entry.getKey(),
                                        new UserPlace().fromMap((Map<String,Object>) entry.getValue()));
                                if (userPlaceHashMap.get(entry.getKey()).getAddress().getFullAddress()
                                        .equals(currentUser.getCurrentAddress().getFullAddress())) flag = true;
                            }
                        }
                        if (!flag) {
                            AddPlaceFragment addPlaceFragment =
                                    AddPlaceFragment.newInstance(currentUser.getCurrentAddress(), currentUser.getCurrentLocation());
                            addPlaceFragment.show(getSupportFragmentManager(), "AddPlaceFragment");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                fetchGeoAddress.fetchCurrencyMetadata(null);
                DataHolder.getInstance().generateMetadata();
            }
            return false;
        }
    });

    private void prepFirebaseData() {
        userDatabaseReference =
                database.getReference(USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (currentUser == null) currentUser = new User();
        DataHolder.getInstance().setRole(CONSUMER);
        DataHolder.ordersReference = userDatabaseReference.child(ORDERS).getRef();
        addressFetchHandler.sendMessage(new Message());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(MainActivity.this, "Location is Set!", Toast.LENGTH_LONG).show();
                } else {
                    locationPermission = new LocationPermission(this, this);
                    locationPermission.getLocationPermission();
                    Toast.makeText(MainActivity.this, "Location Access is Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
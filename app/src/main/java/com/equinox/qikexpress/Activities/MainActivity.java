package com.equinox.qikexpress.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.FusedLocationService;
import com.equinox.qikexpress.Utils.GetOrders;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.equinox.qikexpress.Utils.LocationPermission;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.equinox.qikexpress.Models.Constants.ORDERS;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    DataHolder.userDatabaseReference =
                            DataHolder.database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DataHolder.getInstance().setRole("consumer");
                    DataHolder.getInstance().generateMetadata();
                    DataHolder.ordersReference = DataHolder.userDatabaseReference.child(ORDERS).getRef();
                    loginButton.setVisibility(View.GONE);
                    loginEmail.setVisibility(View.VISIBLE);
                    loginName.setVisibility(View.VISIBLE);
                    loginName.setText(user.getDisplayName());
                    loginEmail.setText(user.getEmail());
                    profileFrame.setVisibility(View.VISIBLE);
                    if (user.getPhotoUrl() != null){
                        profileFrame.setVisibility(View.VISIBLE);
                        loginImage.setImageUrl(user.getPhotoUrl().toString(), DataHolder.getInstance().getImageLoader());
                    }
                }
            }
        };


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            Intent walletIntent = new Intent(MainActivity.this, WalletActivity.class);
            startActivity(walletIntent);
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
            if (DataHolder.location == null)
                DataHolder.location = fusedLocationService.returnLocation();
            DataHolder.getInstance().generateMetadata();
            return true;
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
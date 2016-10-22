package com.equinox.qikexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button loginButton;
    private TextView loginName, loginEmail;
    private ImageView loginImage;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "OnAuthState: Signed in " + user.getUid());
                }
                else {
                    Log.d("AUTH", "OnAuthState: signed out");
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
        loginButton = (Button) navigationHeaderView.findViewById(R.id.login_button);
        loginName = (TextView) navigationHeaderView.findViewById(R.id.login_name);
        loginEmail = (TextView) navigationHeaderView.findViewById(R.id.login_email);
        loginImage = (ImageView) navigationHeaderView.findViewById(R.id.login_image);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            loginButton.setVisibility(View.VISIBLE);
            loginImage.setVisibility(View.GONE);
            loginEmail.setVisibility(View.GONE);
            loginName.setVisibility(View.GONE);
        }
        else loginButton.setVisibility(View.GONE);
        loginName.setText(user.getDisplayName());
        loginEmail.setText(user.getEmail());
        loginImage.setImageURI(user.getPhotoUrl());

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        final float dpWidth  = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth/200);
        final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, columns);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager.canScrollVertically();

        recyclerView = (RecyclerView) findViewById(R.id.main_grid_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecyclerView.Adapter<ListViewHolder>() {

            @Override
            public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_list, parent, false);
                return new ListViewHolder(holder);
            }

            @Override
            public void onBindViewHolder(ListViewHolder holder, int position) {
                holder.cardText.setText(QikList.values()[position].getListName());
                holder.cardIcon.setImageDrawable(getResources().getDrawable(QikList.values()[position].getIcon()));
                holder.listCard.setBackground(getResources().getDrawable(QikList.values()[position].getBackground()));
            }

            @Override
            public int getItemCount() {
                return 5;
            }
        });

    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        CardView listCard;
        TextView cardText;
        ImageView cardIcon;

        public ListViewHolder(View itemView) {
            super(itemView);
            listCard = (CardView) itemView.findViewById(R.id.listCard);
            cardText = (TextView) itemView.findViewById(R.id.card_text);
            cardIcon = (ImageView) itemView.findViewById(R.id.card_icon);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.equinox.qikexpress.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Fragments.GroceryItemOverviewFragment;
import com.equinox.qikexpress.Fragments.GroceryListFragment;
import com.equinox.qikexpress.Fragments.RestaurantListFragment;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.ShopListCommunicator;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;

public class ShopListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TYPE = "TYPE";
    public ShopListCommunicator shopListCommunicator;
    private LinearLayout sortBy, filterBy, trackOrders;
    private TextView cartCount, myAddress, placeTypeName;
    private ImageView placeIcon, backIcon;
    private Context context;
    private List<? extends Place> shopList = new ArrayList<>();
    private QikList qikList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_shop_layout);
        context = this;

        if (currentUser == null) {
            Toast.makeText(this, "Please login to App", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(ShopListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else if (DataHolder.location == null) {
            Toast.makeText(this, "Please turn on your Location!", Toast.LENGTH_LONG).show();
            finish();
        } else if (currentUser.getCurrentAddress() == null) {
            Toast.makeText(this, "Please wait till your current address is decoded!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            View actionBarView = getSupportActionBar().getCustomView();
            myAddress = (TextView) actionBarView.findViewById(R.id.location_address);
            if (currentUser.getCurrentAddress() == null) myAddress.setText("");
            else myAddress.setText(currentUser.getCurrentAddress().getFullAddress());
            backIcon = (ImageView) findViewById(R.id.back_to_main_icon);
            backIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            qikList = QikList.valueOf(getIntent().getStringExtra(TYPE));
            placeTypeName = (TextView) actionBarView.findViewById(R.id.place_type_name);
            placeTypeName.setText(qikList.getPlural());
            placeIcon = (ImageView) actionBarView.findViewById(R.id.place_type_icon);
            placeIcon.setImageResource(qikList.getIcon());

            switch (qikList) {
                case GROCERY:
                    GroceryListFragment groceryFragment = GroceryListFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_list_container, groceryFragment)
                            .commit();
                    break;
                case RESTAURANT:
                    RestaurantListFragment restaurantFragment = RestaurantListFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_list_container, restaurantFragment)
                            .commit();
                    break;
                default:
                    Toast.makeText(context, "Module not implemented yet!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }


            sortBy = (LinearLayout) findViewById(R.id.sort_by);
            sortBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(ShopListActivity.this, sortBy);
                    popup.getMenuInflater().inflate(R.menu.sort_by_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            shopList = shopListCommunicator.getShopList();
                            switch (item.getItemId()) {
                                case R.id.dist:
                                    Collections.sort(shopList, new Comparator<Place>() {
                                        @Override
                                        public int compare(Place lhs, Place rhs) {
                                            return lhs.getDistanceFromCurrent().compareTo(rhs.getDistanceFromCurrent());
                                        }
                                    });
                                    break;
                                case R.id.time:
                                    Collections.sort(shopList, new Comparator<Place>() {
                                        @Override
                                        public int compare(Place lhs, Place rhs) {
                                            return lhs.getTimeFromCurrent().compareTo(rhs.getTimeFromCurrent());
                                        }
                                    });
                                    break;
                            }
                            shopListCommunicator.getListAdapter().notifyDataSetChanged();
                            return true;
                        }
                    });
                    popup.show();
                }
            });
            trackOrders = (LinearLayout) findViewById(R.id.track_order);
            trackOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ShopListActivity.this, TrackingActivity.class));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        cartCount = (TextView) menuCart.findViewById(R.id.cart_count);
        if (DataHolder.userDatabaseReference == null) {
            Toast.makeText(this, "Please login to App", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(ShopListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else DataHolder.userDatabaseReference.child(GROCERY_CART).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = (int) dataSnapshot.getChildrenCount();
                if (count == 0) {
                    cartCount.setVisibility(View.INVISIBLE);
                } else {
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(count.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groceryShoppingCartIntent = new Intent(ShopListActivity.this, GroceryShoppingCartActivity.class);
                startActivity(groceryShoppingCartIntent);
            }
        });
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //SearchView searchView = (SearchView) findViewById(R.id.search_by);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            shopListCommunicator.getListAdapter().getFilter().filter(query);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        shopListCommunicator.getListAdapter().getFilter().filter(newText);
        return false;
    }
}

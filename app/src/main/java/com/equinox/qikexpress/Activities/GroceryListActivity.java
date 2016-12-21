package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.GroceryListRecyclerAdapter;
import com.equinox.qikexpress.Enums.QikList;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetGooglePlaces;
import com.equinox.qikexpress.Utils.HybridLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.equinox.qikexpress.Enums.QikList.GROCERY;
import static com.equinox.qikexpress.Models.Constants.GROCERY_CART;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

public class GroceryListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private HybridLayoutManager layoutManager;
    private GetGooglePlaces<Grocery> getGooglePlaces;
    private RecyclerView recyclerView;
    private Integer pagination;
    private List<Grocery> groceryList = new ArrayList<>();
    private ProgressDialog pDialog;
    private GroceryListRecyclerAdapter listRecyclerAdapter;
    private LinearLayout sortBy, filterBy, trackOrders;
    private TextView cartCount, myAddress, placeTypeName;
    private ImageView placeIcon;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_grocery_layout);
        context = this;

        if (currentUser == null) {
            Toast.makeText(this, "Please login to App", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(GroceryListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else if (DataHolder.location == null) {
            Toast.makeText(this, "Please turn on your Location!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            View actionBarView = getSupportActionBar().getCustomView();
            myAddress = (TextView) actionBarView.findViewById(R.id.location_address);
            if (currentUser.getCurrentAddress() == null) myAddress.setText("");
            else myAddress.setText("near " + currentUser.getCurrentAddress().getFullAddress());
            placeTypeName = (TextView) actionBarView.findViewById(R.id.place_type_name);
            placeTypeName.setText("Groceries");
            placeIcon = (ImageView) actionBarView.findViewById(R.id.place_type_icon);
            placeIcon.setImageResource(GROCERY.getIcon());

            pDialog = new ProgressDialog(this);
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading Groceries Nearby...");
            pDialog.setCancelable(false);
            pDialog.show();

            pagination = 1;
            getGooglePlaces = new GetGooglePlaces<>(GROCERY, new Handler[]{loopUntilLoad, updateDataListView, dismissDialog});
            getGooglePlaces.parsePlaces(DataHolder.location, pagination);

            layoutManager = new HybridLayoutManager(this);
            listRecyclerAdapter = new GroceryListRecyclerAdapter(this, groceryList, loadMoreAction);
            recyclerView = (RecyclerView) findViewById(R.id.grocery_grid_view);
            recyclerView.setLayoutManager(layoutManager.getLayoutManager(300));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(listRecyclerAdapter);

            sortBy = (LinearLayout) findViewById(R.id.sort_by);
            sortBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(GroceryListActivity.this, sortBy);
                    popup.getMenuInflater().inflate(R.menu.sort_by_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.dist:
                                    Collections.sort(groceryList, new Comparator<Grocery>() {
                                        @Override
                                        public int compare(Grocery lhs, Grocery rhs) {
                                            return lhs.getDistanceFromCurrent().compareTo(rhs.getDistanceFromCurrent());
                                        }
                                    });
                                    break;
                                case R.id.time:
                                    Collections.sort(groceryList, new Comparator<Grocery>() {
                                        @Override
                                        public int compare(Grocery lhs, Grocery rhs) {
                                            return lhs.getTimeFromCurrent().compareTo(rhs.getTimeFromCurrent());
                                        }
                                    });
                                    break;
                            }
                            listRecyclerAdapter.notifyDataSetChanged();
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
                    startActivity(new Intent(GroceryListActivity.this, TrackingActivity.class));
                }
            });
        }
    }

    private Handler loadMoreAction = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            showpDialog();
            pagination++;
            if (pagination < 200) getGooglePlaces.parsePlaces(DataHolder.location, pagination);
            else {
                hidePDialog();
                Toast.makeText(context, "No more Groceries can be Loaded!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private Handler loopUntilLoad = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pagination++;
            getGooglePlaces.parsePlaces(DataHolder.location, pagination);
            return false;
        }
    });

    private Handler updateDataListView = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dismissDialog.sendMessage(new Message());
            groceryList.clear();
            groceryList.addAll(getGooglePlaces.returnPlaceList());
            listRecyclerAdapter.notifyDataSetChanged();
            DataHolder.groceryList = groceryList;
            DataHolder.getInstance().setGroceryMap();
            return false;
        }
    });

    private Handler dismissDialog = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hidePDialog();
            if (getGooglePlaces.returnPlaceList().isEmpty())
                Snackbar.make(findViewById(R.id.grocery_main_layout), "Could not load nearby groceries for now. Sorry!",
                        Snackbar.LENGTH_LONG)
                        .setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showpDialog();
                                getGooglePlaces.parsePlaces(DataHolder.location, pagination);
                            }
                        }).show();
            return false;
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    private void showpDialog() {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        cartCount = (TextView) menuCart.findViewById(R.id.cart_count);
        if (DataHolder.userDatabaseReference == null) {
            Toast.makeText(this, "Please login to App", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(GroceryListActivity.this, LoginActivity.class);
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
                Intent groceryShoppingCartIntent = new Intent(GroceryListActivity.this, GroceryShoppingCartActivity.class);
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
            listRecyclerAdapter.getFilter().filter(query);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listRecyclerAdapter.getFilter().filter(newText);
        return false;
    }
}

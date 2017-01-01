package com.equinox.qikexpress.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RelativeLayout;

import com.equinox.qikexpress.Adapters.SearchGroceryItemAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.GroceryItemCollection;
import com.equinox.qikexpress.R;

import java.util.ArrayList;
import java.util.List;

public class SearchGroceryItemActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView searchGroceryItemList;
    private SearchGroceryItemAdapter searchGroceryItemAdapter;
    private List<GroceryItemCollection> groceryItemCollectionList;
    private String category1, category2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_grocery_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        category1 = getIntent().getStringExtra("CATEGORY1");
        category2 = getIntent().getStringExtra("CATEGORY2");
        if (category1 == null && category2 == null)
            groceryItemCollectionList = new ArrayList<>(DataHolder.currentGroceryItemCollections.values());
        else if (category1 != null && category2 == null)
            groceryItemCollectionList = DataHolder.groceryItemCollectionCat1Mapping.get(category1);
        else groceryItemCollectionList = DataHolder.groceryItemCollectionCat2Mapping.get(category1).get(category2);

        searchGroceryItemList = (RecyclerView) findViewById(R.id.grocery_item_search_result);
        searchGroceryItemAdapter = new SearchGroceryItemAdapter(this, groceryItemCollectionList);
        searchGroceryItemList.setLayoutManager(new LinearLayoutManager(this));
        searchGroceryItemList.setHasFixedSize(true);
        searchGroceryItemList.setAdapter(searchGroceryItemAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchGroceryItemAdapter.getFilter().filter(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grocery_item_search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchGroceryItemAdapter.getFilter().filter(newText);
        return false;
    }
}

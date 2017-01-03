package com.equinox.qikexpress.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.UserPlace;
import com.equinox.qikexpress.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.MY_PLACES;
import static com.equinox.qikexpress.Models.DataHolder.defaultUserPlace;
import static com.equinox.qikexpress.Models.DataHolder.userDatabaseReference;
import static com.equinox.qikexpress.Models.DataHolder.userPlaceHashMap;

public class MyPlacesActivity extends AppCompatActivity {

    private static final String USER_PLACE = "userPlace";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context= this;

        final Intent addMyPlaceIntent = new Intent(MyPlacesActivity.this, AddMyPlaceActivity.class);
        final LinearLayout myPlaceList = (LinearLayout) findViewById(R.id.my_places_list_holder);
        List<UserPlace> userPlaceList = new ArrayList<>(userPlaceHashMap.values());
        UserPlace[] tempUserPlaceList = new UserPlace[2];
        for (int i=0; i<userPlaceList.size(); i++) {
            switch (userPlaceList.get(i).getUserPlaceName()) {
                case "Home":
                    tempUserPlaceList[0] = userPlaceList.get(i);
                    userPlaceList.remove(i);
                    break;
                case "Work" :
                    tempUserPlaceList[1] = userPlaceList.get(i);
                    userPlaceList.remove(i);
                    break;
            }
        }
        if (tempUserPlaceList[0] != null) userPlaceList.add(0, tempUserPlaceList[0]);
        if (tempUserPlaceList[1] != null) userPlaceList.add(1, tempUserPlaceList[1]);
        int count = 0;
        for (final UserPlace userPlace : userPlaceList){
            final View myPlaceView = View.inflate(this, R.layout.my_place_card_item, null);
            TextView myPlaceName = (TextView) myPlaceView.findViewById(R.id.my_place_name);
            TextView myPlaceAddress = (TextView) myPlaceView.findViewById(R.id.my_place_address);
            TextView myPlaceDetail = (TextView) myPlaceView.findViewById(R.id.my_place_details);
            ImageView myPlaceIcon = (ImageView) myPlaceView.findViewById(R.id.my_place_type_icon);
            final ImageView myPlaceFav = (ImageView) myPlaceView.findViewById(R.id.my_place_fav_icon);
            NetworkImageView myPlaceMapShot = (NetworkImageView) myPlaceView.findViewById(R.id.my_place_map_shot);
            final ImageView myPlaceMenu = (ImageView) myPlaceView.findViewById(R.id.my_place_type_menu);
            RelativeLayout myPlaceHeader = (RelativeLayout) myPlaceView.findViewById(R.id.my_place_header_color);
            myPlaceName.setText(userPlace.getUserPlaceName());
            switch (userPlace.getUserPlaceName()) {
                case "Home":
                    myPlaceIcon.setImageResource(R.drawable.ic_home_white_48dp);
                    myPlaceHeader.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "Work":
                    myPlaceIcon.setImageResource(R.drawable.ic_business_white_48dp);
                    myPlaceHeader.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                default:
                    myPlaceIcon.setImageResource(R.drawable.ic_place_white_48dp);
                    myPlaceHeader.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                    break;
            }
            myPlaceAddress.setText(userPlace.getAddress() != null
                    ? userPlace.getAddress().getFullAddress() : userPlace.getFullAddress());
            myPlaceMapShot.setImageUrl(userPlace.getSnapshotURL(), DataHolder.getInstance().getImageLoader());
            myPlaceDetail.setText(userPlace.getDetailString());
            if (defaultUserPlace != null && defaultUserPlace.equals(userPlace.getUserPlaceName()))
                myPlaceFav.setVisibility(View.VISIBLE);
            myPlaceMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu cardMenu = new PopupMenu(context, myPlaceMenu);
                    cardMenu.inflate(R.menu.my_place_card_menu);
                    cardMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_edit:
                                    addMyPlaceIntent.putExtra(USER_PLACE, userPlace.getUserPlaceName());
                                    addMyPlaceIntent.putExtra("ADD", true);
                                    startActivity(addMyPlaceIntent);
                                    finish();
                                    break;
                                case R.id.action_delete:
                                    final int position = (Integer) myPlaceView.getTag();
                                    userDatabaseReference.child(MY_PLACES).child(userPlace.getUserPlaceName()).removeValue();
                                    userPlaceHashMap.remove(userPlace.getUserPlaceName());
                                    myPlaceList.removeView(myPlaceView);
                                    Snackbar.make(v, userPlace.getUserPlaceName() + " is deleted", Snackbar.LENGTH_LONG)
                                            .setAction("UNDO", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    userPlaceHashMap.put(userPlace.getUserPlaceName(), userPlace);
                                                    userDatabaseReference.child(MY_PLACES)
                                                            .child(userPlace.getUserPlaceName()).setValue(userPlace.toMap());
                                                    myPlaceList.addView(myPlaceView, position);
                                                }
                                            }).show();
                                    break;
                                case R.id.action_default:
                                    userDatabaseReference.child(MY_PLACES).child("default").setValue(userPlace.getUserPlaceName());
                                    defaultUserPlace = userPlace.getUserPlaceName();
                                    for (int i=0; i<userPlaceHashMap.size(); i++)
                                        myPlaceList.findViewWithTag(i).findViewById(R.id.my_place_fav_icon).setVisibility(View.GONE);
                                    myPlaceFav.setVisibility(View.VISIBLE);
                                    break;
                            }
                            return true;
                        }
                    });
                    MenuPopupHelper cardMenuHelper = new MenuPopupHelper(context, (MenuBuilder)cardMenu.getMenu(), myPlaceMenu);
                    cardMenuHelper.setForceShowIcon(true);
                    cardMenuHelper.show();
                }
            });
            myPlaceView.setTag(count++);
            myPlaceList.addView(myPlaceView);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_my_place);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMyPlaceIntent.putExtra("ADD", true);
                startActivity(addMyPlaceIntent);
                finish();
            }
        });
    }

}

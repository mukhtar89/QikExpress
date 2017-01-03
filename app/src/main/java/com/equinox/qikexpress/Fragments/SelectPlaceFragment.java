package com.equinox.qikexpress.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.equinox.qikexpress.Activities.ShopListActivity;
import com.equinox.qikexpress.Models.UserPlace;
import com.equinox.qikexpress.R;

import java.util.ArrayList;

import static com.equinox.qikexpress.Models.Constants.CURRENT_ADDRESS;
import static com.equinox.qikexpress.Models.Constants.SELECTED_ADDRESS;
import static com.equinox.qikexpress.Models.Constants.SHOP_TYPE;
import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.defaultUserPlace;
import static com.equinox.qikexpress.Models.DataHolder.userPlaceHashMap;

/**
 * Created by mukht on 1/1/2017.
 */

public class SelectPlaceFragment extends DialogFragment {

    public static SelectPlaceFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(SHOP_TYPE, type);
        SelectPlaceFragment fragment = new SelectPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View selectAddressView = inflater.inflate(R.layout.select_address_layout, null);
        final RadioGroup radioGroupAddress = (RadioGroup) selectAddressView.findViewById(R.id.select_address_radio_group);
        final Spinner myPlaces = (Spinner) selectAddressView.findViewById(R.id.select_other_location_spinner);
        if (userPlaceHashMap.isEmpty()) {
            radioGroupAddress.removeView(radioGroupAddress.findViewById(R.id.select_other_location));
            myPlaces.setVisibility(View.GONE);
        }
        else {
            ArrayAdapter<UserPlace> arrayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, new ArrayList<>(userPlaceHashMap.values()));
            myPlaces.setAdapter(arrayAdapter);
            int count = 0;
            if (defaultUserPlace != null)
                for (UserPlace userPlace : userPlaceHashMap.values()) {
                    if (userPlace.getUserPlaceName().equals(defaultUserPlace))
                        myPlaces.setSelection(count, true);
                    else count++;
                }
        }
        AlertDialog.Builder addAddressDialog = new AlertDialog.Builder(getActivity());
        addAddressDialog.setCancelable(false)
                .setTitle("Select an Address")
                .setIcon(R.drawable.logo)
                .setMessage("Please select an address")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        int checked = radioGroupAddress.getCheckedRadioButtonId();
                        Intent shopListIntent = new Intent(getActivity(), ShopListActivity.class);
                        shopListIntent.putExtra(SHOP_TYPE, getArguments().getString(SHOP_TYPE));
                        switch (checked) {
                            case R.id.select_current_location:
                                shopListIntent.putExtra(SELECTED_ADDRESS, CURRENT_ADDRESS);
                                getActivity().startActivity(shopListIntent);
                                break;
                            case R.id.select_other_location:
                                currentUser.setSelectedAddress((UserPlace) myPlaces.getSelectedItem());
                                shopListIntent.putExtra(SELECTED_ADDRESS, currentUser.getSelectedAddress().getUserPlaceName());
                                getActivity().startActivity(shopListIntent);
                                break;
                            default:
                                show(getActivity().getSupportFragmentManager(), "SelectPlaceFragment");
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        addAddressDialog.setView(selectAddressView);
        return addAddressDialog.create();
    }
}

package com.equinox.qikexpress.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.equinox.qikexpress.Activities.AddMyPlaceActivity;
import com.equinox.qikexpress.Models.GeoAddress;
import com.equinox.qikexpress.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

/**
 * Created by mukht on 1/1/2017.
 */

public class AddPlaceFragment extends DialogFragment {

    private static final String ADDRESS = "address";
    private static final String LOCATION = "location";
    private static final String SHOW_CHECKBOX = "AddAddressDontShow";
    private SharedPreferences sharedPreferences;

    public static AddPlaceFragment newInstance(GeoAddress address, LatLng location) {
        Bundle args = new Bundle();
        Gson gsonUserPlace = new Gson();
        args.putString(ADDRESS, gsonUserPlace.toJson(address));
        args.putString(LOCATION, gsonUserPlace.toJson(location));
        AddPlaceFragment fragment = new AddPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Gson gsonUserPlace = new Gson();
        final GeoAddress address = gsonUserPlace.fromJson(getArguments().getString(ADDRESS), GeoAddress.class);
        final LatLng location = gsonUserPlace.fromJson(getArguments().getString(LOCATION), LatLng.class);
        Spanned message = Html.fromHtml("Do you want to add <b>" + address.getFullAddress() + "</b> to your Places list?");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View doNotShowView = inflater.inflate(R.layout.do_not_show_again_layout, null);
        final CheckBox doNotShowCheckBox = (CheckBox) doNotShowView.findViewById(R.id.do_not_show);
        AlertDialog.Builder addAddressDialog = new AlertDialog.Builder(getActivity());
        if (!sharedPreferences.getBoolean(SHOW_CHECKBOX, false))
            addAddressDialog.setView(doNotShowView);
        addAddressDialog.setCancelable(false)
                .setTitle("Add to My Places")
                .setIcon(R.drawable.logo)
                .setMessage(message)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        if (!sharedPreferences.getBoolean(SHOW_CHECKBOX, false) && doNotShowCheckBox.isChecked())
                            sharedPreferences.edit().putBoolean(SHOW_CHECKBOX, true).apply();
                        Intent addMyPlaceIntent = new Intent(getActivity(), AddMyPlaceActivity.class);
                        addMyPlaceIntent.putExtra(ADDRESS, gsonUserPlace.toJson(address));
                        addMyPlaceIntent.putExtra(LOCATION, gsonUserPlace.toJson(location));
                        getActivity().startActivity(addMyPlaceIntent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        if (!sharedPreferences.getBoolean(SHOW_CHECKBOX, false) && doNotShowCheckBox.isChecked())
                            sharedPreferences.edit().putBoolean(SHOW_CHECKBOX, true).apply();
                    }
                });
        return addAddressDialog.create();
    }
}

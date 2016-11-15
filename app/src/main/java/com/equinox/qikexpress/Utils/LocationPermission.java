package com.equinox.qikexpress.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/30/2016.
 */

public class LocationPermission {

    private Context context;
    private Activity activity;
    private AlertDialog.Builder dialogPermissionBuilder = null;

    public LocationPermission(Context context, Activity activity) {
        this.context = context;
        this.activity= activity;
    }

    public void getLocationPermission()  {
        final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                dialogPermissionBuilder = new AlertDialog.Builder(activity);
                dialogPermissionBuilder.setTitle("Location Access")
                        .setIcon(R.drawable.logo)
                        .setMessage("QikDriver requires you to grant permission to the App for accessing your location.")
                        .setCancelable(false)
                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity, permissions, 1);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogPermissionBuilder.show();
                            }
                        })
                        .setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public Boolean getDialogStatus() {
        if (dialogPermissionBuilder == null)
            return false;
        return true;
    }
}

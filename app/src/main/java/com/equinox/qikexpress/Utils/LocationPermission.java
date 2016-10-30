package com.equinox.qikexpress.Utils;

import android.Manifest;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/30/2016.
 */

public class LocationPermission {

    private Context context;
    private Activity activity;

    public LocationPermission(Context context, Activity activity) {
        this.context = context;
        this.activity= activity;
    }

    public void getLocationPermission(Handler handlerLocation)  {
        final Message msg = new Message();
        final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                AlertDialog.Builder dialogPermissionBuilder = new AlertDialog.Builder(activity);
                dialogPermissionBuilder.setTitle("Location Access")
                        .setIcon(R.drawable.logo)
                        .setMessage("Hajj Navigator requires you to grant permission to the App for accessing your location.")
                        .setCancelable(true)
                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity, permissions, 1);
                                msg.arg1 = 1;
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                msg.arg1 = 0;
                            }
                        })
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
        handlerLocation.sendMessage(msg);
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}

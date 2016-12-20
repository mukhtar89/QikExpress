package com.equinox.qikexpress.Utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.Html;

import com.equinox.qikexpress.Activities.TrackingActivity;
import com.equinox.qikexpress.Enums.ChildState;
import com.equinox.qikexpress.Enums.OrderStatus;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.equinox.qikexpress.Enums.ChildState.ADDED;

/**
 * Created by mukht on 11/22/2016.
 */

public class GenerateNotification {

    private Map<ChildState,Integer> notificationCount = new HashMap<>();
    private Context context;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notifcationBuilder;
    private NotificationCompat.Action assignAction;
    private SharedPreferences preferences;

    public GenerateNotification(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationCount.clear();
        notificationManager = NotificationManagerCompat.from(context);
        Intent mainIntent = new Intent(context, TrackingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        /*assignAction = new NotificationCompat.Action.Builder(R.drawable.ic_navigation_white_48dp,
                "Assign to Me", null).build();*/
        notifcationBuilder = new NotificationCompat.Builder(context);
        notifcationBuilder.setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 250, 250, 250, 250})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(Color.rgb(0,84,0), 2000, 500)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
                //.addAction(assignAction);
    }

    public synchronized void showNotification(ChildState state, Order order, OrderStatus orderStatus) {
        if (System.currentTimeMillis() - order.getTimestamp() < 5000 || !state.equals(ADDED)) {
            /*Intent driverIntent = new Intent(context, DirectionsActivity.class);
            driverIntent.putExtra(ORDER_ID, order.getId());
            driverIntent.putExtra(PLACE_ID, order.getShop().getPlaceId());
            assignAction.actionIntent = PendingIntent.getActivity(context, 0, driverIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
            if (!notificationCount.containsKey(state)) notificationCount.put(state, 0);
            notificationCount.put(state, notificationCount.get(state) + 1);
            notifcationBuilder.setNumber(notificationCount.get(state))
                    .setGroup(state.toString())
                    .setGroupSummary(true);
            switch (state) {
                case ADDED:
                    notifcationBuilder.setContentTitle("New Order Added")
                            .setContentText(Html.fromHtml("New order from <b>" + order.getFrom().getName() + "</b>"))
                            .setTicker("New Order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("You have a new order from <b>" + order.getFrom().getName()
                                            + "</b>. The order is placed at <b>" + order.getShop().getName()
                                            + (order.getOrderValue() == null ? "</b>."
                                            : "</b>. The order value is: <b>" + DataHolder.currentUser.getLocalCurrency()
                                            + " " + order.getOrderValue() + "</b>."))));
                    break;
                case CHANGED:
                    notifcationBuilder.setContentTitle("Order Status Changed")
                            .setContentText(Html.fromHtml("<b>" + orderStatus.toString()
                                    + "</b> to <b>" + order.getOrderStatus().toString() + "</b>"))
                            .setTicker("Order Status change from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("Status of order from <b>" + order.getFrom().getName()
                                            + "</b> at <b>" + order.getShop().getName()
                                            + "</b> has been changed from <b>" + orderStatus.toString()
                                            + "</b> to <b>" + order.getOrderStatus().toString() + "</b>")));

                    break;
                case REMOVED:
                    notifcationBuilder.setContentTitle("Order Completed");
                    notifcationBuilder.setContentText(Html.fromHtml("Order from <b>" + order.getFrom().getName()
                            + "</b> has been fulfilled"))
                            .setTicker("Order fulfilled from QikExpress!");
                    if (order.getDriver() != null)
                        notifcationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("Order from <b>" + order.getFrom().getName()
                                            + "</b> at <b>" + order.getShop().getName()
                                            + "</b> has been assigned to <b>" + order.getDriver().getName() + "</b>")));
                    else notifcationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml("Order from <b>" + order.getFrom().getName()
                                    + "</b> at <b>" + order.getShop().getName()
                                    + "</b> has been completed with <b>" + order.getOrderStatus().toString() + "</b> status.")));
                    assignAction.actionIntent = null;
                    break;
            }
            fetchLargeIcon(order);
        }
    }

    private void fetchLargeIcon(final Order order)  {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                } return null;
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                notifcationBuilder.setLargeIcon(bitmap);
                notificationManager.notify(order.getId().hashCode(), notifcationBuilder.build());
            }
        }.execute(order.getFrom().getPhotoURL());
    }

    public void reset() {
        notificationCount.clear();
    }
}


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

import com.equinox.qikexpress.Activities.DeliveryTrackingActivity;
import com.equinox.qikexpress.Activities.TrackingActivity;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.equinox.qikexpress.Models.Constants.BUSINESS_EMPLOYEE;
import static com.equinox.qikexpress.Models.Constants.DEADLINE;
import static com.equinox.qikexpress.Models.Constants.DRIVER;
import static com.equinox.qikexpress.Models.Constants.ORDER_STATUS;
import static com.equinox.qikexpress.Models.Constants.TIMESTAMP;

/**
 * Created by mukht on 11/22/2016.
 */

public class OrderNotification {

    private Context context;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notifcationBuilder;
    private SharedPreferences preferences;
    private Intent mainIntent;
    private PendingIntent pendingIntent;

    public OrderNotification(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationManager = NotificationManagerCompat.from(context);
        notifcationBuilder = new NotificationCompat.Builder(context);
        notifcationBuilder.setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setVibrate(new long[] { 250, 250, 250, 250})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(Color.rgb(160,0,0), 2000, 500)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("QIKEXPRESS")
                .setGroupSummary(true);
    }

    public synchronized void showNotification(String type, Order order, Object oldValue) {
        switch (type) {
            case TIMESTAMP:
                mainIntent = new Intent(context, TrackingActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notifcationBuilder.setContentTitle("New Order Added")
                        .setContentText(Html.fromHtml("New order from <b>" + order.getFrom().getName() + "</b>"))
                        .setTicker("New Order from QikExpress!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml("Your order is placed at <b>" + order.getShop().getName()
                                        + (order.getOrderValue() == null ? "</b>."
                                        : "</b>. The order value is: <b>" + DataHolder.currentUser.getLocalCurrency()
                                        + " " + order.getOrderValue() + "</b>."))))
                        .setContentIntent(pendingIntent);
                fetchLargeIcon(order, TIMESTAMP);
                break;
            case ORDER_STATUS:
                mainIntent = new Intent(context, TrackingActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notifcationBuilder.setContentTitle("Order Status Changed")
                        .setContentText(Html.fromHtml("<b>" + oldValue.toString()
                                + "</b> to <b>" + order.getOrderStatus().toString() + "</b>"))
                        .setTicker("Order Status change from QikExpress!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml("Status of your order at <b>" + order.getShop().getName()
                                        + "</b> has been changed from <b>" + oldValue.toString()
                                        + "</b> to <b>" + order.getOrderStatus().toString() + "</b>")))
                        .setContentIntent(pendingIntent);
                fetchLargeIcon(order, ORDER_STATUS);
                break;
            case DRIVER:
                mainIntent = new Intent(context, DeliveryTrackingActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (order.getDriver() != null && oldValue == null) {
                    notifcationBuilder.setContentTitle("Driver Assigned!")
                            .setContentText(Html.fromHtml("Driver assigned to your order at <b>"
                            + order.getShop().getName() + "</b>"))
                            .setTicker("Driver assigned to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("Your order at <b>" + order.getShop().getName()
                                            + "</b> has been assigned to <b>" + order.getDriver().getName() + "</b>")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, DRIVER);
                } else if (order.getDriver() != null && oldValue != null) {
                    notifcationBuilder.setContentTitle("Driver Changed!")
                            .setContentText(Html.fromHtml("Driver assigned for your order at <b>"
                                    + order.getShop().getName() + "</b> has changed"))
                            .setTicker("Driver assigned changed to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("The driver for your order at <b>" + order.getShop().getName()
                                            + "</b> has been changed from <b>" + oldValue.toString() + "</b>"
                                            + " to <b>" + order.getDriver().getName() + "</b>")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, DRIVER);
                }
                else if (order.getDriver() == null && oldValue != null) {
                    mainIntent = new Intent(context, TrackingActivity.class);
                    pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notifcationBuilder.setContentTitle("Driver Cancelled!")
                            .setContentText(Html.fromHtml("Driver assigned for your order at <b>"
                                    + order.getShop().getName() + "</b> has cancelled"))
                            .setTicker("Driver cancelled to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("<b>" + oldValue.toString() + "</b> has cancelled delivery "
                                            + "of your order at <b>" + order.getShop().getName()
                                            + "</b> Please wait till the next driver takes up your order")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, DRIVER);
                }
                break;
            case BUSINESS_EMPLOYEE:
                mainIntent = new Intent(context, TrackingActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (order.getEmployee() != null && oldValue == null) {
                    notifcationBuilder.setContentTitle("Employee Assigned!")
                            .setContentText(Html.fromHtml("Employee assigned to your order at <b>"
                                    + order.getShop().getName() + "</b>"))
                            .setTicker("Employee assigned to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("Your order at <b>" + order.getShop().getName()
                                            + "</b> has been assigned to <b>" + order.getDriver().getName() + "</b>")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, BUSINESS_EMPLOYEE);
                } else if (order.getEmployee() != null && oldValue != null) {
                    notifcationBuilder.setContentTitle("Employee Changed!")
                            .setContentText(Html.fromHtml("Employee assigned for your order at <b>"
                                    + order.getShop().getName() + "</b> has changed"))
                            .setTicker("Employee assigned changed to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("The driver for your order at <b>" + order.getShop().getName()
                                            + "</b> has been changed from <b>" + oldValue.toString() + "</b>"
                                            + " to <b>" + order.getEmployee().getName() + "</b>")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, BUSINESS_EMPLOYEE);
                }
                else if (order.getEmployee() == null && oldValue != null) {
                    notifcationBuilder.setContentTitle("Employee Cancelled!")
                            .setContentText(Html.fromHtml("Employee assigned for your order at <b>"
                                    + order.getShop().getName() + "</b> has cancelled"))
                            .setTicker("Employee cancelled to your order from QikExpress!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml("<b>" + oldValue.toString() + "</b> has cancelled delivery "
                                            + "of your order at <b>" + order.getShop().getName()
                                            + "</b> Please wait till the next employee takes up your order")))
                            .setContentIntent(pendingIntent);
                    fetchLargeIcon(order, BUSINESS_EMPLOYEE);
                }
                break;
            case DEADLINE:
                mainIntent = new Intent(context, TrackingActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notifcationBuilder.setContentTitle("Order Deadline Changed")
                        .setContentText(Html.fromHtml("Your order at <b>" + order.getShop().getName()
                                + "</b> has new deadline: <b>" + StringManipulation.getFormattedTime(order.getDeadline()) + "</b>"))
                        .setTicker("Order Deadline change from QikExpress!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml("Status of your order at <b>" + order.getShop().getName()
                                        + "</b> has been changed from <b>" + oldValue.toString()
                                        + "</b> to <b>" + order.getOrderStatus().toString() + "</b>")))
                        .setContentIntent(pendingIntent);
                fetchLargeIcon(order, DEADLINE);
                break;
        }
    }

    private void fetchLargeIcon(final Order order, String type)  {
        AsyncTask<String, Void, Bitmap> fetchImage = new AsyncTask<String, Void, Bitmap>() {
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
        };
        switch (type) {
            case TIMESTAMP:
            case ORDER_STATUS:
            case DEADLINE:
                if (order.getShop().getBrandImage() != null)
                    fetchImage.execute(order.getShop().getBrandImage());
                else notifyDefault(order);
                break;
            case DRIVER:
                if (order.getDriver() != null && order.getDriver().getPhotoURL() != null)
                    fetchImage.execute(order.getDriver().getPhotoURL());
                else notifyDefault(order);
                break;
            case BUSINESS_EMPLOYEE:
                if (order.getEmployee() != null && order.getEmployee().getPhotoURL() != null)
                    fetchImage.execute(order.getEmployee().getPhotoURL());
                else notifyDefault(order);
                break;
        }
    }

    private void notifyDefault(Order order) {
        notifcationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));
        notificationManager.notify(order.getId().hashCode(), notifcationBuilder.build());
    }
}


package com.equinox.qikexpress.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyachi.stepview.VerticalStepView;
import com.equinox.qikexpress.Enums.OrderStatus;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.google.android.gms.maps.GoogleMap;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Activities.TrackingActivity.orderIdList;
import static com.equinox.qikexpress.Activities.TrackingActivity.orderTrackingList;
import static com.equinox.qikexpress.Enums.OrderStatus.COMPLETED;
import static com.equinox.qikexpress.Enums.OrderStatus.DELIVERED;
import static com.equinox.qikexpress.Enums.OrderStatus.ENROUTE;
import static com.equinox.qikexpress.Enums.OrderStatus.INCOMING;
import static com.equinox.qikexpress.Enums.OrderStatus.PICKED_UP;
import static com.equinox.qikexpress.Enums.OrderStatus.PROCESSING;
import static com.equinox.qikexpress.Models.Constants.STATUS_TIMESTAMP;

/**
 * Created by mukht on 12/20/2016.
 */

public class OrderFragment extends Fragment {

    private LayoutInflater inflaterMain;
    private Hashtable<OrderStatus,LinearLayout> trainView, timeView;
    private Hashtable<OrderStatus,CardView> cardView;
    private Hashtable<OrderStatus,TextView> statusMessageView, timestampView;
    private ImageView shopBrandImage, employeeImage, driverImage;
    private Order currentOrder;
    private OrderStatus[] orderStatusList = new OrderStatus[] {INCOMING, PROCESSING, COMPLETED, PICKED_UP, ENROUTE, DELIVERED};

    public static OrderFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        currentOrder = orderTrackingList.get(orderIdList.get(getArguments().getInt("POSITION")));
        inflaterMain = inflater;

        initViews(rootView);
        trainViewEngine();
        setTextEngine();
        setTimestampEngine();
        setAlphaViews();

        return rootView;
    }

    private void initViews(View rootView) {
        trainView = new Hashtable<>();
        trainView.put(INCOMING, (LinearLayout) rootView.findViewById(R.id.order_chain_incoming));
        trainView.put(PROCESSING, (LinearLayout) rootView.findViewById(R.id.order_chain_processing));
        trainView.put(COMPLETED, (LinearLayout) rootView.findViewById(R.id.order_chain_completed));
        trainView.put(PICKED_UP, (LinearLayout) rootView.findViewById(R.id.order_chain_pickedup));
        trainView.put(ENROUTE, (LinearLayout) rootView.findViewById(R.id.order_chain_enroute));
        trainView.put(DELIVERED, (LinearLayout) rootView.findViewById(R.id.order_chain_delivered));

        timeView = new Hashtable<>();
        timeView.put(INCOMING, (LinearLayout) rootView.findViewById(R.id.time_submitted_incoming));
        timeView.put(PROCESSING, (LinearLayout) rootView.findViewById(R.id.time_submitted_processing));
        timeView.put(COMPLETED, (LinearLayout) rootView.findViewById(R.id.time_submitted_completed));
        timeView.put(PICKED_UP, (LinearLayout) rootView.findViewById(R.id.time_submitted_pickedup));
        timeView.put(ENROUTE, (LinearLayout) rootView.findViewById(R.id.time_submitted_enroute));
        timeView.put(DELIVERED, (LinearLayout) rootView.findViewById(R.id.time_submitted_delivered));

        cardView = new Hashtable<>();
        cardView.put(INCOMING, (CardView) rootView.findViewById(R.id.order_status_card_incoming));
        cardView.put(PROCESSING, (CardView) rootView.findViewById(R.id.order_status_card_processing));
        cardView.put(COMPLETED, (CardView) rootView.findViewById(R.id.order_status_card_completed));
        cardView.put(PICKED_UP, (CardView) rootView.findViewById(R.id.order_status_card_pickedup));
        cardView.put(ENROUTE, (CardView) rootView.findViewById(R.id.order_status_card_enroute));
        cardView.put(DELIVERED, (CardView) rootView.findViewById(R.id.order_status_card_delivered));

        statusMessageView = new Hashtable<>();
        statusMessageView.put(INCOMING, (TextView) rootView.findViewById(R.id.status_text_message_incoming));
        statusMessageView.put(PROCESSING, (TextView) rootView.findViewById(R.id.status_text_message_processing));
        statusMessageView.put(COMPLETED, (TextView) rootView.findViewById(R.id.status_text_message_completed));
        statusMessageView.put(PICKED_UP, (TextView) rootView.findViewById(R.id.status_text_message_pickedup));
        statusMessageView.put(ENROUTE, (TextView) rootView.findViewById(R.id.status_text_message_enroute));
        statusMessageView.put(DELIVERED, (TextView) rootView.findViewById(R.id.status_text_message_delivered));

        timestampView = new Hashtable<>();
        timestampView.put(INCOMING, (TextView) rootView.findViewById(R.id.timestamp_incoming));
        timestampView.put(PROCESSING, (TextView) rootView.findViewById(R.id.timestamp_processing));
        timestampView.put(COMPLETED, (TextView) rootView.findViewById(R.id.timestamp_completed));
        timestampView.put(PICKED_UP, (TextView) rootView.findViewById(R.id.timestamp_pickedup));
        timestampView.put(ENROUTE, (TextView) rootView.findViewById(R.id.timestamp_enroute));
        timestampView.put(DELIVERED, (TextView) rootView.findViewById(R.id.timestamp_delivered));
    }

    private void setAlphaViews() {
        int position = 0;
        for (int i=0; i<6; i++)
            if (orderStatusList[i].equals(currentOrder.getOrderStatus())) {
                position = i; break;
            }
        for (int i=0; i<6; i++) {
            timeView.get(orderStatusList[i]).setAlpha(i <= position ? 1f : 0.3f);
            cardView.get(orderStatusList[i]).setAlpha(i <= position ? 1f : 0.3f);
        }
    }

    private void setTextEngine() {
        statusMessageView.get(INCOMING).setText(Html.fromHtml("Your order has been placed at <b>" + currentOrder.getShop().getName()
                + "</b> at <b>" + StringManipulation.getFormattedTime(currentOrder.getTimestamp()) + "</b"));
        if (currentOrder.getEmployee() == null)
            statusMessageView.get(PROCESSING).setText(Html.fromHtml("Your order will be assigned to one of the employees. Please wait till <b>"
                    + StringManipulation.getFormattedTime(currentOrder.getDeadline()) + "</b"));
        else statusMessageView.get(PROCESSING).setText(Html.fromHtml("Your order has been assigned to <b>" + currentOrder.getEmployee().getName()
                + "</b> who will finish the task within <b>"
                + StringManipulation.getFormattedTime(currentOrder.getDeadline()) + "</b"));
        String currentAddress = currentOrder.getFrom().getSelectedAddress() != null
                ? currentOrder.getFrom().getSelectedAddress().getAddress().getFullAddress()
                : currentOrder.getFrom().getCurrentAddress().getFullAddress();
        if (currentOrder.getDriver() == null) {
            statusMessageView.get(COMPLETED).setText("Your order is processed. Driver not yet assigned.");
            statusMessageView.get(PICKED_UP).setText("Your order is to be picked up by driver assigned");
            statusMessageView.get(ENROUTE).setText(Html.fromHtml("Your order will be soon enroute soon to your Address at <b>"
                    + currentAddress + "</b>"));
            statusMessageView.get(DELIVERED).setText("Your order will soon be delivered to your place. Please wait!");
        } else {
            statusMessageView.get(COMPLETED).setText(Html.fromHtml("Your order is processed. Waiting for your driver <b>"
                    + currentOrder.getDriver().getName() + "<b> to reach the store"));
            statusMessageView.get(PICKED_UP).setText(Html.fromHtml("Your order is picked up by <b>" + currentOrder.getDriver().getName() + "</b>"));
            if (currentOrder.getOrderStatus().equals(ENROUTE))
                statusMessageView.get(ENROUTE).setText(Html.fromHtml("Your order is now enroute soon to your Address at <b>"
                    + currentAddress + "</b>"));
            else statusMessageView.get(ENROUTE).setText(Html.fromHtml("Your order will be soon enroute soon to your Address at <b>"
                    + currentAddress + "</b>"));
            if (!currentOrder.getOrderStatus().equals(DELIVERED))
                statusMessageView.get(DELIVERED).setText("Your order will soon be delivered to your place. Please wait!");
            else statusMessageView.get(DELIVERED).setText("Your order is delivered to your place. Enjoy!");
        }
    }

    private void setTimestampEngine() {
        Iterator<Map.Entry<OrderStatus,Long>> iteratorStatusTimestamp = currentOrder.getStatusTimestamp().entrySet().iterator();
        while(iteratorStatusTimestamp.hasNext()) {
            Map.Entry<OrderStatus,Long> pair = iteratorStatusTimestamp.next();
            timestampView.get(pair.getKey()).setText(StringManipulation.getFormattedTime(pair.getValue()));
        }
    }

    private void trainViewEngine() {
        switch (currentOrder.getOrderStatus()) {
            case INCOMING:
                inflaterMain.inflate(R.layout.vertical_train_inactive_top_circle, trainView.get(INCOMING), true);
                for (int i=1; i<5; i++)
                    inflaterMain.inflate(R.layout.vertical_train_inactive_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_bottom_circle, trainView.get(DELIVERED), true);
                break;
            case PROCESSING:
                inflaterMain.inflate(R.layout.vertical_train_active_top_circle, trainView.get(INCOMING), true);
                inflaterMain.inflate(R.layout.vertical_train_active_inactive_middle_circle, trainView.get(PROCESSING), true);
                for (int i=2; i<5; i++)
                    inflaterMain.inflate(R.layout.vertical_train_inactive_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_bottom_circle, trainView.get(DELIVERED), true);
                break;
            case COMPLETED:
                inflaterMain.inflate(R.layout.vertical_train_active_top_circle, trainView.get(INCOMING), true);
                inflaterMain.inflate(R.layout.vertical_train_active_middle_circle, trainView.get(PROCESSING), true);
                inflaterMain.inflate(R.layout.vertical_train_active_inactive_middle_circle, trainView.get(COMPLETED), true);
                for (int i=3; i<5; i++)
                    inflaterMain.inflate(R.layout.vertical_train_inactive_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_bottom_circle, trainView.get(DELIVERED), true);
                break;
            case PICKED_UP:
                inflaterMain.inflate(R.layout.vertical_train_active_top_circle, trainView.get(INCOMING), true);
                for (int i=1; i<3; i++)
                    inflaterMain.inflate(R.layout.vertical_train_active_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_active_inactive_middle_circle, trainView.get(PICKED_UP), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_middle_circle, trainView.get(ENROUTE), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_bottom_circle, trainView.get(DELIVERED), true);
                break;
            case ENROUTE:
                inflaterMain.inflate(R.layout.vertical_train_active_top_circle, trainView.get(INCOMING), true);
                for (int i=1; i<4; i++)
                    inflaterMain.inflate(R.layout.vertical_train_active_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_active_inactive_middle_circle, trainView.get(ENROUTE), true);
                inflaterMain.inflate(R.layout.vertical_train_inactive_bottom_circle, trainView.get(DELIVERED), true);
                break;
            case DELIVERED:
                inflaterMain.inflate(R.layout.vertical_train_active_top_circle, trainView.get(INCOMING), true);
                for (int i=1; i<5; i++)
                    inflaterMain.inflate(R.layout.vertical_train_active_middle_circle, trainView.get(orderStatusList[i]), true);
                inflaterMain.inflate(R.layout.vertical_train_active_bottom_circle, trainView.get(DELIVERED), true);
                break;
        }
    }
}

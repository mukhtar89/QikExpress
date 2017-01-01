package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.equinox.qikexpress.Adapters.OrderPagerAdapter;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Services.OrderService;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.ORDER_ID;
import static com.equinox.qikexpress.Models.DataHolder.orderList;
import static com.equinox.qikexpress.Models.DataHolder.placeMap;

public class TrackingActivity extends BaseOrderActivity {

    private OrderPagerAdapter orderPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private HorizontalStepView headerStepView;
    List<StepBean> stepsBeanList = new ArrayList<>();
    public static Hashtable<String,Order> orderTrackingList = new Hashtable<>();
    public static List<String> orderIdList = new ArrayList<>();
    private ProgressDialog pDialog;
    private static TextView orderId;
    private static TextView distText;
    private static TextView timeText;
    private static NetworkImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderPagerAdapter = new OrderPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(orderPagerAdapter);

        orderId = (TextView) findViewById(R.id.orderId);
        distText = (TextView) findViewById(R.id.dist);
        timeText = (TextView) findViewById(R.id.time);
        profileImage = (NetworkImageView) findViewById(R.id.profile_img);
        headerStepView = (HorizontalStepView) findViewById(R.id.step_view_header);
        StepBean stepBean0 = new StepBean("Request",1);
        StepBean stepBean1 = new StepBean("Processed",0);
        StepBean stepBean2 = new StepBean("Shipped",-1);
        StepBean stepBean3 = new StepBean("Delivered",-1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);

        headerStepView
                .setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, android.R.color.white))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.complted))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.attention));
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                updateHeaderDetail(orderTrackingList.get(orderIdList.get(tab.getPosition())));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent deliveryIntent = new Intent(TrackingActivity.this ,DeliveryTrackingActivity.class);
                        deliveryIntent.putExtra(ORDER_ID, orderIdList.get(tab.getPosition()));
                        startActivity(deliveryIntent);
                    }
                });
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading your orders...");
        pDialog.show();
    }

    @Override
    protected void onServiceConnected() {
        getConsumerServiceInterface().setOrdersHandler(orderListHandler);
        if (orderList.isEmpty())
            getConsumerServiceInterface().getOrders();
        else orderListHandler.sendMessage(new Message());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppVolleyController.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppVolleyController.activityPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, OrderService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, OrderService.class));
    }

    private Handler orderListHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (pDialog != null) pDialog.dismiss();
                orderIdList.clear();
                orderTrackingList.clear();
                for (Order tempOrder : DataHolder.orderList.values())
                    if (tempOrder.isVerified())
                        orderTrackingList.put(tempOrder.getId(), tempOrder);
                //orderTrackingList.putAll(DataHolder.orderList);
                orderIdList.addAll(orderTrackingList.keySet());
                if (orderTrackingList.isEmpty()) {
                    Toast.makeText(TrackingActivity.this, "No Orders found to be tracked!", Toast.LENGTH_SHORT).show();
                    finish();
                    return false;
                } else if (orderPagerAdapter != null) {
                    updateHeaderDetail(orderTrackingList.get(orderIdList.get(0)));
                    orderPagerAdapter.notifyDataSetChanged();
                    if (orderIdList.size() > 1) tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    else tabLayout.setTabMode(TabLayout.MODE_FIXED);
                }
            } catch (NullPointerException e){
                orderListHandler.sendMessage(new Message());
            }
            return false;
        }
    });

    private void updateHeaderDetail(final Order order) {
        orderId.setText("Order ID: " + order.getId());
        if (order.getShop().getBrandImage() != null)
            profileImage.setImageUrl(order.getShop().getBrandImage(), DataHolder.getInstance().getImageLoader());
        else profileImage.setImageUrl(order.getShop().getIconURL(), DataHolder.getInstance().getImageLoader());
        switch (order.getOrderStatus()) {
            case INCOMING:
            case PROCESSING:
                stepsBeanList.get(0).setState(1);
                stepsBeanList.get(1).setState(0);
                stepsBeanList.get(2).setState(-1);
                stepsBeanList.get(3).setState(-1);
                headerStepView.setStepViewTexts(stepsBeanList);
                break;
            case COMPLETED:
                stepsBeanList.get(0).setState(1);
                stepsBeanList.get(1).setState(1);
                stepsBeanList.get(2).setState(0);
                stepsBeanList.get(3).setState(-1);
                headerStepView.setStepViewTexts(stepsBeanList);
                break;
            case PICKED_UP:
            case ENROUTE:
                stepsBeanList.get(0).setState(1);
                stepsBeanList.get(1).setState(1);
                stepsBeanList.get(2).setState(1);
                stepsBeanList.get(3).setState(0);
                headerStepView.setStepViewTexts(stepsBeanList);
                break;
            case DELIVERED:
                stepsBeanList.get(0).setState(1);
                stepsBeanList.get(1).setState(1);
                stepsBeanList.get(2).setState(1);
                stepsBeanList.get(3).setState(1);
                headerStepView.setStepViewTexts(stepsBeanList);
                break;
        }
        Handler handleDistance = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj != null) {
                    String[] params = (String[]) msg.obj;
                    order.getShop().setDistanceFromCurrent(params[0]);
                    order.getShop().setTimeFromCurrent(params[1]);
                    distText.setText(order.getShop().getDistanceFromCurrent() + " km");
                    timeText.setText(order.getShop().getTimeFromCurrent() + " min");
                } else updateHeaderDetail(order);
                return false;
            }
        });
        if (order.getShop().getDistanceFromCurrent() == null) {
            if (placeMap.containsKey(order.getShop().getPlaceId()))
                new DistanceRequest(handleDistance).execute(new LatLng(DataHolder.location.getLatitude(), DataHolder.location.getLongitude()),
                        new LatLng(order.getShop().getLocation().latitude, order.getShop().getLocation().longitude));
        } else {
            distText.setText(order.getShop().getDistanceFromCurrent() + " km");
            timeText.setText(order.getShop().getTimeFromCurrent() + " min");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

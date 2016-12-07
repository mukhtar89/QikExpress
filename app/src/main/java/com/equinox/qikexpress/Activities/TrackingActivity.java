package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.Models.Order;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetOrders;
import com.equinox.qikexpress.Utils.MapUtils.DistanceRequest;
import com.equinox.qikexpress.Utils.StringManipulation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.ORDER_ID;

public class TrackingActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private HorizontalStepView headerStepView;
    List<StepBean> stepsBeanList = new ArrayList<>();
    private static Hashtable<String,Order> orderTrackingList = new Hashtable<>();
    private static List<String> orderIdList = new ArrayList<>();
    private ProgressDialog pDialog;
    private static TextView orderId;
    private static TextView distText;
    private static TextView timeText;
    private static NetworkImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateHeaderDetail(orderTrackingList.get(orderIdList.get(tab.getPosition())));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //updateHeaderDetail(orderTrackingList.get(orderIdList.get(0)));
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //updateHeaderDetail(orderTrackingList.get(orderIdList.get(tab.getPosition())));
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        GetOrders.getOrders(orderListHandler, pDialog);
    }

    private Handler orderListHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            synchronized (DataHolder.lock) {
                if(pDialog != null) pDialog.dismiss();
                orderIdList.clear();
                orderTrackingList.clear();
                orderTrackingList.putAll(DataHolder.orderList);
                orderIdList.addAll(orderTrackingList.keySet());
                if (orderTrackingList.isEmpty()) {
                    Toast.makeText(TrackingActivity.this, "No Orders found to be tracked!", Toast.LENGTH_SHORT).show();
                    finish();
                    return false;
                } else if (mSectionsPagerAdapter != null){
                    updateHeaderDetail(orderTrackingList.get(orderIdList.get(0)));
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    if (orderIdList.size() > 1) tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    else tabLayout.setTabMode(TabLayout.MODE_FIXED);
                }
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
                stepsBeanList.get(0).setState(1);
                stepsBeanList.get(1).setState(0);
                stepsBeanList.get(2).setState(-1);
                stepsBeanList.get(3).setState(-1);
                headerStepView.setStepViewTexts(stepsBeanList);
                break;
            case PROCESSING:
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
            if (DataHolder.getInstance().getPlaceMap().containsKey(order.getShop().getPlaceId()))
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public static PlaceholderFragment newInstance(int position) {
            Bundle args = new Bundle();
            args.putInt("POSITION", position);
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
            VerticalStepView trackerView = (VerticalStepView) rootView.findViewById(R.id.tracking_vertical_stepview);
            final Order currentOrder = orderTrackingList.get(orderIdList.get(getArguments().getInt("POSITION")));
            List<String> stepsBeanListVertical = new ArrayList<>();
            String stepBean0 = "Your order has been placed at " + currentOrder.getShop().getName()
                    + " at " + StringManipulation.getFormattedTime(currentOrder.getTimestamp());
            String stepBean1 = "Your order has been assigned to one of the employees. Please wait till "
                    + StringManipulation.getFormattedTime(currentOrder.getDeadline());
            String stepBean2, stepBean3, stepBean4, stepBean5;
            if (currentOrder.getDriver() != null) {
                stepBean2 = "Your order is processed. Waiting for " + currentOrder.getDriver().getName() + " to reach the store";
                stepBean3 = "Your order is picked up by " + currentOrder.getDriver().getName();
                stepBean4 = "Your order is enroute to your Address at ";
                stepBean5 = "Your order is delivered to your place. Enjoy!";
            } else {
                stepBean2 = "Your order is processed. Driver not yet assigned";
                stepBean3 = "Your order is to be picked up by driver assigned";
                stepBean4 = "Your order will be enroute soon to your Address at ";
                stepBean5 = "Your order will soon be delivered to your place. Please wait!";
            }
            stepBean3 += " from " + currentOrder.getShop().getAddress().getFullAddress();
            stepBean4 += DataHolder.currentUser.getPermAddress().getFullAddress();
            stepsBeanListVertical.add(stepBean0);
            stepsBeanListVertical.add(stepBean1);
            stepsBeanListVertical.add(stepBean2);
            stepsBeanListVertical.add(stepBean3);
            stepsBeanListVertical.add(stepBean4);
            stepsBeanListVertical.add(stepBean5);
            trackerView.setTextSize(15);
            trackerView
                    .reverseDraw(false)//default is true
                    .setStepViewTexts(stepsBeanListVertical)//总步骤
                    .setLinePaddingProportion(1.30f)//设置indicator线与线间距的比例系数
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark))//设置StepsViewIndicator完成线的颜色
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))//设置StepsViewIndicator未完成线的颜色
                    .setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark))//设置StepsView text完成线的颜色
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))//设置StepsView text未完成线的颜色
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.complted))//设置StepsViewIndicator CompleteIcon
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_48dp));//设置StepsViewIndicator AttentionIcon
            switch (currentOrder.getOrderStatus()) {
                case INCOMING:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size() - 5);
                    break;
                case PROCESSING:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size() - 4);
                    break;
                case COMPLETED:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size() - 3);
                    break;
                case PICKED_UP:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size() - 2);
                    break;
                case ENROUTE:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size() - 1);
                    break;
                case DELIVERED:
                    trackerView.setStepsViewIndicatorComplectingPosition(stepsBeanListVertical.size());
                    break;
            }
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent deliveryIntent = new Intent(getContext(),DeliveryTrackingActivity.class);
                    deliveryIntent.putExtra(ORDER_ID, currentOrder.getId());
                    getActivity().startActivity(deliveryIntent);
                }
            });
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return orderTrackingList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return orderTrackingList.get(orderIdList.get(position)).getShop().getName();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}

package com.equinox.qikexpress.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.equinox.qikexpress.Fragments.OrderFragment;

import static com.equinox.qikexpress.Activities.TrackingActivity.orderIdList;
import static com.equinox.qikexpress.Activities.TrackingActivity.orderTrackingList;

/**
 * Created by mukht on 12/20/2016.
 */

public class OrderPagerAdapter extends FragmentStatePagerAdapter {

    public OrderPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return OrderFragment.newInstance(position);
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

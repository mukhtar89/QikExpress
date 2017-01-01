package com.equinox.qikexpress.Models;

import com.equinox.qikexpress.Adapters.BaseListRecyclerAdapterFilter;

import java.util.List;

/**
 * Created by mukht on 12/31/2016.
 */

public interface ShopListCommunicator {

    List<? extends Place> getShopList();
    BaseListRecyclerAdapterFilter getListAdapter();

}

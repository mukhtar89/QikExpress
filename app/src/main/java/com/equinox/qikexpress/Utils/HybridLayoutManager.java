package com.equinox.qikexpress.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;

import static com.equinox.qikexpress.Models.DataHolder.mTwoPane;

/**
 * Created by mukht on 10/29/2016.
 */

public class HybridLayoutManager {

    private Activity activity;

    public HybridLayoutManager(Activity activity) {
        this.activity = activity;
    }

    public GridLayoutManager getLayoutManager(int maxWidth) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density  = activity.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        if (mTwoPane) dpWidth *= 0.6;
        int columns = (int) Math.floor(dpWidth/maxWidth);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, columns);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager.canScrollVertically();
        return layoutManager;
    }
}

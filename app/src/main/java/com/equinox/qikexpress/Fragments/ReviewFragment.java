package com.equinox.qikexpress.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.equinox.qikexpress.Adapters.ReviewRecyclerAdapter;
import com.equinox.qikexpress.Models.RatingsManager;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetPlaceDetails;
import com.equinox.qikexpress.Utils.HybridLayoutManager;

import java.util.ArrayList;

/**
 * Created by mukht on 11/2/2016.
 */

public class ReviewFragment extends Fragment {

    private static ArrayList<RatingsManager> ratingsList;
    private static GetPlaceDetails getPlaceDetails;
    private static ProgressDialog pDialog;
    private static ReviewRecyclerAdapter reviewRecyclerAdapter;
    private static RecyclerView reviewRecyclerView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReviewFragment newInstance(String placeId) {
        ReviewFragment fragment = new ReviewFragment();
        ratingsList = new ArrayList<>();
        getPlaceDetails = new GetPlaceDetails(pDialog, updateDataListView);
        getPlaceDetails.parseDetail(placeId);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recycler_view);
        reviewRecyclerAdapter = new ReviewRecyclerAdapter(ratingsList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setAdapter(reviewRecyclerAdapter);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ratingsList.isEmpty())
            hidePDialog();
    }

    private static Handler updateDataListView = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ratingsList.addAll(getPlaceDetails.returnRatingsList());
            reviewRecyclerAdapter.notifyDataSetChanged();
            hidePDialog();
            return false;
        }
    });

    private static void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }
}

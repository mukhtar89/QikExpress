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
import android.widget.Toast;

import com.equinox.qikexpress.Adapters.ReviewRecyclerAdapter;
import com.equinox.qikexpress.Models.Place;
import com.equinox.qikexpress.Models.RatingsManager;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.GetPlaceDetails;
import com.equinox.qikexpress.Utils.HybridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static com.equinox.qikexpress.Models.Constants.PLACE_ID;

/**
 * Created by mukht on 11/2/2016.
 */

public class ReviewFragment extends Fragment {

    private ArrayList<RatingsManager> ratingsList = new ArrayList<>();
    private GetPlaceDetails getPlaceDetails;
    private ProgressDialog pDialog;
    private ReviewRecyclerAdapter reviewRecyclerAdapter;
    private RecyclerView reviewRecyclerView;
    private Handler updateDataListView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReviewFragment newInstance(final String placeId) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(PLACE_ID, placeId);
        fragment.setArguments(args);
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
        updateDataListView = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (getPlaceDetails.returnRatingsList(getArguments().getString(PLACE_ID)) != null) {
                    ratingsList.addAll(getPlaceDetails.returnRatingsList(getArguments().getString(PLACE_ID)));
                    reviewRecyclerAdapter.notifyDataSetChanged();
                } else Toast.makeText(getActivity(), "No Reviews are available for this Place!", Toast.LENGTH_LONG).show();
                hidePDialog();
                return false;
            }
        });
        getPlaceDetails = new GetPlaceDetails(pDialog, updateDataListView);
        getPlaceDetails.parseDetail(getArguments().getString(PLACE_ID));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ratingsList.isEmpty())
            hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }
}

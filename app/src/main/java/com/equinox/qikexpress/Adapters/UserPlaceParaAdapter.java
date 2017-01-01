package com.equinox.qikexpress.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equinox.qikexpress.Models.UserPlace;
import com.equinox.qikexpress.R;
import com.equinox.qikexpress.ViewHolders.ReviewViewHolder;
import com.equinox.qikexpress.ViewHolders.UserPlaceParaViewHolder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 1/1/2017.
 */

public class UserPlaceParaAdapter extends RecyclerView.Adapter<UserPlaceParaViewHolder> {

    private List<String[]> userPlaceParaList;
    private UserPlace addUserPlace;

    public UserPlaceParaAdapter(List<String[]> userPlaceParaList, UserPlace addUserPlace) {
        this.userPlaceParaList = userPlaceParaList;
        this.addUserPlace = addUserPlace;
    }

    @Override
    public UserPlaceParaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_place_para_list_item, parent, false);
        return new UserPlaceParaViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(final UserPlaceParaViewHolder holder, int position) {
        final String[] userPlacePara = userPlaceParaList.get(position);
        holder.getPlaceParaType().setText(userPlacePara[0]);
        holder.getPlaceParaValue().setText(userPlacePara[1]);
        holder.getPlaceParaDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPlaceParaList.remove(holder.getAdapterPosition());
                addUserPlace.removeValue(userPlacePara[0]);
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPlaceParaList.size();
    }
}

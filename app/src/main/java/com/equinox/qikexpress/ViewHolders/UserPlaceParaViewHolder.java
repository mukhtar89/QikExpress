package com.equinox.qikexpress.ViewHolders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/29/2016.
 */

public class UserPlaceParaViewHolder extends RecyclerView.ViewHolder {

    private TextView placeParaType, placeParaValue;
    private ImageView placeParaDelete;

    public UserPlaceParaViewHolder(View itemView) {
        super(itemView);
        placeParaType = (TextView) itemView.findViewById(R.id.place_para_type);
        placeParaValue = (TextView) itemView.findViewById(R.id.place_para_value);
        placeParaDelete = (ImageView) itemView.findViewById(R.id.place_para_delete);
    }

    public TextView getPlaceParaType() {
        return placeParaType;
    }
    public TextView getPlaceParaValue() {
        return placeParaValue;
    }
    public ImageView getPlaceParaDelete() {
        return placeParaDelete;
    }
}

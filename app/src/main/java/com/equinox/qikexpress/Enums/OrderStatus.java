package com.equinox.qikexpress.Enums;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 11/15/2016.
 */

public enum OrderStatus {

    INCOMING("Incoming", android.R.color.holo_red_dark, R.drawable.ic_alarm_add_white_48dp),
    PROCESSING("Processing", android.R.color.holo_orange_dark, R.drawable.ic_alarm_white_48dp),
    COMPLETED("Completed", android.R.color.background_dark, R.drawable.ic_alarm_on_white_48dp),
    PICKED_UP("Picked Up", android.R.color.holo_blue_dark, R.drawable.ic_person_pin_white_48dp),
    ENROUTE("En Route", R.color.colorAccent, R.drawable.ic_near_me_white_48dp),
    DELIVERED("Delivered", android.R.color.holo_green_dark, R.drawable.ic_beenhere_white_48dp),
    CANCELLED("Cancelled", android.R.color.darker_gray, R.drawable.ic_alarm_off_white_48dp),
    REJECTED("Rejected", android.R.color.darker_gray, R.drawable.ic_alarm_off_white_48dp);

    private String name;
    private Integer color, icon;

    OrderStatus(String name, Integer color, Integer icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public Integer getColor() {
        return color;
    }
    public String getName() {
        return name;
    }
    public Integer getIcon() {
        return icon;
    }
}

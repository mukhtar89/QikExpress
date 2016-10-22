package com.equinox.qikexpress;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by mukht on 10/22/2016.
 */

public enum QikList {

    GROCERY(0, "Grocery",  R.drawable.grocery_edited, R.drawable.ic_menu_camera), RESTAURANT(0, "Restaurant",  R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    CLOTHING(0, "Clothing",  R.drawable.grocery_edited, R.drawable.ic_menu_camera), LAUNDRY(0, "Laundry",  R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    DOORTODOOR(0, "Door-to-door",  R.drawable.grocery_edited, R.drawable.ic_menu_camera);

    private int position;
    private String listName;
    private int background, icon;

    QikList(int position, String listName, int background, int icon) {
        this.listName = listName;
        this.position = position;
        this.background = background;
        this.icon = icon;
    }

    public int getPosition() {
        return position;
    }

    public String getListName() {
        return listName;
    }

    public int getBackground() {
        return background;
    }

    public int getIcon() {
        return icon;
    }
}

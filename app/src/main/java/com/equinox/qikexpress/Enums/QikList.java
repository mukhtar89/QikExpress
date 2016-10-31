package com.equinox.qikexpress.Enums;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/22/2016.
 */

public enum QikList {

    GROCERY(0, "Grocery",  "grocery_or_supermarket", R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    RESTAURANT(0, "Restaurant", "restaurant", R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    CLOTHING(0, "Clothing", "clothing_store", R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    LAUNDRY(0, "Laundry", "laundry", R.drawable.grocery_edited, R.drawable.ic_menu_camera),
    DOORTODOOR(0, "Door-to-door", "sublocality", R.drawable.grocery_edited, R.drawable.ic_menu_camera);

    private int position;
    private String listName;
    private String typeName;
    private int background, icon;

    QikList(int position, String listName, String typeName, int background, int icon) {
        this.listName = listName;
        this.typeName = typeName;
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

    public String getTypeName() { return typeName; }

    public int getBackground() {
        return background;
    }

    public int getIcon() {
        return icon;
    }
}

package com.equinox.qikexpress.Enums;

import com.equinox.qikexpress.R;

/**
 * Created by mukht on 10/22/2016.
 */

public enum QikList {

    GROCERY(0, "Grocery", "QikStore", "Groceries", "grocery_or_supermarket", new String[]{"supermarket","grocery"}, R.drawable.grocery_edited, R.drawable.ic_local_grocery_store_white_48dp),
    RESTAURANT(0, "Restaurant", "QikFood", "Restaurants", "restaurant", new String[]{"supermarket","grocery"}, R.drawable.restaurant_edited, R.drawable.ic_restaurant_white_48dp),
    CLOTHING(0, "Clothing", "QikFashion", "Clothing Shops", "clothing_store", new String[]{"supermarket","grocery"}, R.drawable.clothing_edited, R.drawable.ic_shop_white_48dp),
    LAUNDRY(0, "Laundry", "QikLaundry", "Laundries", "laundry", new String[]{"supermarket","grocery"}, R.drawable.laundry_edited, R.drawable.ic_local_laundry_service_white_48dp),
    DOORTODOOR(0, "Door-to-Door", "QikParcel", "N/A", "sublocality", new String[]{"supermarket","grocery"}, R.drawable.delivery_edited, R.drawable.ic_directions_run_white_48dp);


    private int position;
    private String listName;
    private String plural;
    private String faceName;
    private String typeName;
    private String[] keyword;
    private int background, icon;

    QikList(int position, String listName, String faceName, String plural, String typeName, String[] keyword, int background, int icon) {
        this.listName = listName;
        this.plural = plural;
        this.faceName = faceName;
        this.typeName = typeName;
        this.keyword = keyword;
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
    public String[] getKeyword() {
        return keyword;
    }
    public String getPlural() {
        return plural;
    }
    public String getFaceName() {
        return faceName;
    }
}

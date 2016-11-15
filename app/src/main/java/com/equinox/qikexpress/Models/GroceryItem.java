package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 11/3/2016.
 */
public class GroceryItem  extends  Item{

    private List<String> catLevel;

    public List<String> getCatLevel() {return catLevel;}
    public void setCatLevel(List<String> catLevel) {
        this.catLevel = catLevel;
    }
}

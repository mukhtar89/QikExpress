package com.equinox.qikexpress.Enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 11/14/2016.
 */

public enum Vehicle {

    WALK("Walk", "walking", 2, 5), BICYCLE("Cycle", "biking", 5, 10),
    BIKE("Bike", "driving", 20, 20), PICKUP("PickUp", "driving",40,100);

    private String name,mode;
    private int range, weight;

    Vehicle(String name, String mode, int range, int weight) {
        this.name = name;
        this.mode = mode;
        this.range = range;
        this.weight = weight;
    }

    public String getMode() {
        return mode;
    }
    public Integer getRange() {
        return range;
    }
    public Integer getWeight() {
        return weight;
    }
    public String getName() {
        return name;
    }
    public static List<String> getListNames() {
        List<String> tempList = new ArrayList<>();
        tempList.add("All");
        for (Vehicle vehicle : Vehicle.values())
            tempList.add(vehicle.getName());
        return tempList;
    }
    public static List<Integer> getListRanges() {
        List<Integer> tempList = new ArrayList<>();
        tempList.add(2);
        tempList.add(5);
        tempList.add(10);
        tempList.add(20);
        return tempList;
    }
}

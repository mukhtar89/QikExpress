package com.equinox.qikexpress.Enums;

/**
 * Created by mukht on 11/15/2016.
 */

public enum OrderStatus {

    INCOMING("order_incoming"),
    PROCESSING("order_processing"),
    COMPLETED("order_completed"),
    CANCELLED("order_cancelled"),
    PICKED_UP("order_picked_up"),
    ENROUTE("order_enroute"),
    DELIVERED("order_delivered");

    private String nodeName;

    OrderStatus(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}

package com.vince.empire.mbasera.navhistory.adapter.model;

/**
 * Created by VinceGee on 07/14/2016.
 */
public class Order {
    private String orderID;
    private String date;
    private String desc;
    private String total;

    public Order(String orderID, String date, String desc, String total) {
        this.orderID = orderID;
        this.date = date;
        this.desc = desc;
        this.total = total;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getTotal() {
        return total;
    }
}

package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class SceduleItem {
    private String waynumber;
    private String time;
    private String waytitle;
    private String busnumber;
    private String x_coord;
    private String y_coord;
    private String state;
    private String routeCode;


    public SceduleItem(String waynumber, String time, String waytitle, String busnumber, String x_coord, String y_coord, String state, String routeCode) {
        this.waynumber = waynumber;
        this.time = time;
        this.waytitle = waytitle;
        this.busnumber = busnumber;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.state = state;
        this.routeCode = routeCode;
    }

    public String getWaynumber() {
        return waynumber;
    }

    public void setWaynumber(String waynumber) {
        this.waynumber = waynumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWaytitle() {
        return waytitle;
    }

    public void setWaytitle(String waytitle) {
        this.waytitle = waytitle;
    }

    public String getBusnumber() {
        return busnumber;
    }

    public void setBusnumber(String busnumber) {
        this.busnumber = busnumber;
    }

    public String getX_coord() {return x_coord;}

    public void setX_coord(String x_coord) {this.x_coord = x_coord;}

    public String getY_coord() {return y_coord;}

    public void setY_coord(String y_coord) {this.y_coord = y_coord;}

    public String getState() {return state;}

    public void setState(String state) {this.state = state;}

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
}

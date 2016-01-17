package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class SceduleItem {
    private String waynumber;
    private String time;
    private String waytitle;
    private String busnumber;

    public SceduleItem(String waynumber, String time, String waytitle, String busnumber) {
        this.waynumber = waynumber;
        this.time = time;
        this.waytitle = waytitle;
        this.busnumber = busnumber;
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

}

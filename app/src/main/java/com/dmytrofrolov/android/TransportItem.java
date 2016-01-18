package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class TransportItem {
    private String title;
    private String code;


    public TransportItem(String title, String code) {
        super();
        this.title = title;
        this.code = code;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}

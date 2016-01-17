package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class StopItem {
    private String title;
    private String description;


    public StopItem(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

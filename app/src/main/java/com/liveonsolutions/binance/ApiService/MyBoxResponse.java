package com.liveonsolutions.binance.ApiService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyBoxResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("boxLabel")
    @Expose
    private String boxLabel;

    @SerializedName("unlock")
    @Expose
    private boolean unlocked;

    @SerializedName("days")
    @Expose
    private String days;

    @SerializedName("daysToOpen")
    @Expose
    private String daysToOpen;

    @SerializedName("dueDate")
    @Expose
    private String dueDate;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public String getId() {
        return id;
    }

    public String getBoxLabel() {
        return boxLabel;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public String getDays() {
        return days;
    }

    public String getDaysToOpen() {
        return daysToOpen;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

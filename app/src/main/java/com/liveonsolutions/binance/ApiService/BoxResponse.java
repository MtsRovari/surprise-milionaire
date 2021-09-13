package com.liveonsolutions.binance.ApiService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoxResponse {

    @SerializedName("id")
    @Expose
    private String boxId;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("days")
    @Expose
    private String days;

    public String getBoxId() {
        return boxId;
    }

    public String getLabel() {
        return label;
    }

    public String getDays() {
        return days;
    }
}

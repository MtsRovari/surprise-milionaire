package com.liveonsolutions.binance.ApiService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiSuccessResponse {

    @SerializedName("success")
    @Expose
    private boolean success;

    public boolean isSuccess() {
        return success;
    }
}

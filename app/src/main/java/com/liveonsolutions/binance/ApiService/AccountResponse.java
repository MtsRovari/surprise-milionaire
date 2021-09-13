package com.liveonsolutions.binance.ApiService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AccountResponse {

    @SerializedName("amountTotal")
    @Expose
    private String amountTotal;

    public String getAmountTotal() {
        return amountTotal;
    }
}

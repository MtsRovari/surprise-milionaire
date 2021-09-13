package com.liveonsolutions.binance.ApiService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderDetailsResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("isSold")
    @Expose
    private boolean isSold;

    @SerializedName("days")
    @Expose
    private String days;

    @SerializedName("amountTotal")
    @Expose
    private String amountTotal;

    @SerializedName("purchaseTotal")
    @Expose
    private String purchaseTotal;

    @SerializedName("saleTotal")
    @Expose
    private String saleTotal;

    @SerializedName("percentTotal")
    @Expose
    private String percentTotal;

    @SerializedName("orders")
    @Expose
    private List<Orders> orders;

    public String getId() {
        return id;
    }

    public boolean isSold() {
        return isSold;
    }

    public String getDays() {
        return days;
    }

    public String getAmountTotal() {
        return amountTotal;
    }

    public String getPurchaseTotal() {
        return purchaseTotal;
    }

    public String getSaleTotal() {
        return saleTotal;
    }

    public String getPercentTotal() {
        return percentTotal;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public class Orders implements Serializable {

        @SerializedName("symbol")
        @Expose
        private String symbol;

        @SerializedName("purchasePrice")
        @Expose
        private String purchasePrice;

        @SerializedName("purchaseQuantity")
        @Expose
        private String purchaseQuantity;

        @SerializedName("salePrice")
        @Expose
        private String salePrice;

        @SerializedName("saleQuantity")
        @Expose
        private String saleQuantity;

        @SerializedName("percent")
        @Expose
        private String percent;

        public String getSymbol() {
            return symbol;
        }

        public String getPurchasePrice() {
            return purchasePrice;
        }

        public String getPurchaseQuantity() {
            return purchaseQuantity;
        }

        public String getSalePrice() {
            return salePrice;
        }

        public String getSaleQuantity() {
            return saleQuantity;
        }

        public String getPercent() {
            return percent;
        }
    }
}

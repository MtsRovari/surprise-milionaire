package com.liveonsolutions.binance.ApiService;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("account/balances")
    Call<AccountResponse> getAccount();

    @GET("boxs")
    Call<List<BoxResponse>> getBox();

    @GET("orders")
    Call<List<MyBoxResponse>> getMyBox();

    @GET("orders/open")
    Call<List<MyBoxResponse>> getOpenBox();

    @POST("orders/buy")
    Call<ApiSuccessResponse> requestOrderToBuy(@Body RequestBody requestBody);

    @GET("orders/{orderId}")
    Call<OrderDetailsResponse> getOrderDetails(@Path(value="orderId") String orderId);
}
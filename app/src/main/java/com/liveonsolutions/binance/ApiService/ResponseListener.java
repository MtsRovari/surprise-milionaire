package com.liveonsolutions.binance.ApiService;

/**
 * Created by lrodrigues on 26/04/19.
 */
public interface ResponseListener<T> {
    void onSuccess(T o);
    void onFailure(String errorMessage);
}

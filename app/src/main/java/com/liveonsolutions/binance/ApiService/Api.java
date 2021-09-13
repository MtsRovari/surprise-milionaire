package com.liveonsolutions.binance.ApiService;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.liveonsolutions.binance.BuildConfig;
import com.liveonsolutions.binance.R;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Context mContext;
    private static Api instance;
    private Retrofit retrofit;
    private static Call<?> currentCall;

    private Api() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://surprise-millionaire-dev.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private void setCurrentCall(Call<?> currentCall) {
        this.currentCall = currentCall;
    }

    public static Api getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    private String getError(ResponseBody errorBody, String responseCode) {
        String errorMessage = null;
        try {
            Gson gson = new GsonBuilder().create();

            ApiErrorResponse apiErrorResponse = gson.fromJson(errorBody.charStream(), ApiErrorResponse.class);

            errorMessage = apiErrorResponse.getErrorMessage();

        } catch (Exception e) {
            Log.e("**ERR", e.getMessage());
        }

        return errorMessage != null ? errorMessage : responseCode;
    }

    private String treatFailureMessage(Throwable t) {
        String message;
        if (t instanceof SocketTimeoutException) {
            message = mContext.getString(R.string.timeout_exception);
        } else if (t instanceof UnknownHostException) {
            message = mContext.getString(R.string.unknown_host_exception);
        } else {
            message = mContext.getString(R.string.unknown_exception);
            Log.e("API", t.getMessage());
        }

        return message;
    }

    public void getAccount(final ResponseListener listener){
        ApiService service = retrofit.create(ApiService.class);
        Call<AccountResponse> call = service.getAccount();
        getGenericCall(call, listener);
    }

    public void getBox(final ResponseListener listener){
        ApiService service = retrofit.create(ApiService.class);
        Call<List<BoxResponse>> call = service.getBox();
        getGenericCall(call, listener);
    }

    public void getMyBox(final ResponseListener listener){
        ApiService service = retrofit.create(ApiService.class);
        Call<List<MyBoxResponse>> call = service.getMyBox();
        getGenericCall(call, listener);
    }

    public void getOpenBox(final ResponseListener listener){
        ApiService service = retrofit.create(ApiService.class);
        Call<List<MyBoxResponse>> call = service.getOpenBox();
        getGenericCall(call, listener);
    }

    public void requestOrderToBuy(String days, final ResponseListener listener){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("boxId", days);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        ApiService service = retrofit.create(ApiService.class);
        Call<ApiSuccessResponse> call = service.requestOrderToBuy(body);
        getGenericCall(call, listener);
    }

    public void getOrderDetails(String orderId, final ResponseListener listener){
        ApiService service = retrofit.create(ApiService.class);
        Call<OrderDetailsResponse> call = service.getOrderDetails(orderId);
        getGenericCall(call, listener);
    }

    public <T> T getDynamicService(Class<T> serviceInstance) {
        return retrofit.create(serviceInstance);
    }

    public <T> void getGenericCall(Call<T> call, final ResponseListener<T> listener) {
        setCurrentCall(call);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (null == listener) return;

                if (response.isSuccessful())
                    listener.onSuccess(response.body());
                else
                    listener.onFailure(getError(response.errorBody(), String.valueOf(response.code())));
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                listener.onFailure(treatFailureMessage(t));
            }
        });
    }
}

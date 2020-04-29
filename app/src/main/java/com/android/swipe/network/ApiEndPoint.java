package com.android.swipe.network;


import androidx.lifecycle.LiveData;

import com.android.swipe.model.Result;
import com.android.swipe.network.response.ApiResponse;

import retrofit2.http.GET;

public interface ApiEndPoint {

    @GET("api/0.4/?randomapi")
    LiveData<ApiResponse<Result>> getUserInfo();
}

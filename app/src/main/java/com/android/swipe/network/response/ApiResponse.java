package com.android.swipe.network.response;


import java.io.IOException;

import retrofit2.HttpException;
import retrofit2.Response;


/**
 * Generic class for handling responses from Retrofit
 *
 * @param <T>
 */
public class ApiResponse<T> {

    private T data;
    private String error;
    private int statusCode;

    public ApiResponse() {

    }

    private ApiResponse(T data, String error, int status) {
        this.data = data;
        this.error = error;
        this.statusCode = status;
    }

    public ApiResponse<T> create(Response<T> response) {

        if (response.isSuccessful()) {
            return new ApiResponse<>(response.body(), null, response.code());

        } else {
            String errorMsg = "";
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiResponse<>(null, errorMsg, response.code());
        }
    }

    public ApiResponse<T> create(Throwable error) {
        return new ApiResponse<>(null, error.getMessage().equals("")
                ? error.getMessage() : "Unknown error\nCheck network connection",
                ((HttpException) error).code()
        );
    }

    public T getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}






















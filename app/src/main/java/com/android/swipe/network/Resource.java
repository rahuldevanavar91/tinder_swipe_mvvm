package com.android.swipe.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.android.swipe.network.Status.ERROR;
import static com.android.swipe.network.Status.LOADING;
import static com.android.swipe.network.Status.NETWORK_ERROR;
import static com.android.swipe.network.Status.SUCCESS;


public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public T data;

    @Nullable
    private final String message;

    private int statusCode;
    private int updatePosition;


    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, int updatePosition) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.updatePosition = updatePosition;
    }


    public static <T> Resource<T> success(@NonNull T data, int updatePosition) {
        return new Resource<>(SUCCESS, data, null, updatePosition);
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null, 0);
    }


    public static <T> Resource<T> loading() {
        return new Resource<>(LOADING, null, null, 0);
    }

    public static <T> Resource<T> error(String msg) {
        return new Resource<>(ERROR, null, msg == null ?
                "internal server error" : msg, 0);
    }

    public static <T> Resource<T> networkError() {
        return new Resource<>(NETWORK_ERROR, null, "Please check network", 0);
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public int getUpdatePosition() {
        return updatePosition;
    }

    public void setUpdatePosition(int updatePosition) {
        this.updatePosition = updatePosition;
    }
}
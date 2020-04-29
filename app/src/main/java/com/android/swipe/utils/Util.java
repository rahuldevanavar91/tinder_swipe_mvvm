package com.android.swipe.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Util {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null ? cm.getActiveNetworkInfo() : null) != null && cm.getActiveNetworkInfo().isConnected();
    }


}

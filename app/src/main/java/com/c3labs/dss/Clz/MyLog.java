package com.c3labs.dss.Clz;

import android.util.Log;

/**
 * Created by c3 on 2/21/2018.
 */

class MyLog {
    private static final String TAG = "---------------------";

    public static void v(String s) {
        Log.d(TAG, "v: " + s);
    }

    public static void e(String s) {
        Log.d(TAG, "e: " + s);
    }
}

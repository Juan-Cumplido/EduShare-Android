// Logger.java
package com.example.edushareandroid.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "EduShare";
    private static final boolean DEBUG = true; // Cambiar a false en producción

    public static void d(String message) {
        if (DEBUG) Log.d(TAG, message);
    }

    public static void e(String message, Throwable throwable) {
        if (DEBUG) Log.e(TAG, message, throwable);
    }

    public static void i(String message) {
        if (DEBUG) Log.i(TAG, message);
    }

    public static void w(String message) {
        if (DEBUG) Log.w(TAG, message);
    }
}
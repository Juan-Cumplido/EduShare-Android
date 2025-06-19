package com.example.edushareandroid.network.websocket;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication instancia;

    @Override
    public void onCreate() {
        super.onCreate();
        instancia = this;
    }

    public static Context getContext() {
        return instancia.getApplicationContext();
    }
}

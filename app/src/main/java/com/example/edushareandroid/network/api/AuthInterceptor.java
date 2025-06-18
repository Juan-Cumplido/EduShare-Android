package com.example.edushareandroid.network.api;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.edushareandroid.utils.SesionUsuario;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            // Token inválido o expirado
            SesionUsuario.cerrarSesion(context); // Borra el token

            // Envía un broadcast local para notificar al resto de la app
            Intent intent = new Intent("SESION_EXPIRADA");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        return response;
    }
}

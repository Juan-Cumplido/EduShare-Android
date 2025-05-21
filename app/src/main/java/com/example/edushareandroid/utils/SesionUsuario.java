package com.example.edushareandroid.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SesionUsuario {
    private static final String PREF_NAME = "usuario_prefs";
    private static final String KEY_LOGUEADO = "usuario_logueado";

    public static boolean isUsuarioLogueado(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_LOGUEADO, false);
    }

    public static void guardarEstadoLogueado(Context context, boolean logueado) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGUEADO, logueado).apply();
    }

    public static void cerrarSesion(Context context) {
        guardarEstadoLogueado(context, false);
    }
}

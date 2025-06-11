package com.example.edushareandroid.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.edushareandroid.model.base_de_datos.UsuarioData;

public class SesionUsuario {
    private static final String PREF_NAME = "usuario_prefs";
    private static final String KEY_TOKEN = "token_jwt";
    private static final String KEY_LOGUEADO = "usuario_logueado";

    public static boolean isUsuarioLogueado(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_LOGUEADO, false);
    }
    public static void guardarToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String obtenerToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    public static void guardarEstadoLogueado(Context context, boolean logueado) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGUEADO, logueado).apply();
    }

    public static void cerrarSesion(Context context) {
        guardarEstadoLogueado(context, false);
    }

    public static void guardarDatosUsuario(Context context, UsuarioData usuario) {
        SharedPreferences prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        prefs.edit()
                .putInt("idUsuarioRegistrado", usuario.getIdUsuarioRegistrado())
                .putString("nombre", usuario.getNombre())
                .putString("fotoPerfil", usuario.getFotoPerfil())
                .apply();
    }

    public static UsuarioData obtenerDatosUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        UsuarioData usuario = new UsuarioData();
        usuario.setIdUsuarioRegistrado(prefs.getInt("idUsuarioRegistrado", -1));
        usuario.setNombre(prefs.getString("nombre", ""));
        usuario.setFotoPerfil(prefs.getString("fotoPerfil", ""));
        return usuario;
    }

}

package com.example.edushareandroid.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.edushareandroid.ui.login.UsuarioData;

public class SesionUsuario {
    private static final String PREF_NAME = "sesion_usuario";
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
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(KEY_TOKEN);
        editor.remove(KEY_LOGUEADO);
        editor.remove("idUsuarioRegistrado");
        editor.remove("nombre");
        editor.remove("fotoPerfil");
        editor.apply();

        SharedPreferences prefsSecundarias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        prefsSecundarias.edit().remove("nombre_usuario").apply();
    }

    public static void guardarDatosUsuario(Context context, UsuarioData usuario) {
        Log.d("SesionUsuario", "Guardando ID: " + usuario.getIdUsuario());
        SharedPreferences prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        prefs.edit()
                .putInt("idUsuarioRegistrado", usuario.getIdUsuario())
                .putString("nombre", usuario.getNombre())
                .putString("nombreUsuario", usuario.getNombreUsuario())
                .putString("fotoPerfil", usuario.getFotoPerfil())
                .apply();
    }

    public static UsuarioData obtenerDatosUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        UsuarioData usuario = new UsuarioData();
        usuario.setIdUsuario(prefs.getInt("idUsuarioRegistrado", -1));
        usuario.setNombre(prefs.getString("nombre", ""));
        usuario.setFotoPerfil(prefs.getString("fotoPerfil", ""));
        return usuario;
    }

    public static void guardarNombreUsuario(Context context, String nombreUsuario) {
        SharedPreferences prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        prefs.edit().putString("nombre_usuario", nombreUsuario).apply();
    }

    public static String obtenerNombreUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return prefs.getString("nombre_usuario", null);
    }

}

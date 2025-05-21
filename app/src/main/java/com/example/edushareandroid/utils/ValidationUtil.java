package com.example.edushareandroid.utils;

import android.text.TextUtils;

public class ValidationUtil {

    public static boolean isValidCorreo(String correo) {
        return !TextUtils.isEmpty(correo) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 300;
    }

    public static boolean isValidNombreUsuario(String username) {
        return username != null && username.length() >= 4 && username.length() <= 15;
    }

    public static boolean isValidNombre(String nombre) {
        return nombre != null && nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{1,30}$");
    }

    public static boolean isValidApellido(String apellido) {
        return apellido != null && apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{1,30}$");
    }

    public static boolean isValidInstitucion(String nombre, String carrera, String nivel) {
        return nombre != null && nombre.length() <= 100 &&
                carrera != null && carrera.length() <= 70 &&
                nivel != null && nivel.length() <= 20;
    }

    public static boolean noEstaVacio(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

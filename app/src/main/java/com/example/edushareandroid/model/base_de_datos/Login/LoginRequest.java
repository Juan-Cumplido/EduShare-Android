package com.example.edushareandroid.model.base_de_datos.Login;

public class LoginRequest {
    private String identifier; // puede ser correo o nombre de usuario
    private String contrasenia; // en formato SHA-256

    // Constructor vacío
    public LoginRequest() {
    }

    // Constructor con parámetros
    public LoginRequest(String identifier, String contrasenia) {
        this.identifier = identifier;
        this.contrasenia = contrasenia;
    }

    // Getters y setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}

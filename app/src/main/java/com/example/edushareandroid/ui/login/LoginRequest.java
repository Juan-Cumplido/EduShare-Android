package com.example.edushareandroid.ui.login;

public class LoginRequest {
    private String identifier;
    private String contrasenia;

    public LoginRequest() {
    }

    public LoginRequest(String identifier, String contrasenia) {
        this.identifier = identifier;
        this.contrasenia = contrasenia;
    }

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

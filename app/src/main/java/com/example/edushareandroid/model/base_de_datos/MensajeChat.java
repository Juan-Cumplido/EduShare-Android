package com.example.edushareandroid.model.base_de_datos;

public class MensajeChat {
    private String idMensaje;
    private String idChat;
    private String idUsuario;
    private String nombreUsuario;
    private String mensaje;
    private String hora;
    private String fotoPerfil;
    private boolean esMio;

    public MensajeChat() {}

    public MensajeChat(String idMensaje, String idChat, String idUsuario, String nombreUsuario,
                       String mensaje, String hora, String fotoPerfil, boolean esMio) {
        this.idMensaje = idMensaje;
        this.idChat = idChat;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.mensaje = mensaje;
        this.hora = hora;
        this.fotoPerfil = fotoPerfil;
        this.esMio = esMio;
    }

    // Getters
    public String getIdMensaje() { return idMensaje; }
    public String getIdChat() { return idChat; }
    public String getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getMensaje() { return mensaje; }
    public String getHora() { return hora; }
    public String getFotoPerfil() { return fotoPerfil; }
    public boolean isEsMio() { return esMio; }

    // Setters
    public void setIdMensaje(String idMensaje) { this.idMensaje = idMensaje; }
    public void setIdChat(String idChat) { this.idChat = idChat; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setHora(String hora) { this.hora = hora; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setEsMio(boolean esMio) { this.esMio = esMio; }
}
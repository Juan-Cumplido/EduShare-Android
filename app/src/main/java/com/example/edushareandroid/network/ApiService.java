package com.example.edushareandroid.network;

import com.example.edushareandroid.model.base_de_datos.ChangePasswordRequest;
import com.example.edushareandroid.model.base_de_datos.LoginRequest;
import com.example.edushareandroid.model.base_de_datos.LoginResponse;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/edushare/acceso/registro")
    Call<ApiResponse> registrarUsuario(@Body UsuarioRegistro usuario);

    @POST("/edushare/acceso/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/edushare/acceso/recuperarContrasena")
    Call<ApiResponse> recuperarContrasena(@Body RecoveryRequest request);

    @POST("/edushare/acceso/verificarCodigoYCambiarContrasena")
    Call<ApiResponse> cambiarContrasena(@Body ChangePasswordRequest request);

}

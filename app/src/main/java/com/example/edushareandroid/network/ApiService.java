package com.example.edushareandroid.network;

import com.example.edushareandroid.model.home.bd.UsuarioRegistro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/edushare/acceso/registro")
    Call<ApiResponse> registrarUsuario(@Body UsuarioRegistro usuario);
}

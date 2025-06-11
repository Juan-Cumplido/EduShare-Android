package com.example.edushareandroid.network.api;

import com.example.edushareandroid.model.base_de_datos.CambiarContraseniaRequest;
import com.example.edushareandroid.model.base_de_datos.InstitucionesResponse;
import com.example.edushareandroid.model.base_de_datos.LoginRequest;
import com.example.edushareandroid.model.base_de_datos.LoginResponse;
import com.example.edushareandroid.model.base_de_datos.PerfilResponse;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.model.base_de_datos.UsuarioPerfil;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/edushare/acceso/registro")
    Call<ApiResponse> registrarUsuario(@Body UsuarioRegistro usuario);

    @POST("/edushare/acceso/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/edushare/acceso/recuperarContrasena")
    Call<ApiResponse> recuperarContrasena(@Body RecoveryRequest request);

    @POST("/edushare/acceso/verificarCodigoYCambiarContrasena")
    Call<ApiResponse> cambiarContrasena(@Body CambiarContraseniaRequest request);

    @GET("/edushare/catalogo/instituciones")
    Call<InstitucionesResponse> obtenerInstituciones(@Query("nivel") String nivel);
    @GET("/edushare/perfil/me")
    Call<PerfilResponse> obtenerPerfilPropio(@Header("Authorization") String token); // ✅ Correcto

}

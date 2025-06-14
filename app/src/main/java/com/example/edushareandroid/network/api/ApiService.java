package com.example.edushareandroid.network.api;

import com.example.edushareandroid.model.base_de_datos.ActualizarPerfil;
import com.example.edushareandroid.model.base_de_datos.AvatarRequest;
import com.example.edushareandroid.model.base_de_datos.CambiarContraseniaRequest;
import com.example.edushareandroid.model.base_de_datos.CatalogoResponse;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.model.base_de_datos.comentarios.CrearComentarioRequest;
import com.example.edushareandroid.model.base_de_datos.DejarSeguirRequest;
import com.example.edushareandroid.model.base_de_datos.DocumentoRequest;
import com.example.edushareandroid.model.base_de_datos.DocumentoSubidoResponse;
import com.example.edushareandroid.model.base_de_datos.InstitucionesResponse;
import com.example.edushareandroid.model.base_de_datos.Login.LoginRequest;
import com.example.edushareandroid.model.base_de_datos.Login.LoginResponse;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaBase;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaConDatos;
import com.example.edushareandroid.ui.perfil.PerfilResponse;
import com.example.edushareandroid.model.base_de_datos.PerfilResponseList;
import com.example.edushareandroid.model.base_de_datos.PublicacionRequest;
import com.example.edushareandroid.model.base_de_datos.PublicacionResponse;
import com.example.edushareandroid.model.base_de_datos.PublicacionesResponse;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.model.base_de_datos.SeguimientoRequest;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;
import com.example.edushareandroid.model.base_de_datos.VerificacionSeguimientoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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
    Call<PerfilResponse> obtenerPerfilPropio(@Header("Authorization") String token);

    @GET("/edushare/publicaciones/me")
    Call<PublicacionesResponse> obtenerPublicacionesPropias(@Header("Authorization") String token);

    @DELETE("/edushare/publicaciones/{idPublicacion}")
    Call<ApiResponse> eliminarPublicacion(@Path("idPublicacion") int idPublicacion, @Header("Authorization") String token);

    @PUT("/edushare/perfil/me")
    Call<ApiResponse> actualizarPerfil(@Header("Authorization") String token, @Body ActualizarPerfil perfil);

    @PUT("/edushare/perfil/me/avatar")
    Call<ApiResponse> actualizarAvatar(@Header("Authorization") String token, @Body AvatarRequest avatarRequest);

    @GET("/edushare/perfil/")
    Call<PerfilResponseList> buscarPerfiles(@Query("nombreUsuario") String nombreUsuario);

    @GET("/edushare/perfil/{idUsuario}")
    Call<PerfilResponse> obtenerPerfilPorId(@Path("idUsuario") int idUsuario);

    @POST("/edushare/seguimiento/seguir")
    Call<ApiResponse> seguirUsuario(@Header("Authorization") String token, @Body SeguimientoRequest request);

    @HTTP(method = "DELETE", path = "/edushare/seguimiento/dejar-seguir", hasBody = true)
    Call<ApiResponse> dejarDeSeguirUsuario(@Header("Authorization") String token, @Body DejarSeguirRequest request);

    @GET("/edushare/seguimiento/verificar/{idUsuario}")
    Call<VerificacionSeguimientoResponse> verificarSeguimiento(@Header("Authorization") String token, @Path("idUsuario") int idUsuario
    );
    @GET("/edushare/catalogo/categorias")
    Call<CatalogoResponse<List<Categoria>>> obtenerCategorias();

    @GET("/edushare/catalogo/ramas")
    Call<CatalogoResponse<List<Rama>>> obtenerRamas();

    @GET("/edushare/catalogo/materias")
    Call<CatalogoResponse<List<Materia>>> obtenerMateriasPorRama(@Query("idRama") int idRama);

    @POST("/edushare/publicaciones/documento")
    Call<DocumentoSubidoResponse> crearDocumento(@Body DocumentoRequest request, @Header("Authorization") String token);

    @POST("/edushare/publicaciones/")
    Call<PublicacionResponse> crearPublicacion(@Body PublicacionRequest request, @Header("Authorization") String token);

    @POST("/edushare/comentario/")
    Call<RespuestaBase> crearComentario(@Body CrearComentarioRequest request, @Header("Authorization") String token);

    @DELETE("/edushare/comentario/{idComentario}")
    Call<RespuestaBase> eliminarComentario(@Path("idComentario") int idComentario, @Header("Authorization") String token);

    @GET("/edushare/comentario/publicacion/{idPublicacion}")
    Call<RespuestaConDatos<List<Comentario>>> obtenerComentarios(@Path("idPublicacion") int idPublicacion);

    //Endpoint para la implemenracion de like, visaulizacion y descargas
    // Obtener todas las publicaciones
    @GET("/edushare/publicaciones")
    Call<PublicacionesResponse> obtenerPublicaciones();

    // Verificar like
    @GET("/edushare/publicaciones/{id}/like")
    Call<ApiResponse> verificarLike(@Path("id") int idPublicacion, @Header("Authorization") String token);

    // Dar like
    @POST("/edushare/publicaciones/{id}/like")
    Call<ApiResponse> darLike(@Path("id") int idPublicacion, @Header("Authorization") String token);

    // Quitar like
    @DELETE("/edushare/publicaciones/{id}/like")
    Call<ApiResponse> quitarLike(@Path("id") int idPublicacion, @Header("Authorization") String token);

    // Registrar visualización
    @POST("/edushare/publicaciones/{id}/vista")
    Call<ApiResponse> registrarVisualizacion(@Path("id") int idPublicacion);

    // Registrar descarga
    @POST("/edushare/publicaciones/{id}/descarga")
    Call<ApiResponse> registrarDescarga(@Path("id") int idPublicacion, @Header("Authorization") String token);
}

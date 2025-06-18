package com.example.edushareandroid.ui.publicaciones;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.PublicacionesResponse;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.perfil.PerfilResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacionesRepository {
    private static final String TAG = "PublicacionesRepo";
    private final ApiService apiService;

    public PublicacionesRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    /**
     * Enum para definir los tipos de publicaciones que se pueden obtener
     */
    public enum TipoPublicacion {
        PROPIAS,           // Publicaciones del usuario logueado
        TODAS,             // Todas las publicaciones públicas
        DE_USUARIO,        // Publicaciones de un usuario específico
        POR_CATEGORIA,     // Publicaciones filtradas por categoría
        POR_RAMA,
        POR_NIVEL_EDUCATIVO,
    }

    /**
     * Clase para encapsular parámetros de consulta
     */
    public static class ParametrosConsulta {
        private final TipoPublicacion tipo;
        private String token;
        private Integer idUsuario;
        private Integer idCategoria;
        private Integer idRama;
        private String filtro;

        public ParametrosConsulta(TipoPublicacion tipo) {
            this.tipo = tipo;
        }

        public ParametrosConsulta conToken(String token) {
            this.token = token;
            return this;
        }

        public ParametrosConsulta conIdUsuario(int idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }

        public ParametrosConsulta conIdCategoria(int idCategoria) {
            this.idCategoria = idCategoria;
            return this;
        }

        public ParametrosConsulta conIdRama(int idRama) {
            this.idRama = idRama;
            return this;
        }

        public ParametrosConsulta conFiltro(String filtro) {
            this.filtro = filtro;
            return this;
        }

        // Getters
        public TipoPublicacion getTipo() {
            return tipo;
        }

        public String getToken() {
            return token;
        }

        public Integer getIdUsuario() {
            return idUsuario;
        }

        public Integer getIdCategoria() {
            return idCategoria;
        }

        public Integer getIdRama() {
            return idRama;
        }

        public String getFiltro() {
            return filtro;
        }
    }

    /**
     * Método principal para obtener publicaciones según el tipo
     */
    public LiveData<ResultadoPublicaciones> obtenerPublicaciones(ParametrosConsulta parametros) {
        MutableLiveData<ResultadoPublicaciones> resultado = new MutableLiveData<>();

        Log.d(TAG, "Obteniendo publicaciones de tipo: " + parametros.getTipo());

        switch (parametros.getTipo()) {
            case PROPIAS:
                obtenerPublicacionesPropias(parametros.getToken(), resultado);
                break;
            case TODAS:
                obtenerTodasLasPublicaciones(resultado);
                break;
            case DE_USUARIO:
                obtenerPublicacionesDeUsuario(parametros.getIdUsuario(), resultado);
                break;
            case POR_CATEGORIA:
                obtenerPublicacionesPorCategoria(parametros.getIdCategoria(), resultado);
                break;
            case POR_RAMA:
                obtenerPublicacionesPorRama(parametros.getIdRama(), resultado);
                break;
            default:
                resultado.setValue(new ResultadoPublicaciones(false, "Tipo de consulta no válido", new ArrayList<>()));
        }

        return resultado;
    }

    /**
     * Obtener publicaciones propias del usuario logueado
     */
    private void obtenerPublicacionesPropias(String token, MutableLiveData<ResultadoPublicaciones> resultado) {
        if (token == null || token.trim().isEmpty()) {
            resultado.setValue(new ResultadoPublicaciones(false, "Token no válido", new ArrayList<>()));
            return;
        }

        apiService.obtenerPublicacionesPropias("Bearer " + token)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones propias");
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones propias", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });
    }

    /**
     * Obtener todas las publicaciones públicas
     */
    private void obtenerTodasLasPublicaciones(MutableLiveData<ResultadoPublicaciones> resultado) {
        apiService.obtenerPublicaciones()
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "todas las publicaciones");
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener todas las publicaciones", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });
    }

    /**
     * Obtener publicaciones de un usuario específico
     */
    private void obtenerPublicacionesDeUsuario(Integer idUsuario, MutableLiveData<ResultadoPublicaciones> resultado) {
        if (idUsuario == null) {
            resultado.setValue(new ResultadoPublicaciones(false, "ID de usuario no válido", new ArrayList<>()));
            return;
        }
        apiService.obtenerPublicacionesPorUsuario(idUsuario)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones del usuario con ID " + idUsuario);
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones de usuario", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });

        obtenerTodasLasPublicaciones(resultado);
    }

    /**
     * Obtener publicaciones por categoría
     */
    private void obtenerPublicacionesPorCategoria(Integer idCategoria, MutableLiveData<ResultadoPublicaciones> resultado) {
        if (idCategoria == null) {
            resultado.setValue(new ResultadoPublicaciones(false, "ID de categoría no válido", new ArrayList<>()));
            return;
        }

        apiService.obtenerPublicacionesPorCategoria(idCategoria)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones del categoria con ID " + idCategoria);
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones de categoria", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });

        obtenerTodasLasPublicaciones(resultado);
    }

    private void obtenerPublicacionesPorRama(Integer idRama, MutableLiveData<ResultadoPublicaciones> resultado) {
        if (idRama == null) {
            resultado.setValue(new ResultadoPublicaciones(false, "ID de categoría no válido", new ArrayList<>()));
            return;
        }
        apiService.obtenerPublicacionesPorRama(idRama)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones del rama con ID " + idRama);
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones de rama", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });

        obtenerTodasLasPublicaciones(resultado);
    }


    /**
     * Método para procesar la respuesta de la API
     */
    private void procesarRespuestaPublicaciones(Response<PublicacionesResponse> response,
                                                MutableLiveData<ResultadoPublicaciones> resultado,
                                                String tipoConsulta) {
        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
            List<DocumentoResponse> publicaciones = response.body().getDatos();
            String mensaje = publicaciones.isEmpty() ?
                    "No se encontraron " + tipoConsulta :
                    "Se encontraron " + publicaciones.size() + " " + tipoConsulta;

            resultado.setValue(new ResultadoPublicaciones(true, mensaje, publicaciones));
            Log.d(TAG, mensaje);
        } else {
            String errorMsg = "Error al obtener " + tipoConsulta + ": " + response.code();
            Log.e(TAG, errorMsg);
            resultado.setValue(new ResultadoPublicaciones(false, errorMsg, new ArrayList<>()));
        }
    }

    /**
     * Eliminar una publicación
     */
    public LiveData<ResultadoEliminacion> eliminarPublicacion(int idPublicacion, String token) {
        MutableLiveData<ResultadoEliminacion> resultado = new MutableLiveData<>();

        if (token == null || token.trim().isEmpty()) {
            resultado.setValue(new ResultadoEliminacion(false, "Token no válido", idPublicacion));
            return resultado;
        }

        apiService.eliminarPublicacion(idPublicacion, "Bearer " + token)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (!apiResponse.isError()) {
                                resultado.setValue(new ResultadoEliminacion(true, "Publicación eliminada exitosamente", idPublicacion));
                                Log.d(TAG, "Publicación eliminada exitosamente");
                            } else {
                                resultado.setValue(new ResultadoEliminacion(false, apiResponse.getMensaje(), idPublicacion));
                                Log.e(TAG, "Error de la API: " + apiResponse.getMensaje());
                            }
                        } else {
                            String mensaje = "Error al eliminar la publicación. Código: " + response.code();
                            resultado.setValue(new ResultadoEliminacion(false, mensaje, idPublicacion));
                            Log.e(TAG, mensaje);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        String mensaje = "Error de conexión: " + t.getMessage();
                        resultado.setValue(new ResultadoEliminacion(false, mensaje, idPublicacion));
                        Log.e(TAG, "Fallo en la solicitud", t);
                    }
                });

        return resultado;
    }

    /**
     * Clase para encapsular el resultado de las consultas de publicaciones
     */
    public static class ResultadoPublicaciones {
        private final boolean exitoso;
        private final String mensaje;
        private final List<DocumentoResponse> publicaciones;

        public ResultadoPublicaciones(boolean exitoso, String mensaje, List<DocumentoResponse> publicaciones) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.publicaciones = publicaciones != null ? publicaciones : new ArrayList<>();
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public List<DocumentoResponse> getPublicaciones() {
            return publicaciones;
        }

        public int getCantidad() {
            return publicaciones.size();
        }

        public boolean tienePublicaciones() {
            return !publicaciones.isEmpty();
        }
    }

    /**
     * Clase para encapsular el resultado de la eliminación
     */
    public static class ResultadoEliminacion {
        private final boolean exitoso;
        private final String mensaje;
        private final int idPublicacionEliminada;

        public ResultadoEliminacion(boolean exitoso, String mensaje, int idPublicacionEliminada) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.idPublicacionEliminada = idPublicacionEliminada;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public int getIdPublicacionEliminada() {
            return idPublicacionEliminada;
        }
    }
}
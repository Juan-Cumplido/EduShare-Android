package com.example.edushareandroid.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.CatalogoResponse;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.ui.publicaciones.PublicacionesResponse;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private static final String TAG = "HomeRepository";
    private final ApiService apiService;
    private final String token;
    private final Context context;

    public HomeRepository(Context context, String token) {
        this.context = context.getApplicationContext();
        this.token = token != null ? "Bearer " + token : null;
        this.apiService = RetrofitClient.getApiService(context);
    }

    // Constructor sin token para consultas públicas
    public HomeRepository(Context context) {
        this.context = context.getApplicationContext();
        this.token = null;
        this.apiService = RetrofitClient.getApiService(context);
    }

    // =========================== MÉTODOS DE CATÁLOGOS ===========================

    public LiveData<List<Categoria>> obtenerCategorias() {
        MutableLiveData<List<Categoria>> data = new MutableLiveData<>();
        apiService.obtenerCategorias().enqueue(new Callback<CatalogoResponse<List<Categoria>>>() {
            @Override
            public void onResponse(Call<CatalogoResponse<List<Categoria>>> call, Response<CatalogoResponse<List<Categoria>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    data.setValue(response.body().getDatos());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CatalogoResponse<List<Categoria>>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Rama>> obtenerRamas() {
        MutableLiveData<List<Rama>> data = new MutableLiveData<>();
        apiService.obtenerRamas().enqueue(new Callback<CatalogoResponse<List<Rama>>>() {
            @Override
            public void onResponse(Call<CatalogoResponse<List<Rama>>> call, Response<CatalogoResponse<List<Rama>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    data.setValue(response.body().getDatos());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CatalogoResponse<List<Rama>>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Materia>> obtenerMateriasPorRama(int idRama) {
        MutableLiveData<List<Materia>> data = new MutableLiveData<>();
        apiService.obtenerMateriasPorRama(idRama).enqueue(new Callback<CatalogoResponse<List<Materia>>>() {
            @Override
            public void onResponse(Call<CatalogoResponse<List<Materia>>> call, Response<CatalogoResponse<List<Materia>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    data.setValue(response.body().getDatos());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CatalogoResponse<List<Materia>>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    // =========================== MÉTODOS DE PUBLICACIONES ===========================

    /**
     * Obtener todas las publicaciones públicas para el feed principal
     */
    public LiveData<ResultadoPublicaciones> obtenerTodasLasPublicaciones() {
        MutableLiveData<ResultadoPublicaciones> resultado = new MutableLiveData<>();

        Log.d(TAG, "Obteniendo todas las publicaciones para Home");

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

        return resultado;
    }

    /**
     * Obtener publicaciones filtradas por categoría
     */
    public LiveData<ResultadoPublicaciones> obtenerPublicacionesPorCategoria(Integer idCategoria) {
        MutableLiveData<ResultadoPublicaciones> resultado = new MutableLiveData<>();

        if (idCategoria == null) {
            resultado.setValue(new ResultadoPublicaciones(false, "ID de categoría no válido", new ArrayList<>()));
            return resultado;
        }

        Log.d(TAG, "Obteniendo publicaciones por categoría ID: " + idCategoria);

        apiService.obtenerPublicacionesPorCategoria(idCategoria)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones de la categoría con ID " + idCategoria);
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones por categoría", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });

        return resultado;
    }

    /**
     * Obtener publicaciones filtradas por rama educativa
     */
    public LiveData<ResultadoPublicaciones> obtenerPublicacionesPorRama(Integer idRama) {
        MutableLiveData<ResultadoPublicaciones> resultado = new MutableLiveData<>();

        if (idRama == null) {
            resultado.setValue(new ResultadoPublicaciones(false, "ID de rama no válido", new ArrayList<>()));
            return resultado;
        }

        Log.d(TAG, "Obteniendo publicaciones por rama ID: " + idRama);

        apiService.obtenerPublicacionesPorRama(idRama)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        procesarRespuestaPublicaciones(response, resultado, "publicaciones de la rama con ID " + idRama);
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e(TAG, "Error al obtener publicaciones por rama", t);
                        resultado.setValue(new ResultadoPublicaciones(false, "Error de conexión: " + t.getMessage(), new ArrayList<>()));
                    }
                });

        return resultado;
    }

    /**
     * Obtener publicaciones con filtros combinados (para búsquedas avanzadas)
     */
    public LiveData<ResultadoPublicaciones> obtenerPublicacionesConFiltros(Integer idCategoria, Integer idRama) {
        // Si solo hay un filtro, usar el método específico
        if (idCategoria != null && idRama == null) {
            return obtenerPublicacionesPorCategoria(idCategoria);
        } else if (idRama != null && idCategoria == null) {
            return obtenerPublicacionesPorRama(idRama);
        } else if (idCategoria == null && idRama == null) {
            return obtenerTodasLasPublicaciones();
        }

        // Para filtros combinados, podrías implementar lógica adicional aquí
        // Por ahora, prioriza la rama sobre la categoría
        return obtenerPublicacionesPorRama(idRama);
    }

    // =========================== MÉTODOS AUXILIARES ===========================

    /**
     * Método para procesar la respuesta de la API de publicaciones
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

    // =========================== CLASES DE RESULTADO ===========================

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
}
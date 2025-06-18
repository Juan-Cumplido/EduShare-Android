package com.example.edushareandroid.ui.usuarios;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.DejarSeguirRequest;
import com.example.edushareandroid.model.base_de_datos.PublicacionesResponse;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.perfil.PerfilResponse;
import com.example.edushareandroid.model.base_de_datos.PerfilResponseList;
import com.example.edushareandroid.model.base_de_datos.SeguimientoRequest;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.model.base_de_datos.UsuarioPerfilRecuperado;
import com.example.edushareandroid.model.base_de_datos.VerificacionSeguimientoResponse;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository {

    private final ApiService apiService;
    private final Context context;
    public UsuarioRepository(Context context, ApiService apiService) {
        this.context = context.getApplicationContext(); // para evitar fugas
        this.apiService = apiService;
    }

    public LiveData<List<UsuarioPerfilRecuperado>> buscarPerfiles(String nombreUsuario) {
        MutableLiveData<List<UsuarioPerfilRecuperado>> resultado = new MutableLiveData<>();

        apiService.buscarPerfiles(nombreUsuario).enqueue(new Callback<PerfilResponseList>() {
            @Override
            public void onResponse(Call<PerfilResponseList> call, Response<PerfilResponseList> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    resultado.postValue(response.body().getDatos());
                } else {
                    resultado.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<PerfilResponseList> call, Throwable t) {
                resultado.postValue(new ArrayList<>());
            }
        });

        return resultado;
    }

    public LiveData<UsuarioPerfil> obtenerPerfilPorId(int idUsuario) {
        MutableLiveData<UsuarioPerfil> resultado = new MutableLiveData<>();

        apiService.obtenerPerfilPorId(idUsuario).enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    resultado.postValue(response.body().getDatos());
                } else {
                    resultado.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<PerfilResponse> call, Throwable t) {
                resultado.postValue(null);
            }
        });

        return resultado;
    }

    public LiveData<ApiResponse> seguirUsuario(String token, int idUsuarioSeguido) {
        MutableLiveData<ApiResponse> resultado = new MutableLiveData<>();

        apiService.seguirUsuario("Bearer " + token, new SeguimientoRequest(idUsuarioSeguido))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            resultado.postValue(response.body());
                        } else {
                            resultado.postValue(new ApiResponse(true, "Error al seguir usuario"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        resultado.postValue(new ApiResponse(true, "Error de conexión al seguir usuario"));
                    }
                });

        return resultado;
    }

    public LiveData<ApiResponse> dejarDeSeguirUsuario(String token, int idUsuarioSeguido) {
        MutableLiveData<ApiResponse> resultado = new MutableLiveData<>();
        DejarSeguirRequest request = new DejarSeguirRequest(idUsuarioSeguido);

        apiService.dejarDeSeguirUsuario("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.postValue(response.body());
                } else {
                    resultado.postValue(new ApiResponse(true, "Error al dejar de seguir"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resultado.postValue(new ApiResponse(true, "Error de red: " + t.getMessage()));
            }
        });

        return resultado;
    }


    public LiveData<Boolean> verificarSeguimiento(String token, int idUsuarioSeguido) {
        MutableLiveData<Boolean> resultado = new MutableLiveData<>();

        apiService.verificarSeguimiento("Bearer " + token, idUsuarioSeguido)
                .enqueue(new Callback<VerificacionSeguimientoResponse>() {
                    @Override
                    public void onResponse(Call<VerificacionSeguimientoResponse> call, Response<VerificacionSeguimientoResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                            resultado.postValue(response.body().isSiguiendo());
                        } else {
                            resultado.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<VerificacionSeguimientoResponse> call, Throwable t) {
                        resultado.postValue(false);
                    }
                });

        return resultado;
    }
    // Método para obtener publicaciones por ID de usuario
    public LiveData<PublicacionesResult> obtenerPublicacionesPorUsuario(int idUsuario) {
        MutableLiveData<PublicacionesResult> resultado = new MutableLiveData<>();

        apiService.obtenerPublicacionesPorUsuario(idUsuario)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                            List<DocumentoResponse> publicaciones = response.body().getDatos();
                            resultado.setValue(new PublicacionesResult(
                                    true,
                                    "Se encontraron " + publicaciones.size() + " publicaciones",
                                    publicaciones
                            ));
                            Log.d(TAG, "Publicaciones obtenidas: " + publicaciones.size());
                        } else {
                            String errorMsg = "Error al obtener publicaciones: " + response.code();
                            resultado.setValue(new PublicacionesResult(false, errorMsg, new ArrayList<>()));
                            Log.e(TAG, errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        String errorMsg = "Error de conexión: " + t.getMessage();
                        resultado.setValue(new PublicacionesResult(false, errorMsg, new ArrayList<>()));
                        Log.e(TAG, errorMsg, t);
                    }
                });

        return resultado;
    }

    // Clase para encapsular el resultado de las publicaciones
    public static class PublicacionesResult {
        private final boolean success;
        private final String message;
        private final List<DocumentoResponse> publicaciones;

        public PublicacionesResult(boolean success, String message, List<DocumentoResponse> publicaciones) {
            this.success = success;
            this.message = message;
            this.publicaciones = publicaciones != null ? publicaciones : new ArrayList<>();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<DocumentoResponse> getPublicaciones() {
            return publicaciones;
        }
    }
}

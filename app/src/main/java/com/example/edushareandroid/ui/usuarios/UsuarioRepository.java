package com.example.edushareandroid.ui.usuarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.DejarSeguirRequest;
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

    public UsuarioRepository(ApiService apiService) {
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
}

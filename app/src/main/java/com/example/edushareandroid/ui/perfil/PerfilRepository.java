package com.example.edushareandroid.ui.perfil;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.PerfilResponse;
import com.example.edushareandroid.model.base_de_datos.UsuarioPerfil;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.network.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilRepository {
    private final ApiService usuarioApi;

    public PerfilRepository() {
        usuarioApi = RetrofitClient.getApiService();
    }

    public LiveData<UsuarioPerfil> obtenerPerfil(String token) {
        MutableLiveData<UsuarioPerfil> perfilLiveData = new MutableLiveData<>();

        usuarioApi.obtenerPerfilPropio("Bearer " + token).enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    perfilLiveData.setValue(response.body().getDatos());
                } else {
                    Log.e("Perfil", "Error al obtener perfil: " + response.code());
                    perfilLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PerfilResponse> call, Throwable t) {
                Log.e("Perfil", "Fallo en la solicitud", t);
                perfilLiveData.setValue(null);
            }
        });

        return perfilLiveData;
    }
}

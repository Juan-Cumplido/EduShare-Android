package com.example.edushareandroid.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.CatalogoResponse;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {

    private final ApiService apiService;
    private final String token;
    private final Context context;

    public HomeRepository(Context context, String token) {
        this.context = context.getApplicationContext();
        this.token = "Bearer " + token;
        this.apiService = RetrofitClient.getApiService(context);
    }

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
}

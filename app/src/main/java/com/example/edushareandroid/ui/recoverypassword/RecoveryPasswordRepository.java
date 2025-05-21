package com.example.edushareandroid.ui.recoverypassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.ChangePasswordRequest;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.network.RetrofitClient;
import com.example.edushareandroid.network.ApiResponse;
import com.example.edushareandroid.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryPasswordRepository {
    private final ApiService apiService;

    public RecoveryPasswordRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<ApiResponse> recuperarContrasena(RecoveryRequest request) {
        MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        apiService.recuperarContrasena(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                ApiResponse error = new ApiResponse(true, "Error de conexión");
                data.setValue(error);
            }
        });
        return data;
    }

    public LiveData<ApiResponse> cambiarContrasena(ChangePasswordRequest request) {
        MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        apiService.cambiarContrasena(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                ApiResponse error = new ApiResponse(true, "Error de conexión");
                data.setValue(error);
            }
        });
        return data;
    }
}

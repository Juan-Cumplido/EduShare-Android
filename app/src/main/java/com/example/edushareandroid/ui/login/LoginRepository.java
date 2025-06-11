package com.example.edushareandroid.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.LoginRequest;
import com.example.edushareandroid.model.base_de_datos.LoginResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private final ApiService apiService;

    public LoginRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<LoginResponse> login(LoginRequest request) {
        MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.setValue(response.body());
                } else {
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.setError(true);
                    errorResponse.setEstado(response.code());
                    errorResponse.setMensaje("Error al iniciar sesión: " + response.message());
                    loginResponse.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setError(true);
                errorResponse.setEstado(500);
                errorResponse.setMensaje("Fallo de conexión: " + t.getMessage());
                loginResponse.setValue(errorResponse);
                Log.e("LoginRepository", "Fallo en login: " + t.getMessage(), t);
            }
        });

        return loginResponse;
    }
}

package com.example.edushareandroid.ui.crearcuenta;

import android.util.Log;

import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository {

    public interface RegistroCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    private final ApiService apiService = RetrofitClient.getApiService();

    public void registrarUsuario(UsuarioRegistro usuario, RegistroCallback callback) {
        apiService.registrarUsuario(usuario).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse respuesta = response.body();
                    if (respuesta != null) {
                        Log.d("Registro", "Éxito: " + respuesta.getMensaje());
                    } else {
                        Log.d("Registro", "Éxito sin cuerpo de respuesta");
                    }
                    callback.onSuccess();
                } else {
                    Log.e("Registro", "Error HTTP: " + response.code());
                    String mensajeError;

                    // Manejo específico de códigos HTTP
                    switch (response.code()) {
                        case 400:
                            mensajeError = "Datos inválidos. Revisa los campos ingresados.";
                            break;
                        case 409:
                            mensajeError = "El correo o nombre de usuario ya está en uso.";
                            break;
                        case 500:
                            mensajeError = "Error interno del servidor. Intenta más tarde.";
                            break;
                        default:
                            mensajeError = "Error desconocido. Código: " + response.code();
                            break;
                    }

                    // Intentar leer el cuerpo del error si está disponible
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("Registro", "Cuerpo de error: " + errorBody);
                            mensajeError += "\n" + errorBody;
                        } catch (IOException e) {
                            Log.e("Registro", "Error al leer el cuerpo de error", e);
                        }
                    }

                    callback.onError(mensajeError);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Registro", "Fallo de red: " + t.getMessage(), t);
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }


}

package com.example.edushareandroid.ui.recuperarcontrasenia;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.CambiarContraseniaRequest;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;

import org.json.JSONObject;

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
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse apiResponse = new ApiResponse(true, "Error al recuperar contraseña");

                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            JSONObject jsonError = new JSONObject(errorBodyStr);

                            int estado = jsonError.optInt("estado", response.code());
                            String mensaje = jsonError.optString("mensaje", "Error desconocido");

                            if (jsonError.has("mensaje") && jsonError.get("mensaje") instanceof JSONObject) {
                                JSONObject mensajeObj = jsonError.getJSONObject("mensaje");
                                mensaje = mensajeObj.toString();
                            }

                            apiResponse.setEstado(estado);
                            apiResponse.setMensaje(mensaje);

                            // Manejo detallado por código
                            switch (estado) {
                                case 400:
                                    apiResponse.setErrorDetalle("Correo no válido o faltante");
                                    break;
                                case 404:
                                    apiResponse.setErrorDetalle("El correo no está registrado");
                                    break;
                                case 500:
                                    apiResponse.setErrorDetalle("Error interno del servidor");
                                    break;
                                default:
                                    apiResponse.setErrorDetalle("Error desconocido");
                                    break;
                            }
                        } else {
                            apiResponse.setEstado(response.code());
                            apiResponse.setMensaje("Respuesta sin cuerpo de error");
                            apiResponse.setErrorDetalle("No se recibió detalle del error");
                        }
                    } catch (Exception e) {
                        Log.e("RecoveryRepository", "Error al procesar errorBody", e);
                        apiResponse.setEstado(response.code());
                        apiResponse.setMensaje("Error al interpretar la respuesta del servidor");
                        apiResponse.setErrorDetalle(e.getMessage());
                    }

                    data.setValue(apiResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("RecoveryRepository", "Fallo en recuperarContrasena", t);
                ApiResponse apiResponse = new ApiResponse(true, "Error de conexión");
                apiResponse.setEstado(0);
                apiResponse.setErrorDetalle(t.getMessage());
                data.setValue(apiResponse);
            }
        });

        return data;
    }


    public LiveData<ApiResponse> cambiarContrasena(CambiarContraseniaRequest request) {
        MutableLiveData<ApiResponse> data = new MutableLiveData<>();

        apiService.cambiarContrasena(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse apiResponse = new ApiResponse(true, "Error al cambiar contraseña");

                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            JSONObject jsonError = new JSONObject(errorBodyStr);

                            int estado = jsonError.optInt("estado", response.code());
                            String mensaje = jsonError.optString("mensaje", "Error al cambiar contraseña");

                            // Si el mensaje es un objeto (como errores por campo), lo convertimos a String
                            if (jsonError.has("mensaje") && jsonError.get("mensaje") instanceof JSONObject) {
                                JSONObject mensajeObj = jsonError.getJSONObject("mensaje");
                                mensaje = mensajeObj.toString(); // puedes formatear mejor si quieres
                            }

                            apiResponse.setEstado(estado);
                            apiResponse.setMensaje(mensaje);

                            // Puedes usar un switch para mensajes por código si lo deseas:
                            switch (estado) {
                                case 400:
                                    apiResponse.setErrorDetalle("Solicitud inválida");
                                    break;
                                case 401:
                                    apiResponse.setErrorDetalle("Código expirado o inválido");
                                    break;
                                case 404:
                                    apiResponse.setErrorDetalle("Correo no tiene solicitud de recuperación");
                                    break;
                                case 500:
                                    apiResponse.setErrorDetalle("Error interno del servidor");
                                    break;
                                default:
                                    apiResponse.setErrorDetalle("Error desconocido");
                                    break;
                            }

                        } else {
                            apiResponse.setEstado(response.code());
                            apiResponse.setMensaje("Error sin cuerpo");
                            apiResponse.setErrorDetalle("No se recibió detalle del error");
                        }
                    } catch (Exception e) {
                        Log.e("RecoveryRepository", "Error al procesar errorBody", e);
                        apiResponse.setEstado(response.code());
                        apiResponse.setMensaje("Error al interpretar la respuesta del servidor");
                        apiResponse.setErrorDetalle(e.getMessage());
                    }

                    data.setValue(apiResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("RecoveryRepository", "Fallo en cambiarContrasena", t);
                ApiResponse apiResponse = new ApiResponse(true, "Error de conexión");
                apiResponse.setEstado(0);
                apiResponse.setErrorDetalle(t.getMessage());
                data.setValue(apiResponse);
            }
        });

        return data;
    }

}

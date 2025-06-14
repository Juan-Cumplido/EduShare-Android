package com.example.edushareandroid.ui.verarchivo;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.edushareandroid.utils.SesionUsuario.obtenerToken;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.model.base_de_datos.comentarios.CrearComentarioRequest;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaBase;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaConDatos;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerArchivoRepository {
    private static final String TAG = "VerArchivoRepository";

    private ApiService apiService;

    public VerArchivoRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<RespuestaBase> crearComentario(Context context, String contenido, int idPublicacion) {
        MutableLiveData<RespuestaBase> liveData = new MutableLiveData<>();

        if (contenido == null || contenido.trim().isEmpty()) {
            RespuestaBase respuestaError = new RespuestaBase();
            respuestaError.setEstado(400);
            respuestaError.setError(true);
            respuestaError.setMensaje("Comentario vacío");
            liveData.postValue(respuestaError);
            return liveData;
        }

        CrearComentarioRequest request = new CrearComentarioRequest(contenido, idPublicacion);
        String token = "Bearer " + SesionUsuario.obtenerToken(context); // ✅ Ahora correctamente
        apiService.crearComentario(request, token).enqueue(new Callback<RespuestaBase>() {
            @Override
            public void onResponse(Call<RespuestaBase> call, Response<RespuestaBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    RespuestaBase respuestaError = new RespuestaBase();
                    respuestaError.setEstado(response.code());
                    respuestaError.setError(true);
                    respuestaError.setMensaje("Error al crear comentario");
                    liveData.postValue(respuestaError);
                }
            }

            @Override
            public void onFailure(Call<RespuestaBase> call, Throwable t) {
                RespuestaBase respuestaError = new RespuestaBase();
                respuestaError.setEstado(0);
                respuestaError.setError(true);
                respuestaError.setMensaje("Fallo de red al crear comentario");
                liveData.postValue(respuestaError);
            }
        });

        return liveData;
    }


    public LiveData<RespuestaBase> eliminarComentario(Context context, int idComentario) {
        MutableLiveData<RespuestaBase> liveData = new MutableLiveData<>();

        String token = "Bearer " + SesionUsuario.obtenerToken(context);
        apiService.eliminarComentario(idComentario, token).enqueue(new Callback<RespuestaBase>() {
            @Override
            public void onResponse(Call<RespuestaBase> call, Response<RespuestaBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    RespuestaBase error = new RespuestaBase();
                    error.setEstado(response.code());
                    error.setError(true);
                    error.setMensaje("Error al eliminar el comentario");
                    liveData.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<RespuestaBase> call, Throwable t) {
                RespuestaBase error = new RespuestaBase();
                error.setEstado(0);
                error.setError(true);
                error.setMensaje("Error de red al eliminar comentario");
                liveData.postValue(error);
            }
        });

        return liveData;
    }


    public LiveData<List<Comentario>> obtenerComentarios(int idPublicacion) {
        MutableLiveData<List<Comentario>> liveData = new MutableLiveData<>();

        apiService.obtenerComentarios(idPublicacion).enqueue(new Callback<RespuestaConDatos<List<Comentario>>>() {
            @Override
            public void onResponse(Call<RespuestaConDatos<List<Comentario>>> call, Response<RespuestaConDatos<List<Comentario>>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // Cambiar getResultado() por getEstado() acorde a tu RespuestaBase actualizada
                        if (response.body().getEstado() == 200) {
                            Log.i(TAG, "obtenerComentarios éxito: " + response.body().getDatos().size() + " comentarios");
                            liveData.postValue(response.body().getDatos());
                        } else {
                            Log.w(TAG, "obtenerComentarios resultado no OK: " + response.body().getEstado());
                            liveData.postValue(Collections.emptyList());
                        }
                    } else {
                        Log.w(TAG, "obtenerComentarios cuerpo vacío");
                        liveData.postValue(Collections.emptyList());
                    }
                } else {
                    Log.e(TAG, "obtenerComentarios fallo HTTP: Código " + response.code() + " - " + response.message());
                    liveData.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<RespuestaConDatos<List<Comentario>>> call, Throwable t) {
                Log.e(TAG, "obtenerComentarios error de red: ", t);
                liveData.postValue(Collections.emptyList());
            }
        });

        return liveData;
    }
}

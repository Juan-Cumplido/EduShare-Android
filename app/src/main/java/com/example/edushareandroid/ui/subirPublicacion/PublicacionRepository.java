package com.example.edushareandroid.ui.subirPublicacion;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.CatalogoResponse;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.DocumentoRequest;
import com.example.edushareandroid.model.base_de_datos.DocumentoSubidoResponse;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.PublicacionRequest;
import com.example.edushareandroid.model.base_de_datos.PublicacionResponse;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacionRepository {

    private final ApiService apiService;
    private final String token;
    private final Context context;


    public PublicacionRepository(Context context, String token) {
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

    public LiveData<Resource<Integer>> crearDocumento(DocumentoRequest request) {
        MutableLiveData<Resource<Integer>> resultado = new MutableLiveData<>();
        resultado.setValue(Resource.loading());

        apiService.crearDocumento(request, token).enqueue(new Callback<DocumentoSubidoResponse>() {
            @Override
            public void onResponse(Call<DocumentoSubidoResponse> call, Response<DocumentoSubidoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DocumentoSubidoResponse res = response.body();
                    if (!res.isError()) {
                        if (res.getEstado() == 200 || res.getEstado() == 201) {
                            resultado.setValue(Resource.success(res.getId(), res.getEstado()));
                        } else {
                            resultado.setValue(Resource.error(res.getMensaje(), res.getEstado()));
                        }
                    } else {
                        resultado.setValue(Resource.error(res.getMensaje(), res.getEstado()));
                    }
                } else {
                    resultado.setValue(Resource.error("Error en la respuesta del servidor", response.code()));
                }
            }

            @Override
            public void onFailure(Call<DocumentoSubidoResponse> call, Throwable t) {
                resultado.setValue(Resource.error("Error de red: " + t.getMessage(), 0));
            }
        });

        return resultado;
    }

    public LiveData<Resource<Integer>> crearPublicacion(PublicacionRequest request) {
        MutableLiveData<Resource<Integer>> resultado = new MutableLiveData<>();
        resultado.setValue(Resource.loading());

        apiService.crearPublicacion(request, token).enqueue(new Callback<PublicacionResponse>() {
            @Override
            public void onResponse(Call<PublicacionResponse> call, Response<PublicacionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PublicacionResponse res = response.body();

                    if (!res.isError()) {
                        if (res.getEstado() == 200 || res.getEstado() == 201) {
                            resultado.setValue(Resource.success(res.getId(), res.getEstado()));
                        } else {
                            resultado.setValue(Resource.error(res.getMensaje(), res.getEstado()));
                        }
                    } else {
                        resultado.setValue(Resource.error(res.getMensaje(), res.getEstado()));
                    }
                } else {
                    resultado.setValue(Resource.error("Error desconocido al crear publicación", response.code()));
                }
            }

            @Override
            public void onFailure(Call<PublicacionResponse> call, Throwable t) {
                resultado.setValue(Resource.error("Error de red: " + t.getMessage(), 0));
            }
        });

        return resultado;
    }

}

package com.example.edushareandroid.ui.crearcuenta;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.model.base_de_datos.InstitucionesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountViewModel extends ViewModel {

    private final MutableLiveData<List<Institucion>> instituciones = new MutableLiveData<>();
    private final MutableLiveData<String> rutaImagen = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resultadoRegistro = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final UsuarioRepository repository = new UsuarioRepository();
    private final ApiService apiService = RetrofitClient.getApiService();
    private final FileServiceClient fileServiceClient = new FileServiceClient();

    public LiveData<List<Institucion>> getInstituciones() {
        return instituciones;
    }

    public LiveData<String> getRutaImagen() {
        return rutaImagen;
    }

    public LiveData<Boolean> getResultadoRegistro() {
        return resultadoRegistro;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarInstituciones(String nivelEducativo) {
        apiService.obtenerInstituciones(nivelEducativo).enqueue(new Callback<InstitucionesResponse>() {
            @Override
            public void onResponse(Call<InstitucionesResponse> call, Response<InstitucionesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    instituciones.postValue(response.body().getDatos());
                    Log.d("Instituciones", "Cargadas: " + response.body().getDatos().size());
                } else {
                    String mensaje = "Error al cargar instituciones: código " + response.code();
                    error.postValue(mensaje);
                    Log.e("Instituciones", mensaje);
                }
            }

            @Override
            public void onFailure(Call<InstitucionesResponse> call, Throwable t) {
                String mensaje = "Error de red: " + t.getMessage();
                error.postValue(mensaje);
                Log.e("Instituciones", mensaje);
            }
        });
    }



    public int obtenerIdInstitucion(String nombreInstitucion) {
        List<Institucion> lista = instituciones.getValue();
        if (lista != null) {
            for (Institucion inst : lista) {
                if (inst.getNombreInstitucion().equals(nombreInstitucion)) {
                    return inst.getIdInstitucion(); // Suponiendo que tienes un metodo getId()
                }
            }
        }
        return -1; // o algún valor que indique que no se encontró
    }

    public void registrarUsuario(UsuarioRegistro usuario) {
        repository.registrarUsuario(usuario, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                resultadoRegistro.postValue(true);
            }

            @Override
            public void onError(String mensaje) {
                error.postValue(mensaje);
            }
        });
    }
}
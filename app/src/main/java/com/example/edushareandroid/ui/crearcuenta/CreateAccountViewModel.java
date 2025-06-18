package com.example.edushareandroid.ui.crearcuenta;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;

import java.util.List;

public class CreateAccountViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Institucion>> instituciones = new MutableLiveData<>();
    private final MutableLiveData<String> rutaImagen = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resultadoRegistro = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final UsuarioRepository repository;

    public CreateAccountViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UsuarioRepository(application.getApplicationContext());
    }

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
        repository.cargarInstituciones(nivelEducativo, new UsuarioRepository.InstitucionesCallback() {
            @Override
            public void onSuccess(List<Institucion> institucionesList) {
                instituciones.postValue(institucionesList);
                Log.d("Instituciones", "Cargadas: " + institucionesList.size());
            }

            @Override
            public void onError(String mensaje) {
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
                    return inst.getIdInstitucion();
                }
            }
        }
        return -1;
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
package com.example.edushareandroid.ui.usuarios;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class UsuariosViewModel extends AndroidViewModel {
    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<UsuarioPerfil> perfilUsuario = new MutableLiveData<>();
    private final MutableLiveData<List<UsuarioPerfilRecuperado>> perfilesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<UsuarioPerfilRecuperado> perfilSeleccionado = new MutableLiveData<>();
    private final MutableLiveData<List<DocumentoResponse>> publicacionesPorUsuario = new MutableLiveData<>();
    private final MutableLiveData<String> mensajePublicaciones = new MutableLiveData<>();

    private final MutableLiveData<ApiResponse> respuestaSeguimiento = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> respuestaDejarSeguimiento = new MutableLiveData<>();
    private final MutableLiveData<Boolean> estaSiguiendo = new MutableLiveData<>();

    public UsuariosViewModel(@NonNull Application application) {
        super(application);
        Context appContext = application.getApplicationContext();

        ApiService apiService = RetrofitClient.getApiService(appContext);
        usuarioRepository = new UsuarioRepository(appContext, apiService);
    }

    public LiveData<List<DocumentoResponse>> getPublicacionesPorUsuario() {
        return publicacionesPorUsuario;
    }

    public LiveData<String> getMensajePublicaciones() {
        return mensajePublicaciones;
    }


    public LiveData<List<UsuarioPerfilRecuperado>> getPerfiles() {
        return perfilesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void buscarPerfiles(String textoBusqueda) {
        usuarioRepository.buscarPerfiles(textoBusqueda).observeForever(perfiles -> {
            if (perfiles != null && !perfiles.isEmpty()) {
                perfilesLiveData.postValue(perfiles);
                errorLiveData.postValue(null);
            } else {
                perfilesLiveData.postValue(new ArrayList<>());
                errorLiveData.postValue("No se encontraron resultados");
            }
        });
    }

    public LiveData<UsuarioPerfil> getPerfilUsuario() {
        return perfilUsuario;
    }

    public void cargarPerfilPorId(int idUsuario) {
        usuarioRepository.obtenerPerfilPorId(idUsuario).observeForever(perfil -> {
            perfilUsuario.postValue(perfil);
        });
    }

    public void cargarPublicacionesPorUsuario(int idUsuario) {
        usuarioRepository.obtenerPublicacionesPorUsuario(idUsuario).observeForever(resultado -> {
            if (resultado != null && resultado.isSuccess()) {
                publicacionesPorUsuario.postValue(resultado.getPublicaciones());
                mensajePublicaciones.postValue(resultado.getMessage());
            } else {
                publicacionesPorUsuario.postValue(new ArrayList<>());
                mensajePublicaciones.postValue(resultado != null ? resultado.getMessage() : "Error desconocido");
            }
        });
    }


    public LiveData<ApiResponse> getRespuestaSeguimiento() {
        return respuestaSeguimiento;
    }

    public LiveData<ApiResponse> getRespuestaDejarSeguimiento() {
        return respuestaDejarSeguimiento;
    }

    public LiveData<Boolean> getEstaSiguiendo() {
        return estaSiguiendo;
    }

    public void seguirUsuario(String token, int idUsuario) {
        usuarioRepository.seguirUsuario(token, idUsuario).observeForever(respuesta -> {
            respuestaSeguimiento.postValue(respuesta);
        });
    }

    public void dejarDeSeguirUsuario(String token, int idUsuario) {
        usuarioRepository.dejarDeSeguirUsuario(token, idUsuario).observeForever(respuesta -> {
            respuestaDejarSeguimiento.postValue(respuesta);
        });
    }

    public void verificarSeguimiento(String token, int idUsuario) {
        usuarioRepository.verificarSeguimiento(token, idUsuario).observeForever(estado -> {
            estaSiguiendo.postValue(estado);
        });
    }

}

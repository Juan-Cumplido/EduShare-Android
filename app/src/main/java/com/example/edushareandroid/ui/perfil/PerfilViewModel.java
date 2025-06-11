package com.example.edushareandroid.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.UsuarioPerfil;

public class PerfilViewModel extends ViewModel {
    private final PerfilRepository usuarioRepository = new PerfilRepository();
    private final MutableLiveData<UsuarioPerfil> perfilLiveData = new MutableLiveData<>();

    public void cargarPerfil(String token) {
        usuarioRepository.obtenerPerfil(token).observeForever(perfil -> {
            perfilLiveData.setValue(perfil);
        });
    }

    public LiveData<UsuarioPerfil> getPerfilLiveData() {
        return perfilLiveData;
    }
}

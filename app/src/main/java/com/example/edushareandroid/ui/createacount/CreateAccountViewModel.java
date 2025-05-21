package com.example.edushareandroid.ui.createacount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;

public class CreateAccountViewModel extends ViewModel {

    private final UsuarioRepository repository = new UsuarioRepository();

    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<Boolean> getRegistroExitoso() {
        return registroExitoso;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void registrarUsuario(UsuarioRegistro usuario) {
        repository.registrarUsuario(usuario, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                registroExitoso.postValue(true);
            }

            @Override
            public void onError(String mensaje) {
                error.postValue(mensaje);
            }
        });
    }
}

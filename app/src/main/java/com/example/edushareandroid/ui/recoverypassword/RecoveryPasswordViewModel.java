package com.example.edushareandroid.ui.recoverypassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.ChangePasswordRequest;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.network.ApiResponse;

public class RecoveryPasswordViewModel extends ViewModel {
    private final RecoveryPasswordRepository repository = new RecoveryPasswordRepository();

    public LiveData<ApiResponse> recuperarContrasena(String correo) {
        return repository.recuperarContrasena(new RecoveryRequest(correo));
    }

    public LiveData<ApiResponse> cambiarContrasena(String correo, String codigo, String nuevaContra) {
        return repository.cambiarContrasena(new ChangePasswordRequest(correo, codigo, nuevaContra));
    }
}


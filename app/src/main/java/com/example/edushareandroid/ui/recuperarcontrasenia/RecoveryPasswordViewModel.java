package com.example.edushareandroid.ui.recuperarcontrasenia;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.CambiarContraseniaRequest;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.network.api.ApiResponse;

public class RecoveryPasswordViewModel extends ViewModel {

    private final RecoveryPasswordRepository repository = new RecoveryPasswordRepository();

    private final MutableLiveData<ApiResponse> recuperarContrasenaResponse = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> cambiarContrasenaResponse = new MutableLiveData<>();

    public LiveData<ApiResponse> getRecuperarContrasenaResponse() {
        return recuperarContrasenaResponse;
    }

    public LiveData<ApiResponse> getCambiarContrasenaResponse() {
        return cambiarContrasenaResponse;
    }

    public void recuperarContrasena(String correo) {
        repository.recuperarContrasena(new RecoveryRequest(correo))
                .observeForever(response -> recuperarContrasenaResponse.postValue(response));
    }

    public void cambiarContrasena(String correo, String codigo, String nuevaContra) {
        CambiarContraseniaRequest request = new CambiarContraseniaRequest(correo, codigo, nuevaContra);
        repository.cambiarContrasena(request)
                .observeForever(response -> cambiarContrasenaResponse.postValue(response));
    }
}

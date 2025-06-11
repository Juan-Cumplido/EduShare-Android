package com.example.edushareandroid.ui.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.LoginRequest;
import com.example.edushareandroid.model.base_de_datos.LoginResponse;
import com.example.edushareandroid.utils.SesionUsuario;

public class LoginViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel() {
        loginRepository = new LoginRepository();
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String identifier, String passwordHasheado) {
        isLoading.setValue(true);

        LoginRequest request = new LoginRequest(identifier, passwordHasheado);

        loginRepository.login(request).observeForever(response -> {
            isLoading.setValue(false);
            if (!response.isError()) {
                loginResult.setValue(response);
            } else {
                errorMessage.setValue(response.getMensaje());
            }
        });
    }
    public void manejarRespuestaLogin(LoginResponse response, Context context) {
        if (!response.isError()) {
            SesionUsuario.guardarEstadoLogueado(context, true);
            SesionUsuario.guardarToken(context, response.getToken());
        }
    }
}

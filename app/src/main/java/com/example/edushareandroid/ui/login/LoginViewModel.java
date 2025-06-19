package com.example.edushareandroid.ui.login;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.utils.SesionUsuario;

public class LoginViewModel extends AndroidViewModel {

    private final LoginRepository loginRepository;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new LoginRepository(application.getApplicationContext());
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

            if (response.getDatos() != null) {
                SesionUsuario.guardarDatosUsuario(context, response.getDatos());
            }
        }
    }
}

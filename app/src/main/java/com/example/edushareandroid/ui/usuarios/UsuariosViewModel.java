package com.example.edushareandroid.ui.usuarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsuariosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UsuariosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Por favor inicie sesión");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
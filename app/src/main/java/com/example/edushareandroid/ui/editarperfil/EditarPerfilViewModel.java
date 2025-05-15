package com.example.edushareandroid.ui.editarperfil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class EditarPerfilViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public EditarPerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Por favor inicie sesión");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

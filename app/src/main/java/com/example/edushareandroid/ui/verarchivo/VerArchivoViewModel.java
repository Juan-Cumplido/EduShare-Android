package com.example.edushareandroid.ui.verarchivo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.databinding.FragmentEditarperfilBinding;

public class VerArchivoViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public VerArchivoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Por favor inicie sesión");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

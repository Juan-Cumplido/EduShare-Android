package com.example.edushareandroid.ui.vistachat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VistaChatViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public VistaChatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Por favor inicie sesión");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
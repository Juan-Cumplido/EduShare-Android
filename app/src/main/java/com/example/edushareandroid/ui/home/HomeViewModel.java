package com.example.edushareandroid.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepository repository;
    private final MutableLiveData<List<Categoria>> categorias = new MutableLiveData<>();
    private final MutableLiveData<List<Rama>> ramas = new MutableLiveData<>();
    private final MutableLiveData<List<Materia>> materias = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        String token = SesionUsuario.obtenerToken(application.getApplicationContext());
        repository = new HomeRepository(application.getApplicationContext(), token);
    }
    public LiveData<List<Categoria>> getCategorias() {
        return categorias;
    }

    public LiveData<List<Rama>> getRamas() {
        return ramas;
    }

    public LiveData<List<Materia>> getMaterias() {
        return materias;
    }

    public void cargarCategorias() {
        repository.obtenerCategorias().observeForever(categorias::setValue);
    }

    public void cargarRamas() {
        repository.obtenerRamas().observeForever(ramas::setValue);
    }

    public void cargarMateriasPorRama(int idRama) {
        repository.obtenerMateriasPorRama(idRama).observeForever(materias::setValue);
    }
}
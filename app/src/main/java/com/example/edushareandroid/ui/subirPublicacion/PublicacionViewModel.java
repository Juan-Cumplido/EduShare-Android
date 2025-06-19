package com.example.edushareandroid.ui.subirPublicacion;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.DocumentoRequest;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.PublicacionRequest;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.utils.Resource;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.List;

public class PublicacionViewModel extends AndroidViewModel {

    private final PublicacionRepository repository;

    private final MutableLiveData<List<Categoria>> categorias = new MutableLiveData<>();
    private final MutableLiveData<List<Rama>> ramas = new MutableLiveData<>();
    private final MutableLiveData<List<Materia>> materias = new MutableLiveData<>();

    public PublicacionViewModel(@NonNull Application application) {
        super(application);
        String token = SesionUsuario.obtenerToken(application.getApplicationContext());
        repository = new PublicacionRepository(application.getApplicationContext(), token);
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

    public LiveData<Resource<Integer>> crearDocumento(DocumentoRequest request) {
        return repository.crearDocumento(request);
    }

    public LiveData<Resource<Integer>> crearPublicacion(PublicacionRequest request) {
        return repository.crearPublicacion(request);
    }
}

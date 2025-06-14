package com.example.edushareandroid.ui.subirPublicacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.DocumentoRequest;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.PublicacionRequest;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.utils.Resource;

import java.util.List;

public class PublicacionViewModel extends ViewModel {

    private PublicacionRepository repository;

    private final MutableLiveData<List<Categoria>> categorias = new MutableLiveData<>();
    private final MutableLiveData<List<Rama>> ramas = new MutableLiveData<>();
    private final MutableLiveData<List<Materia>> materias = new MutableLiveData<>();

    public void setToken(String token) {
        ApiService apiService = RetrofitClient.getApiService();
        this.repository = new PublicacionRepository(apiService, token);
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
        if (repository != null)
            repository.obtenerCategorias().observeForever(categorias::setValue);
    }

    public void cargarRamas() {
        if (repository != null)
            repository.obtenerRamas().observeForever(ramas::setValue);
    }

    public void cargarMateriasPorRama(int idRama) {
        if (repository != null)
            repository.obtenerMateriasPorRama(idRama).observeForever(materias::setValue);
    }

    public LiveData<Resource<Integer>> crearDocumento(DocumentoRequest request) {
        if (repository != null)
            return repository.crearDocumento(request);
        else
            return new MutableLiveData<>(Resource.error("Token no inicializado", 0));
    }

    public LiveData<Resource<Integer>> crearPublicacion(PublicacionRequest request) {
        if (repository != null)
            return repository.crearPublicacion(request);
        else
            return new MutableLiveData<>(Resource.error("Token no inicializado", 0));
    }
}

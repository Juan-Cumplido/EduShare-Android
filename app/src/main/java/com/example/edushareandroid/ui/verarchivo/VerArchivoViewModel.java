package com.example.edushareandroid.ui.verarchivo;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaBase;

import java.util.List;

public class VerArchivoViewModel extends ViewModel {
    private final VerArchivoRepository repository;
    private final MutableLiveData<List<Comentario>> comentarios = new MutableLiveData<>();

    public VerArchivoViewModel() {
        this.repository = new VerArchivoRepository();
    }

    public LiveData<List<Comentario>> getComentarios() {
        return comentarios;
    }

    public void cargarComentarios(int idPublicacion) {
        repository.obtenerComentarios(idPublicacion).observeForever(comentarios::setValue);
    }

    public LiveData<RespuestaBase> crearComentario(Context context, String contenido, int idPublicacion) {
        return repository.crearComentario(context, contenido, idPublicacion);
    }

    public LiveData<RespuestaBase> eliminarComentario(Context context, int idComentario) {
        return repository.eliminarComentario(context, idComentario);
    }

}

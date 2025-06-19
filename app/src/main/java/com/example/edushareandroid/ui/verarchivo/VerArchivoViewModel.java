package com.example.edushareandroid.ui.verarchivo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.model.base_de_datos.comentarios.RespuestaBase;
import com.example.edushareandroid.network.api.ApiResponse;

import java.util.List;

public class VerArchivoViewModel extends AndroidViewModel {
    private final VerArchivoRepository repository;
    private final MutableLiveData<List<Comentario>> comentarios = new MutableLiveData<>();
    private final Context context;

    public VerArchivoViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        this.repository = new VerArchivoRepository(context);
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

    public LiveData<ApiResponse> darLike(Context context, int idPublicacion) {
        return repository.darLike(idPublicacion, context);
    }

    public LiveData<ApiResponse> quitarLike(Context context, int idPublicacion) {
        return repository.quitarLike(idPublicacion, context);
    }

    public LiveData<ApiResponse> verificarLike(Context context, int idPublicacion) {
        return repository.verificarLike(idPublicacion, context);
    }

    public LiveData<ApiResponse> registrarVisualizacion(int idPublicacion) {
        return repository.registrarVisualizacion(idPublicacion);
    }

    public LiveData<ApiResponse> registrarDescarga(Context context, int idPublicacion) {
        return repository.registrarDescarga(idPublicacion, context);
    }
}

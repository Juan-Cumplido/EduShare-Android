package com.example.edushareandroid.ui.perfil;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class PerfilViewModel extends ViewModel {
    private final PerfilRepository perfilRepository;

    // LiveData existentes
    private final MutableLiveData<UsuarioPerfil> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> imagenPerfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<DocumentoResponse>> publicacionesLiveData = new MutableLiveData<>(); // Corregido el error de sintaxis

    // LiveData para manejar la eliminación
    private final MutableLiveData<String> mensajeEliminacionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> eliminacionExitosaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mostrandoDialogoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoEliminacionLiveData = new MutableLiveData<>();

    public PerfilViewModel() {
        perfilRepository = new PerfilRepository();
    }

    // Getters existentes
    public LiveData<Bitmap> getImagenPerfilLiveData() {
        return imagenPerfilLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<UsuarioPerfil> getPerfilLiveData() {
        return perfilLiveData;
    }

    public LiveData<List<DocumentoResponse>> getPublicaciones() {
        return publicacionesLiveData;
    }

    public LiveData<List<DocumentoResponse>> getPublicacionesLiveData() {
        return publicacionesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    // Getters para la eliminación
    public LiveData<String> getMensajeEliminacionLiveData() {
        return mensajeEliminacionLiveData;
    }

    public LiveData<Boolean> getEliminacionExitosaLiveData() {
        return eliminacionExitosaLiveData;
    }

    public LiveData<Boolean> getMostrandoDialogoLiveData() {
        return mostrandoDialogoLiveData;
    }

    public LiveData<Boolean> getCargandoEliminacionLiveData() {
        return cargandoEliminacionLiveData;
    }

    // Métodos existentes
    public void cargarPerfil(String token) {
        Log.d("PerfilVM", "Cargando perfil...");
        perfilRepository.obtenerPerfil(token).observeForever(perfil -> {
            Log.d("PerfilVM", "Perfil recibido: " + (perfil != null ? perfil.getNombre() : "null"));
            perfilLiveData.setValue(perfil);
            if (perfil != null && perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
                descargarImagenPerfil();
            }
        });
    }

    public void descargarImagenPerfil() {
        UsuarioPerfil perfil = perfilLiveData.getValue();
        if (perfil == null || perfil.getFotoPerfil() == null || perfil.getFotoPerfil().isEmpty()) {
            Log.e("PerfilVM", "No se encontró la ruta de imagen para descargar.");
            errorLiveData.setValue("No se encontró la ruta de la imagen.");
            return;
        }

        String ruta = perfil.getFotoPerfil();
        Log.d("PerfilVM", "Descargando imagen desde: " + ruta);

        FileServiceClient client = new FileServiceClient();
        client.downloadImage(ruta, new FileServiceClient.DownloadCallback() {
            @Override
            public void onSuccess(byte[] fileData, String filename) {
                Log.d("PerfilVM", "Imagen de perfil descargada con éxito.");
                Bitmap bitmap = ImageUtil.binaryToBitmap(fileData);
                imagenPerfilLiveData.postValue(bitmap);
                client.shutdown();
            }

            @Override
            public void onError(Exception e) {
                Log.e("PerfilVM", "Error al descargar imagen: " + e.getMessage());
                errorLiveData.postValue("Error al descargar imagen: " + e.getMessage());
                client.shutdown();
            }
        });
    }

    public void cargarPublicacionesUsuario(String token) {
        Log.d("Publicaciones", "Solicitando publicaciones del usuario...");
        perfilRepository.obtenerPublicacionesUsuario(token).observeForever(publicaciones -> {
            if (publicaciones != null) {
                Log.d("Publicaciones", "Se recuperaron " + publicaciones.size() + " publicaciones");
            } else {
                Log.d("Publicaciones", "La lista de publicaciones es null");
            }

            if (publicaciones != null && !publicaciones.isEmpty()) {
                publicacionesLiveData.postValue(publicaciones);
                errorLiveData.postValue(null);
            } else {
                publicacionesLiveData.postValue(new ArrayList<>());
                errorLiveData.postValue("Aún no has subido publicaciones");
            }
        });
    }

    // Métodos para manejar la eliminación
    public void solicitarEliminarPublicacion(int idPublicacion, String tituloPublicacion) {
        // Mostrar el diálogo de confirmación
        mostrandoDialogoLiveData.setValue(true);
        Log.d("PerfilVM", "Solicitando eliminación de publicación: " + tituloPublicacion + " (ID: " + idPublicacion + ")");
    }

    public void confirmarEliminacionPublicacion(int idPublicacion, String token) {
        Log.d("PerfilVM", "Confirmando eliminación de publicación con ID: " + idPublicacion);

        // Mostrar indicador de carga
        cargandoEliminacionLiveData.setValue(true);
        mostrandoDialogoLiveData.setValue(false);

        perfilRepository.eliminarPublicacion(idPublicacion, token).observeForever(resultado -> {
            cargandoEliminacionLiveData.setValue(false);

            if (resultado != null) {
                if (resultado.isExitoso()) {
                    // Eliminación exitosa
                    Log.d("PerfilVM", "Publicación eliminada exitosamente");
                    mensajeEliminacionLiveData.setValue(resultado.getMensaje());
                    eliminacionExitosaLiveData.setValue(true);

                    // Remover la publicación de la lista local
                    eliminarPublicacionDeLista(idPublicacion);

                } else {
                    // Error en la eliminación
                    Log.e("PerfilVM", "Error al eliminar publicación: " + resultado.getMensaje());
                    mensajeEliminacionLiveData.setValue(resultado.getMensaje());
                    eliminacionExitosaLiveData.setValue(false);
                }
            } else {
                // Error inesperado
                Log.e("PerfilVM", "Resultado de eliminación es null");
                mensajeEliminacionLiveData.setValue("Error inesperado al eliminar la publicación");
                eliminacionExitosaLiveData.setValue(false);
            }
        });
    }

    public void cancelarEliminacion() {
        mostrandoDialogoLiveData.setValue(false);
        Log.d("PerfilVM", "Eliminación cancelada por el usuario");
    }

    private void eliminarPublicacionDeLista(int idPublicacion) {
        List<DocumentoResponse> publicacionesActuales = publicacionesLiveData.getValue();
        if (publicacionesActuales != null) {
            List<DocumentoResponse> publicacionesActualizadas = new ArrayList<>(publicacionesActuales);
            publicacionesActualizadas.removeIf(publicacion ->
                    publicacion.getIdPublicacion() == idPublicacion);
            publicacionesLiveData.setValue(publicacionesActualizadas);

            Log.d("PerfilVM", "Publicación con ID " + idPublicacion + " removida de la lista local");

            // Si ya no hay publicaciones, mostrar mensaje apropiado
            if (publicacionesActualizadas.isEmpty()) {
                errorLiveData.setValue("Aún no has subido publicaciones");
            }
        }
    }

    public void limpiarMensajesEliminacion() {
        mensajeEliminacionLiveData.setValue(null);
        eliminacionExitosaLiveData.setValue(null);
        cargandoEliminacionLiveData.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("PerfilVM", "ViewModel destruido");
    }
}
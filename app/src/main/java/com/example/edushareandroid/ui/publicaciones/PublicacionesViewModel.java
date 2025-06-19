package com.example.edushareandroid.ui.publicaciones;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.edushareandroid.ui.perfil.DocumentoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PublicacionesViewModel extends AndroidViewModel {
    private static final String TAG = "PublicacionesVM";

    private final PublicacionesRepository publicacionesRepository;
    private final AtomicReference<UUID> currentRequestId = new AtomicReference<>();

    private final MutableLiveData<List<DocumentoResponse>> publicacionesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> mensajeEliminacionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> eliminacionExitosaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoEliminacionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mostrandoDialogoLiveData = new MutableLiveData<>();
    private int publicacionAEliminarId = -1;

    private PublicacionesRepository.ParametrosConsulta parametrosActuales;

    private Observer<PublicacionesRepository.ResultadoPublicaciones> currentObserver;

    public PublicacionesViewModel(@NonNull Application application) {
        super(application);
        this.publicacionesRepository = new PublicacionesRepository(application.getApplicationContext());
        inicializarEstado();
    }

    private void inicializarEstado() {
        publicacionesLiveData.setValue(new ArrayList<>());
        cargandoLiveData.setValue(false);
        errorLiveData.setValue(false);
        cargandoEliminacionLiveData.setValue(false);
        mostrandoDialogoLiveData.setValue(false);
        mensajeLiveData.setValue(null);
        mensajeEliminacionLiveData.setValue(null);
        eliminacionExitosaLiveData.setValue(null);
    }

    public LiveData<List<DocumentoResponse>> getPublicacionesLiveData() {
        return publicacionesLiveData;
    }

    public LiveData<String> getMensajeLiveData() {
        return mensajeLiveData;
    }

    public LiveData<Boolean> getCargandoLiveData() {
        return cargandoLiveData;
    }

    public LiveData<Boolean> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<String> getMensajeEliminacionLiveData() {
        return mensajeEliminacionLiveData;
    }

    public LiveData<Boolean> getEliminacionExitosaLiveData() {
        return eliminacionExitosaLiveData;
    }

    public LiveData<Boolean> getCargandoEliminacionLiveData() {
        return cargandoEliminacionLiveData;
    }

    public LiveData<Boolean> getMostrandoDialogoLiveData() {
        return mostrandoDialogoLiveData;
    }

    public void limpiarEstadoParaNuevaConsulta() {
        Log.d(TAG, "Limpiando estado para nueva consulta");

        if (currentObserver != null) {
            try {
                LiveData<PublicacionesRepository.ResultadoPublicaciones> previousLiveData =
                        publicacionesRepository.obtenerPublicaciones(parametrosActuales);
                if (previousLiveData != null) {
                    previousLiveData.removeObserver(currentObserver);
                }
            } catch (Exception e) {
                Log.w(TAG, "Error al remover observador anterior: " + e.getMessage());
            }
            currentObserver = null;
        }

        currentRequestId.set(null);
        publicacionesLiveData.setValue(new ArrayList<>());
        mensajeLiveData.setValue(null);
        errorLiveData.setValue(false);
        cargandoLiveData.setValue(false);
        parametrosActuales = null;
    }

    public void cargarPublicaciones(PublicacionesRepository.ParametrosConsulta parametros) {
        if (parametros == null) {
            Log.e(TAG, "Parámetros de consulta no pueden ser null");
            errorLiveData.setValue(true);
            mensajeLiveData.setValue("Error: Parámetros de consulta no válidos");
            return;
        }

        limpiarEstadoParaNuevaConsulta();

        UUID newRequestId = UUID.randomUUID();
        currentRequestId.set(newRequestId);

        this.parametrosActuales = parametros;
        cargandoLiveData.setValue(true);
        errorLiveData.setValue(false);
        mensajeLiveData.setValue(null);

        Log.d(TAG, "Cargando publicaciones de tipo: " + parametros.getTipo() + " [RequestID: " + newRequestId + "]");

        currentObserver = resultado -> {
            if (!newRequestId.equals(currentRequestId.get())) {
                Log.d(TAG, "Ignorando respuesta obsoleta para request: " + newRequestId);
                return;
            }

            cargandoLiveData.setValue(false);

            if (resultado != null) {
                if (resultado.isExitoso()) {
                    List<DocumentoResponse> publicaciones = resultado.getPublicaciones();

                    if (publicaciones != null) {
                        publicacionesLiveData.setValue(new ArrayList<>(publicaciones));
                    } else {
                        publicacionesLiveData.setValue(new ArrayList<>());
                    }

                    errorLiveData.setValue(false);

                    if (resultado.tienePublicaciones()) {
                        mensajeLiveData.setValue(null);
                    } else {
                        mensajeLiveData.setValue(obtenerMensajeVacio(parametros.getTipo()));
                    }

                    Log.d(TAG, "Publicaciones cargadas exitosamente: " + resultado.getCantidad() +
                            " [RequestID: " + newRequestId + "]");
                } else {
                    publicacionesLiveData.setValue(new ArrayList<>());
                    errorLiveData.setValue(true);
                    mensajeLiveData.setValue(resultado.getMensaje());
                    Log.e(TAG, "Error al cargar publicaciones: " + resultado.getMensaje());
                }
            } else {
                publicacionesLiveData.setValue(new ArrayList<>());
                errorLiveData.setValue(true);
                mensajeLiveData.setValue("Error inesperado al cargar publicaciones");
                Log.e(TAG, "Resultado de carga es null");
            }
        };

        publicacionesRepository.obtenerPublicaciones(parametros).observeForever(currentObserver);
    }

    public void recargarPublicaciones() {
        if (parametrosActuales != null) {
            cargarPublicaciones(parametrosActuales);
        } else {
            Log.w(TAG, "No hay parámetros previos para recargar");
        }
    }

    public void cargarPublicacionesPropias(String token) {
        PublicacionesRepository.ParametrosConsulta parametros =
                new PublicacionesRepository.ParametrosConsulta(PublicacionesRepository.TipoPublicacion.PROPIAS)
                        .conToken(token);
        cargarPublicaciones(parametros);
    }

    public void cargarTodasLasPublicaciones() {
        PublicacionesRepository.ParametrosConsulta parametros =
                new PublicacionesRepository.ParametrosConsulta(PublicacionesRepository.TipoPublicacion.TODAS);
        cargarPublicaciones(parametros);
    }

    public void cargarPublicacionesDeUsuario(int idUsuario) {
        PublicacionesRepository.ParametrosConsulta parametros =
                new PublicacionesRepository.ParametrosConsulta(PublicacionesRepository.TipoPublicacion.DE_USUARIO)
                        .conIdUsuario(idUsuario);
        cargarPublicaciones(parametros);
    }

    public void cargarPublicacionesPorCategoria(int idCategoria) {
        PublicacionesRepository.ParametrosConsulta parametros =
                new PublicacionesRepository.ParametrosConsulta(PublicacionesRepository.TipoPublicacion.POR_CATEGORIA)
                        .conIdCategoria(idCategoria);
        cargarPublicaciones(parametros);
    }

    public void cargarPublicacionesPorRama(int idRama) {
        PublicacionesRepository.ParametrosConsulta parametros =
                new PublicacionesRepository.ParametrosConsulta(PublicacionesRepository.TipoPublicacion.POR_RAMA)
                        .conIdCategoria(idRama);
        cargarPublicaciones(parametros);
    }

    public void solicitarEliminarPublicacion(int idPublicacion, String tituloPublicacion) {
        this.publicacionAEliminarId = idPublicacion;
        mostrandoDialogoLiveData.setValue(true);
        Log.d(TAG, "Solicitando eliminación de publicación: " + tituloPublicacion + " (ID: " + idPublicacion + ")");
    }

    public int getPublicacionAEliminarId() {
        return publicacionAEliminarId;
    }

    public void confirmarEliminacionPublicacion(int idPublicacion, String token) {
        Log.d(TAG, "Confirmando eliminación de publicación con ID: " + idPublicacion);

        cargandoEliminacionLiveData.setValue(true);
        mostrandoDialogoLiveData.setValue(false);

        publicacionesRepository.eliminarPublicacion(idPublicacion, token).observeForever(resultado -> {
            cargandoEliminacionLiveData.setValue(false);

            if (resultado != null) {
                if (resultado.isExitoso()) {
                    Log.d(TAG, "Publicación eliminada exitosamente");
                    mensajeEliminacionLiveData.setValue(resultado.getMensaje());
                    eliminacionExitosaLiveData.setValue(true);
                    eliminarPublicacionDeLista(idPublicacion);
                } else {
                    Log.e(TAG, "Error al eliminar publicación: " + resultado.getMensaje());
                    mensajeEliminacionLiveData.setValue(resultado.getMensaje());
                    eliminacionExitosaLiveData.setValue(false);
                }
            } else {
                Log.e(TAG, "Resultado de eliminación es null");
                mensajeEliminacionLiveData.setValue("Error inesperado al eliminar la publicación");
                eliminacionExitosaLiveData.setValue(false);
            }
        });
    }

    public void cancelarEliminacion() {
        mostrandoDialogoLiveData.setValue(false);
        Log.d(TAG, "Eliminación cancelada por el usuario");
    }

    public void filtrarPublicaciones(String filtro) {
        List<DocumentoResponse> publicacionesActuales = publicacionesLiveData.getValue();
        if (publicacionesActuales == null || publicacionesActuales.isEmpty()) {
            return;
        }

        if (filtro == null || filtro.trim().isEmpty()) {
            recargarPublicaciones();
            return;
        }

        String filtroLower = filtro.toLowerCase().trim();
        List<DocumentoResponse> publicacionesFiltradas = new ArrayList<>();

        for (DocumentoResponse publicacion : publicacionesActuales) {
            if (publicacion.getTitulo().toLowerCase().contains(filtroLower) ||
                    publicacion.getResuContenido().toLowerCase().contains(filtroLower) ||
                    publicacion.getNombreCompleto().toLowerCase().contains(filtroLower)) {
                publicacionesFiltradas.add(publicacion);
            }
        }

        publicacionesLiveData.setValue(publicacionesFiltradas);

        if (publicacionesFiltradas.isEmpty()) {
            mensajeLiveData.setValue("No se encontraron publicaciones que coincidan con: " + filtro);
        } else {
            mensajeLiveData.setValue(null);
        }

        Log.d(TAG, "Filtradas " + publicacionesFiltradas.size() + " publicaciones de " + publicacionesActuales.size());
    }

    public boolean puedeEliminarPublicacion(DocumentoResponse publicacion, int idUsuarioActual) {
        return publicacion.getIdUsuarioRegistrado() == idUsuarioActual;
    }

    public void limpiarMensajesEliminacion() {
        mensajeEliminacionLiveData.setValue(null);
        eliminacionExitosaLiveData.setValue(null);
        cargandoEliminacionLiveData.setValue(false);
    }

    private String obtenerMensajeVacio(PublicacionesRepository.TipoPublicacion tipo) {
        switch (tipo) {
            case PROPIAS:
                return "Aún no has subido publicaciones";
            case TODAS:
                return "No hay publicaciones disponibles";
            case DE_USUARIO:
                return "Este usuario no tiene publicaciones";
            case POR_CATEGORIA:
                return "No hay publicaciones en esta categoría";
            case POR_RAMA:
                return "No hay publicaciones en esta rama";
            default:
                return "No se encontraron publicaciones";
        }
    }

    private void eliminarPublicacionDeLista(int idPublicacion) {
        List<DocumentoResponse> publicacionesActuales = publicacionesLiveData.getValue();
        if (publicacionesActuales != null) {
            List<DocumentoResponse> publicacionesActualizadas = new ArrayList<>(publicacionesActuales);
            publicacionesActualizadas.removeIf(publicacion ->
                    publicacion.getIdPublicacion() == idPublicacion);

            publicacionesLiveData.setValue(publicacionesActualizadas);
            Log.d(TAG, "Publicación con ID " + idPublicacion + " removida de la lista local");

            if (publicacionesActualizadas.isEmpty() && parametrosActuales != null) {
                mensajeLiveData.setValue(obtenerMensajeVacio(parametrosActuales.getTipo()));
            }
        }
    }

    public EstadisticasPublicaciones getEstadisticas() {
        List<DocumentoResponse> publicaciones = publicacionesLiveData.getValue();
        if (publicaciones == null || publicaciones.isEmpty()) {
            return new EstadisticasPublicaciones(0, 0, 0, 0);
        }

        int totalLikes = 0;
        int totalVisualizaciones = 0;
        int totalDescargas = 0;

        for (DocumentoResponse publicacion : publicaciones) {
            totalLikes += publicacion.getNumeroLiker();
            totalVisualizaciones += publicacion.getNumeroVisualizaciones();
            totalDescargas += publicacion.getNumeroDescargas();
        }

        return new EstadisticasPublicaciones(
                publicaciones.size(),
                totalLikes,
                totalVisualizaciones,
                totalDescargas
        );
    }

    public static class EstadisticasPublicaciones {
        private final int totalPublicaciones;
        private final int totalLikes;
        private final int totalVisualizaciones;
        private final int totalDescargas;

        public EstadisticasPublicaciones(int totalPublicaciones, int totalLikes,
                                         int totalVisualizaciones, int totalDescargas) {
            this.totalPublicaciones = totalPublicaciones;
            this.totalLikes = totalLikes;
            this.totalVisualizaciones = totalVisualizaciones;
            this.totalDescargas = totalDescargas;
        }

        public int getTotalPublicaciones() { return totalPublicaciones; }
        public int getTotalLikes() { return totalLikes; }
        public int getTotalVisualizaciones() { return totalVisualizaciones; }
        public int getTotalDescargas() { return totalDescargas; }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (currentObserver != null && parametrosActuales != null) {
            try {
                LiveData<PublicacionesRepository.ResultadoPublicaciones> liveData =
                        publicacionesRepository.obtenerPublicaciones(parametrosActuales);
                if (liveData != null) {
                    liveData.removeObserver(currentObserver);
                }
            } catch (Exception e) {
                Log.w(TAG, "Error al limpiar observadores en onCleared: " + e.getMessage());
            }
        }

        currentRequestId.set(null);
        currentObserver = null;
        Log.d(TAG, "ViewModel destruido y limpiado");
    }

    public void limpiarEstadoCompleto() {
        Log.d(TAG, "Limpiando estado completo del ViewModel");

        if (currentObserver != null && parametrosActuales != null) {
            try {
                LiveData<PublicacionesRepository.ResultadoPublicaciones> liveData =
                        publicacionesRepository.obtenerPublicaciones(parametrosActuales);
                if (liveData != null) {
                    liveData.removeObserver(currentObserver);
                }
            } catch (Exception e) {
                Log.w(TAG, "Error al limpiar observadores: " + e.getMessage());
            }
        }

        currentRequestId.set(null);
        currentObserver = null;

        inicializarEstado();

        parametrosActuales = null;
        publicacionAEliminarId = -1;
    }
}
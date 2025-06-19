package com.example.edushareandroid.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepository repository;

    private final MutableLiveData<List<Categoria>> categorias = new MutableLiveData<>();
    private final MutableLiveData<List<Rama>> ramas = new MutableLiveData<>();
    private final MutableLiveData<List<Materia>> materias = new MutableLiveData<>();

    private final MutableLiveData<List<DocumentoResponse>> publicaciones = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoPublicaciones = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeExito = new MutableLiveData<>();

    private final MutableLiveData<Integer> categoriaSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Integer> ramaSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Boolean> filtrosActivos = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        String token = SesionUsuario.obtenerToken(application.getApplicationContext());
        repository = new HomeRepository(application.getApplicationContext(), token);

        cargarTodasLasPublicaciones();
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

    public LiveData<List<DocumentoResponse>> getPublicaciones() {
        return publicaciones;
    }

    public LiveData<Boolean> getCargandoPublicaciones() {
        return cargandoPublicaciones;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<String> getMensajeExito() {
        return mensajeExito;
    }

    public LiveData<Integer> getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public LiveData<Integer> getRamaSeleccionada() {
        return ramaSeleccionada;
    }

    public LiveData<Boolean> getFiltrosActivos() {
        return filtrosActivos;
    }

    public void cargarCategorias() {
        repository.obtenerCategorias().observeForever(new Observer<List<Categoria>>() {
            @Override
            public void onChanged(List<Categoria> resultado) {
                categorias.setValue(resultado);
                repository.obtenerCategorias().removeObserver(this);
            }
        });
    }

    public void cargarRamas() {
        repository.obtenerRamas().observeForever(new Observer<List<Rama>>() {
            @Override
            public void onChanged(List<Rama> resultado) {
                ramas.setValue(resultado);
                repository.obtenerRamas().removeObserver(this);
            }
        });
    }

    public void cargarMateriasPorRama(int idRama) {
        repository.obtenerMateriasPorRama(idRama).observeForever(new Observer<List<Materia>>() {
            @Override
            public void onChanged(List<Materia> resultado) {
                materias.setValue(resultado);
                // Remover el observer para evitar memory leaks
                repository.obtenerMateriasPorRama(idRama).removeObserver(this);
            }
        });
    }

    public void cargarTodasLasPublicaciones() {
        cargandoPublicaciones.setValue(true);
        limpiarMensajes();

        repository.obtenerTodasLasPublicaciones().observeForever(new Observer<HomeRepository.ResultadoPublicaciones>() {
            @Override
            public void onChanged(HomeRepository.ResultadoPublicaciones resultado) {
                cargandoPublicaciones.setValue(false);

                if (resultado.isExitoso()) {
                    publicaciones.setValue(resultado.getPublicaciones());
                    if (resultado.tienePublicaciones()) {
                        mensajeExito.setValue(resultado.getMensaje());
                    }
                } else {
                    mensajeError.setValue(resultado.getMensaje());
                }

                repository.obtenerTodasLasPublicaciones().removeObserver(this);
            }
        });
    }

    public void filtrarPorCategoria(Integer idCategoria) {
        if (idCategoria == null) {
            limpiarFiltroCategoria();
            return;
        }

        cargandoPublicaciones.setValue(true);
        limpiarMensajes();
        categoriaSeleccionada.setValue(idCategoria);
        actualizarEstadoFiltros();

        repository.obtenerPublicacionesPorCategoria(idCategoria).observeForever(new Observer<HomeRepository.ResultadoPublicaciones>() {
            @Override
            public void onChanged(HomeRepository.ResultadoPublicaciones resultado) {
                cargandoPublicaciones.setValue(false);

                if (resultado.isExitoso()) {
                    publicaciones.setValue(resultado.getPublicaciones());
                    mensajeExito.setValue(resultado.getMensaje());
                } else {
                    mensajeError.setValue(resultado.getMensaje());
                }

                repository.obtenerPublicacionesPorCategoria(idCategoria).removeObserver(this);
            }
        });
    }

    public void filtrarPorRama(Integer idRama) {
        if (idRama == null) {
            limpiarFiltroRama();
            return;
        }

        cargandoPublicaciones.setValue(true);
        limpiarMensajes();
        ramaSeleccionada.setValue(idRama);
        actualizarEstadoFiltros();

        repository.obtenerPublicacionesPorRama(idRama).observeForever(new Observer<HomeRepository.ResultadoPublicaciones>() {
            @Override
            public void onChanged(HomeRepository.ResultadoPublicaciones resultado) {
                cargandoPublicaciones.setValue(false);

                if (resultado.isExitoso()) {
                    publicaciones.setValue(resultado.getPublicaciones());
                    mensajeExito.setValue(resultado.getMensaje());
                } else {
                    mensajeError.setValue(resultado.getMensaje());
                }

                repository.obtenerPublicacionesPorRama(idRama).removeObserver(this);
            }
        });
    }

    public void aplicarFiltros(Integer idCategoria, Integer idRama) {
        cargandoPublicaciones.setValue(true);
        limpiarMensajes();

        categoriaSeleccionada.setValue(idCategoria);
        ramaSeleccionada.setValue(idRama);
        actualizarEstadoFiltros();

        repository.obtenerPublicacionesConFiltros(idCategoria, idRama).observeForever(new Observer<HomeRepository.ResultadoPublicaciones>() {
            @Override
            public void onChanged(HomeRepository.ResultadoPublicaciones resultado) {
                cargandoPublicaciones.setValue(false);

                if (resultado.isExitoso()) {
                    publicaciones.setValue(resultado.getPublicaciones());
                    mensajeExito.setValue(resultado.getMensaje());
                } else {
                    mensajeError.setValue(resultado.getMensaje());
                }

                repository.obtenerPublicacionesConFiltros(idCategoria, idRama).removeObserver(this);
            }
        });
    }

    public void limpiarFiltros() {
        categoriaSeleccionada.setValue(null);
        ramaSeleccionada.setValue(null);
        filtrosActivos.setValue(false);
        cargarTodasLasPublicaciones();
    }

    public void refrescarPublicaciones() {
        Integer categoria = categoriaSeleccionada.getValue();
        Integer rama = ramaSeleccionada.getValue();

        if (categoria != null || rama != null) {
            aplicarFiltros(categoria, rama);
        } else {
            cargarTodasLasPublicaciones();
        }
    }

    private void limpiarFiltroCategoria() {
        categoriaSeleccionada.setValue(null);
        actualizarEstadoFiltros();
        if (ramaSeleccionada.getValue() != null) {
            filtrarPorRama(ramaSeleccionada.getValue());
        } else {
            cargarTodasLasPublicaciones();
        }
    }

    private void limpiarFiltroRama() {
        ramaSeleccionada.setValue(null);
        actualizarEstadoFiltros();
        if (categoriaSeleccionada.getValue() != null) {
            filtrarPorCategoria(categoriaSeleccionada.getValue());
        } else {
            cargarTodasLasPublicaciones();
        }
    }

    private void actualizarEstadoFiltros() {
        boolean hayFiltros = categoriaSeleccionada.getValue() != null || ramaSeleccionada.getValue() != null;
        filtrosActivos.setValue(hayFiltros);
    }

    private void limpiarMensajes() {
        mensajeError.setValue(null);
        mensajeExito.setValue(null);
    }

    public String obtenerTextoFiltrosActivos() {
        Integer categoria = categoriaSeleccionada.getValue();
        Integer rama = ramaSeleccionada.getValue();

        if (categoria == null && rama == null) {
            return "Todas las publicaciones";
        }

        StringBuilder texto = new StringBuilder("Filtros: ");
        if (categoria != null) {
            texto.append("Categoría ").append(categoria);
        }
        if (rama != null) {
            if (categoria != null) {
                texto.append(", ");
            }
            texto.append("Rama ").append(rama);
        }

        return texto.toString();
    }

    public boolean hayPublicaciones() {
        List<DocumentoResponse> pubs = publicaciones.getValue();
        return pubs != null && !pubs.isEmpty();
    }

    public int getCantidadPublicaciones() {
        List<DocumentoResponse> pubs = publicaciones.getValue();
        return pubs != null ? pubs.size() : 0;
    }
}
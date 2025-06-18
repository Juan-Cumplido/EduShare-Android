package com.example.edushareandroid.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Adapter específico para las publicaciones del HomeFragment
 * Mantiene la misma funcionalidad que PublicacionesAdapter pero optimizado para Home
 */
public class HomePublicacionesAdapter extends RecyclerView.Adapter<HomePublicacionesAdapter.HomePublicacionViewHolder> {

    private List<DocumentoResponse> listaPublicaciones;
    private final FileServiceClient fileServiceClient;
    private final LruCache<String, Bitmap> memoryCache;
    private final ExecutorService imageProcessingExecutor;
    private final Context context;
    private final OnHomeItemClickListener listener;

    /**
     * Interface específica para eventos del HomeFragment
     */
    public interface OnHomeItemClickListener {
        void onVerMasClick(DocumentoResponse publicacion);
        void onOpcionesClick(DocumentoResponse publicacion);
        void onEliminarClick(DocumentoResponse publicacion);
        void onCompartirClick(DocumentoResponse publicacion); // Funcionalidad adicional para Home
        void onFavoritoClick(DocumentoResponse publicacion); // Funcionalidad adicional para Home
    }

    public HomePublicacionesAdapter(List<DocumentoResponse> listaPublicaciones,
                                    FileServiceClient fileServiceClient,
                                    OnHomeItemClickListener listener,
                                    Context context) {
        this.listaPublicaciones = listaPublicaciones != null ? listaPublicaciones : new ArrayList<>();
        this.fileServiceClient = fileServiceClient;
        this.listener = listener;
        this.context = context;

        // Configurar caché de imágenes optimizado para Home
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 6; // Un poco más de caché para Home
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        // Pool de hilos optimizado para Home (puede manejar más imágenes simultáneas)
        imageProcessingExecutor = Executors.newFixedThreadPool(3);
    }

    /**
     * Actualiza la lista de publicaciones y notifica los cambios
     */
    public void actualizarLista(List<DocumentoResponse> nuevaLista) {
        listaPublicaciones.clear();
        if (nuevaLista != null) {
            listaPublicaciones.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    /**
     * Agrega nuevas publicaciones a la lista existente (útil para paginación)
     */
    public void agregarPublicaciones(List<DocumentoResponse> nuevasPublicaciones) {
        if (nuevasPublicaciones != null && !nuevasPublicaciones.isEmpty()) {
            int posicionInicial = listaPublicaciones.size();
            listaPublicaciones.addAll(nuevasPublicaciones);
            notifyItemRangeInserted(posicionInicial, nuevasPublicaciones.size());
        }
    }

    /**
     * Elimina una publicación específica de la lista
     */
    public void eliminarPublicacion(DocumentoResponse publicacion) {
        int posicion = listaPublicaciones.indexOf(publicacion);
        if (posicion != -1) {
            listaPublicaciones.remove(posicion);
            notifyItemRemoved(posicion);
        }
    }

    /**
     * Obtiene una publicación por posición
     */
    public DocumentoResponse getPublicacion(int posicion) {
        if (posicion >= 0 && posicion < listaPublicaciones.size()) {
            return listaPublicaciones.get(posicion);
        }
        return null;
    }

    /**
     * Verifica si hay publicaciones cargadas
     */
    public boolean hayPublicaciones() {
        return !listaPublicaciones.isEmpty();
    }

    @NonNull
    @Override
    public HomePublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado, parent, false);
        return new HomePublicacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePublicacionViewHolder holder, int position) {
        DocumentoResponse publicacion = listaPublicaciones.get(position);
        holder.bind(publicacion);
    }

    @Override
    public void onViewRecycled(@NonNull HomePublicacionViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelPendingOperations();
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    /**
     * Limpia recursos cuando el adapter ya no se necesita
     */
    public void cleanup() {
        if (imageProcessingExecutor != null && !imageProcessingExecutor.isShutdown()) {
            imageProcessingExecutor.shutdown();
        }
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

    /**
     * Limpia la caché de imágenes
     */
    public void limpiarCache() {
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

    /**
     * ViewHolder específico para publicaciones del Home
     */
    class HomePublicacionViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitulo;
        private final TextView txtResumen;
        private final TextView txtFecha;
        private final TextView txtEstado;
        private final TextView txtAutor;
        private final ImageView imgPortada;
        private final ImageButton btnVerMas;
        private final ImageButton btnOpciones;

        private String currentImagePath;
        private Future<?> imageProcessingFuture;

        public HomePublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txt_titulo_documento);
            txtResumen = itemView.findViewById(R.id.txt_subtitulo);
            txtFecha = itemView.findViewById(R.id.txt_detalles);
            txtEstado = itemView.findViewById(R.id.txt_estado);
            txtAutor = itemView.findViewById(R.id.txt_autor); // Asumiendo que existe este campo
            imgPortada = itemView.findViewById(R.id.img_portada);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas);
            btnOpciones = itemView.findViewById(R.id.btn_opciones);
        }

        void bind(DocumentoResponse publicacion) {
            // Configurar datos básicos
            txtTitulo.setText(publicacion.getTitulo());
            txtResumen.setText(publicacion.getResuContenido());
            txtFecha.setText(formatearFecha(publicacion.getFecha()));
            txtEstado.setText(publicacion.getEstado());

            // Mostrar autor (útil en el feed de Home)
            if (txtAutor != null) {
                txtAutor.setText(publicacion.getNombreCompleto());
            }

            // Obtener ID del usuario actual
            int idUsuarioActual = SesionUsuario.obtenerDatosUsuario(context).getIdUsuario();
            boolean esMiPublicacion = publicacion.getIdUsuarioRegistrado() == idUsuarioActual;

            // Configurar visibilidad del botón de opciones
            btnOpciones.setVisibility(esMiPublicacion ? View.GONE : View.GONE);

            // Configurar listeners
            configurarListeners(publicacion, esMiPublicacion);

            // Cargar imagen de portada
            loadCoverImage(publicacion.getRuta());
        }

        private void configurarListeners(DocumentoResponse publicacion, boolean esMiPublicacion) {
            // Listener para ver más
            btnVerMas.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(publicacion);
                }
            });

            // Listener para opciones (solo si es mi publicación)
            if (esMiPublicacion) {
                btnOpciones.setOnClickListener(v -> mostrarMenuOpciones(v, publicacion));
            } else {
                btnOpciones.setOnClickListener(null);
            }

            // Listener para clic en el elemento completo (opcional)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(publicacion);
                }
            });
        }

        private String formatearFecha(String fecha) {
            if (fecha != null && fecha.length() >= 10) {
                // Formatear fecha para mostrar solo la fecha sin hora
                return fecha.substring(0, 10);
            }
            return fecha != null ? fecha : "";
        }

        private void mostrarMenuOpciones(View view, DocumentoResponse publicacion) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

            // Agregar opciones específicas para Home
            popupMenu.getMenu().add(0, 1, 0, "Compartir");
            popupMenu.getMenu().add(0, 2, 0, "Editar");
            popupMenu.getMenu().add(0, 3, 0, "Eliminar");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener == null) return false;

                switch (item.getItemId()) {
                    case 1: // Compartir
                        listener.onCompartirClick(publicacion);
                        return true;
                    case 2: // Editar (redirigir a opciones)
                        listener.onOpcionesClick(publicacion);
                        return true;
                    case 3: // Eliminar
                        listener.onEliminarClick(publicacion);
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        }

        void loadCoverImage(String ruta) {
            cancelPendingOperations();
            currentImagePath = ruta;
            imgPortada.setImageResource(R.drawable.ic_archivo); // Imagen por defecto

            if (ruta == null || ruta.isEmpty()) return;

            // Verificar caché
            Bitmap cachedBitmap = memoryCache.get(ruta);
            if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
                imgPortada.setImageBitmap(cachedBitmap);
                return;
            }

            // Descargar imagen si no está en caché
            fileServiceClient.downloadCover(ruta, new FileServiceClient.DownloadCallback() {
                @Override
                public void onSuccess(byte[] imageData, String filename) {
                    if (!ruta.equals(currentImagePath)) return;
                    processImageAsync(ruta, imageData);
                }

                @Override
                public void onError(Exception e) {
                    if (ruta.equals(currentImagePath)) {
                        imgPortada.post(() -> {
                            if (ruta.equals(currentImagePath)) {
                                imgPortada.setImageResource(R.drawable.ic_archivo);
                            }
                        });
                    }
                }
            });
        }

        private void processImageAsync(String path, byte[] imageData) {
            imageProcessingFuture = imageProcessingExecutor.submit(() -> {
                try {
                    Bitmap original = ImageUtil.binaryToBitmap(imageData);
                    if (original == null) return;

                    // Redimensionar para optimizar memoria (tamaño específico para Home)
                    Bitmap resized = Bitmap.createScaledBitmap(original, 400, 300, true);

                    // Verificar que el bitmap no esté corrupto antes de guardarlo en caché
                    if (!resized.isRecycled()) {
                        memoryCache.put(path, resized);

                        if (path.equals(currentImagePath)) {
                            imgPortada.post(() -> {
                                if (path.equals(currentImagePath) && !resized.isRecycled()) {
                                    imgPortada.setImageBitmap(resized);
                                }
                            });
                        }
                    }

                    // Limpiar bitmap original si es diferente
                    if (original != resized && !original.isRecycled()) {
                        original.recycle();
                    }
                } catch (Exception e) {
                    // Error silencioso, mantener imagen por defecto
                    if (path.equals(currentImagePath)) {
                        imgPortada.post(() -> {
                            if (path.equals(currentImagePath)) {
                                imgPortada.setImageResource(R.drawable.ic_archivo);
                            }
                        });
                    }
                }
            });
        }

        void cancelPendingOperations() {
            currentImagePath = null;
            if (imageProcessingFuture != null && !imageProcessingFuture.isDone()) {
                imageProcessingFuture.cancel(true);
            }
        }
    }
}
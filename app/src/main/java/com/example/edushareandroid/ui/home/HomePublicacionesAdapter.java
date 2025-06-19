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

public class HomePublicacionesAdapter extends RecyclerView.Adapter<HomePublicacionesAdapter.HomePublicacionViewHolder> {

    private List<DocumentoResponse> listaPublicaciones;
    private final FileServiceClient fileServiceClient;
    private final LruCache<String, Bitmap> memoryCache;
    private final ExecutorService imageProcessingExecutor;
    private final Context context;
    private final OnHomeItemClickListener listener;

    public interface OnHomeItemClickListener {
        void onVerMasClick(DocumentoResponse publicacion);

        void onOpcionesClick(DocumentoResponse publicacion);

        void onEliminarClick(DocumentoResponse publicacion);

        void onCompartirClick(DocumentoResponse publicacion);

        void onFavoritoClick(DocumentoResponse publicacion);
    }

    public HomePublicacionesAdapter(List<DocumentoResponse> listaPublicaciones,
                                    FileServiceClient fileServiceClient,
                                    OnHomeItemClickListener listener,
                                    Context context) {
        this.listaPublicaciones = listaPublicaciones != null ? listaPublicaciones : new ArrayList<>();
        this.fileServiceClient = fileServiceClient;
        this.listener = listener;
        this.context = context;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 6;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        imageProcessingExecutor = Executors.newFixedThreadPool(3);
    }

    public void actualizarLista(List<DocumentoResponse> nuevaLista) {
        listaPublicaciones.clear();
        if (nuevaLista != null) {
            listaPublicaciones.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    public void agregarPublicaciones(List<DocumentoResponse> nuevasPublicaciones) {
        if (nuevasPublicaciones != null && !nuevasPublicaciones.isEmpty()) {
            int posicionInicial = listaPublicaciones.size();
            listaPublicaciones.addAll(nuevasPublicaciones);
            notifyItemRangeInserted(posicionInicial, nuevasPublicaciones.size());
        }
    }

    public void eliminarPublicacion(DocumentoResponse publicacion) {
        int posicion = listaPublicaciones.indexOf(publicacion);
        if (posicion != -1) {
            listaPublicaciones.remove(posicion);
            notifyItemRemoved(posicion);
        }
    }

    public DocumentoResponse getPublicacion(int posicion) {
        if (posicion >= 0 && posicion < listaPublicaciones.size()) {
            return listaPublicaciones.get(posicion);
        }
        return null;
    }

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

    public void cleanup() {
        if (imageProcessingExecutor != null && !imageProcessingExecutor.isShutdown()) {
            imageProcessingExecutor.shutdown();
        }
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

    public void limpiarCache() {
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

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
            txtAutor = itemView.findViewById(R.id.txt_autor);
            imgPortada = itemView.findViewById(R.id.img_portada);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas);
            btnOpciones = itemView.findViewById(R.id.btn_opciones);
        }

        void bind(DocumentoResponse publicacion) {
            txtTitulo.setText(publicacion.getTitulo());
            txtResumen.setText(publicacion.getResuContenido());
            txtFecha.setText(formatearFecha(publicacion.getFecha()));
            txtEstado.setText(publicacion.getEstado());

            if (txtAutor != null) {
                txtAutor.setText(publicacion.getNombreCompleto());
            }

            int idUsuarioActual = SesionUsuario.obtenerDatosUsuario(context).getIdUsuario();
            boolean esMiPublicacion = publicacion.getIdUsuarioRegistrado() == idUsuarioActual;

            btnOpciones.setVisibility(esMiPublicacion ? View.GONE : View.GONE);

            configurarListeners(publicacion, esMiPublicacion);

            loadCoverImage(publicacion.getRuta());
        }

        private void configurarListeners(DocumentoResponse publicacion, boolean esMiPublicacion) {
            btnVerMas.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(publicacion);
                }
            });

            if (esMiPublicacion) {
                btnOpciones.setOnClickListener(v -> mostrarMenuOpciones(v, publicacion));
            } else {
                btnOpciones.setOnClickListener(null);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(publicacion);
                }
            });
        }

        private String formatearFecha(String fecha) {
            if (fecha != null && fecha.length() >= 10) {
                return fecha.substring(0, 10);
            }
            return fecha != null ? fecha : "";
        }

        private void mostrarMenuOpciones(View view, DocumentoResponse publicacion) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

            popupMenu.getMenu().add(0, 1, 0, "Compartir");
            popupMenu.getMenu().add(0, 2, 0, "Editar");
            popupMenu.getMenu().add(0, 3, 0, "Eliminar");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener == null) return false;

                switch (item.getItemId()) {
                    case 1:
                        listener.onCompartirClick(publicacion);
                        return true;
                    case 2:
                        listener.onOpcionesClick(publicacion);
                        return true;
                    case 3:
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
            imgPortada.setImageResource(R.drawable.ic_archivo);

            if (ruta == null || ruta.isEmpty()) return;

            Bitmap cachedBitmap = memoryCache.get(ruta);
            if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
                imgPortada.setImageBitmap(cachedBitmap);
                return;
            }

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

                    Bitmap resized = Bitmap.createScaledBitmap(original, 400, 300, true);

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

                    if (original != resized && !original.isRecycled()) {
                        original.recycle();
                    }
                } catch (Exception e) {
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
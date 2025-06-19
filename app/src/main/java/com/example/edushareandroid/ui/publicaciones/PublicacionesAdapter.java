package com.example.edushareandroid.ui.publicaciones;

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

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionViewHolder> {

    private List<DocumentoResponse> listaPublicaciones;
    private final FileServiceClient fileServiceClient;
    private final LruCache<String, Bitmap> memoryCache;
    private final ExecutorService imageProcessingExecutor;
    private final Context context;

    public interface OnItemClickListener {
        void onVerMasClick(DocumentoResponse publicacion);

        void onOpcionesClick(DocumentoResponse publicacion);

        void onEliminarClick(DocumentoResponse publicacion);
    }

    private final OnItemClickListener listener;

    public PublicacionesAdapter(List<DocumentoResponse> listaPublicaciones,
                                FileServiceClient fileServiceClient,
                                OnItemClickListener listener,
                                Context context) {
        this.listaPublicaciones = listaPublicaciones;
        this.fileServiceClient = fileServiceClient;
        this.listener = listener;
        this.context = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        imageProcessingExecutor = Executors.newFixedThreadPool(2);
    }

    public void actualizarLista(List<DocumentoResponse> nuevaLista) {
        listaPublicaciones.clear();
        if (nuevaLista != null) {
            listaPublicaciones.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado, parent, false);
        return new PublicacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        DocumentoResponse publicacion = listaPublicaciones.get(position);
        holder.bind(publicacion);
    }

    @Override
    public void onViewRecycled(@NonNull PublicacionViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelPendingOperations();
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones != null ? listaPublicaciones.size() : 0;
    }

    public void cleanup() {
        if (imageProcessingExecutor != null && !imageProcessingExecutor.isShutdown()) {
            imageProcessingExecutor.shutdown();
        }
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

    class PublicacionViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitulo;
        private final TextView txtResumen;
        private final TextView txtFecha;
        private final TextView txtEstado;
        private final ImageView imgPortada;
        private final ImageButton btnVerMas;
        private final ImageButton btnOpciones;

        private String currentImagePath;
        private Future<?> imageProcessingFuture;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txt_titulo_documento);
            txtResumen = itemView.findViewById(R.id.txt_subtitulo);
            txtFecha = itemView.findViewById(R.id.txt_detalles);
            txtEstado = itemView.findViewById(R.id.txt_estado);
            imgPortada = itemView.findViewById(R.id.img_portada);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas);
            btnOpciones = itemView.findViewById(R.id.btn_opciones);
        }

        void bind(DocumentoResponse publicacion) {
            txtTitulo.setText(publicacion.getTitulo());
            txtResumen.setText(publicacion.getResuContenido());
            txtFecha.setText(publicacion.getFecha().substring(0, 10));
            txtEstado.setText(publicacion.getEstado());

            int idUsuarioActual = SesionUsuario.obtenerDatosUsuario(context).getIdUsuario();

            boolean esMiPublicacion = publicacion.getIdUsuarioRegistrado() == idUsuarioActual;

            btnOpciones.setVisibility(esMiPublicacion ? View.VISIBLE : View.GONE);

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

            loadCoverImage(publicacion.getRuta());
        }

        private String formatNumber(int number) {
            if (number < 1000) {
                return String.valueOf(number);
            } else if (number < 1000000) {
                return String.format("%.1fK", number / 1000.0);
            } else {
                return String.format("%.1fM", number / 1000000.0);
            }
        }

        private void mostrarMenuOpciones(View view, DocumentoResponse publicacion) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenu().add(0, 1, 0, "Eliminar");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1 && listener != null) {
                    listener.onEliminarClick(publicacion);
                    return true;
                }
                return false;
            });

            popupMenu.show();

            if (listener != null) {
                listener.onOpcionesClick(publicacion);
            }
        }

        void loadCoverImage(String ruta) {
            cancelPendingOperations();
            currentImagePath = ruta;
            imgPortada.setImageResource(R.drawable.ic_archivo);

            if (ruta == null || ruta.isEmpty()) return;

            Bitmap cachedBitmap = memoryCache.get(ruta);
            if (cachedBitmap != null) {
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
                    memoryCache.put(path, resized);

                    if (path.equals(currentImagePath)) {
                        imgPortada.post(() -> {
                            if (path.equals(currentImagePath)) {
                                imgPortada.setImageBitmap(resized);
                            }
                        });
                    }

                    if (original != resized) {
                        original.recycle();
                    }
                } catch (Exception e) {
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
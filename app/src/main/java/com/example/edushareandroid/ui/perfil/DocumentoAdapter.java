package com.example.edushareandroid.ui.perfil;

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
import com.example.edushareandroid.utils.ImageUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentoAdapter extends RecyclerView.Adapter<DocumentoAdapter.DocumentoViewHolder> {

    private List<DocumentoResponse> listaDocumentos;
    private final FileServiceClient fileServiceClient;
    private final LruCache<String, Bitmap> memoryCache;
    private final ExecutorService imageProcessingExecutor;

    public interface OnItemClickListener {
        void onVerMasClick(DocumentoResponse documento);
        void onOpcionesClick(DocumentoResponse documento);
        void onEliminarClick(DocumentoResponse documento);
    }

    private final OnItemClickListener listener;

    public DocumentoAdapter(List<DocumentoResponse> listaDocumentos,
                            FileServiceClient fileServiceClient,
                            OnItemClickListener listener) {
        this.listaDocumentos = listaDocumentos;
        this.fileServiceClient = fileServiceClient;
        this.listener = listener;

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
        this.listaDocumentos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado, parent, false);
        return new DocumentoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentoViewHolder holder, int position) {
        DocumentoResponse documento = listaDocumentos.get(position);
        holder.bind(documento);
    }

    @Override
    public void onViewRecycled(@NonNull DocumentoViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelPendingOperations();
    }

    @Override
    public int getItemCount() {
        return listaDocumentos != null ? listaDocumentos.size() : 0;
    }

    class DocumentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtResumen, txtFecha, txtEstado;
        ImageView imgPortada;
        ImageButton btnVerMas, btnOpciones;

        private String currentImagePath;
        private Future<?> imageProcessingFuture;

        public DocumentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txt_titulo_documento);
            txtResumen = itemView.findViewById(R.id.txt_subtitulo);
            txtFecha = itemView.findViewById(R.id.txt_detalles);
            txtEstado = itemView.findViewById(R.id.txt_estado);
            imgPortada = itemView.findViewById(R.id.img_portada);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas);
            btnOpciones = itemView.findViewById(R.id.btn_opciones);
        }

        void bind(DocumentoResponse documento) {
            txtTitulo.setText(documento.getTitulo());
            txtResumen.setText(documento.getResuContenido());
            txtFecha.setText(documento.getFecha().substring(0, 10));
            txtEstado.setText(documento.getEstado());

            btnVerMas.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(documento);
                }
            });

            btnOpciones.setOnClickListener(v -> mostrarMenuOpciones(v, documento));
            loadCoverImage(documento.getRuta());
        }

        private void mostrarMenuOpciones(View view, DocumentoResponse documento) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        if (listener != null) {
                            listener.onEliminarClick(documento);
                        }
                        return true;
                    default:
                        return false;
                }
            });
            if (listener != null) {
                listener.onOpcionesClick(documento);
            }
            popupMenu.show();
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


    public void cleanup() {
        if (imageProcessingExecutor != null && !imageProcessingExecutor.isShutdown()) {
            imageProcessingExecutor.shutdown();
        }
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }
}
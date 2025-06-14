package com.example.edushareandroid.ui.usuarios;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.UsuarioPerfilRecuperado;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UsuarioAdapterPerfil extends RecyclerView.Adapter<UsuarioAdapterPerfil.UsuarioViewHolder> {

    private List<UsuarioPerfilRecuperado> listaUsuarios;
    private final OnItemClickListener listener;
    private final FileServiceClient fileServiceClient;

    // Cache de memoria para imágenes
    private final LruCache<String, Bitmap> memoryCache;
    // Thread pool para procesamiento de imágenes
    private final ExecutorService imageProcessingExecutor;

    public interface OnItemClickListener {
        void onItemClick(UsuarioPerfilRecuperado usuario);

        void onVerMasClick(UsuarioPerfilRecuperado usuario);
    }

    public UsuarioAdapterPerfil(List<UsuarioPerfilRecuperado> listaUsuarios,
                                OnItemClickListener listener,
                                FileServiceClient fileServiceClient) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
        this.fileServiceClient = fileServiceClient;

        // Configurar cache de memoria (1/8 de la memoria disponible)
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        // Thread pool con 2 hilos para procesamiento de imágenes
        imageProcessingExecutor = Executors.newFixedThreadPool(2);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioPerfilRecuperado usuario = listaUsuarios.get(position);
        holder.bindBasicData(usuario);
        holder.loadProfileImage(usuario.getFotoPerfil());
    }

    @Override
    public void onViewRecycled(@NonNull UsuarioViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelPendingOperations();
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public void updateData(List<UsuarioPerfilRecuperado> nuevaLista) {
        this.listaUsuarios = nuevaLista;
        notifyDataSetChanged();
    }

    public void clearResources() {
        memoryCache.evictAll();
        imageProcessingExecutor.shutdownNow();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreCompleto, tvNombreUsuario, tvNivelEducativo,
                tvPublicaciones, tvSeguidores;
        private final ImageView imgFotoPerfil;
        private final ImageButton btnVerMas;

        private String currentImagePath;
        private Future<?> imageProcessingFuture;

        UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.txt_nombre_completo);
            tvNombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario);
            tvNivelEducativo = itemView.findViewById(R.id.txt_nivel_educativo);
            tvPublicaciones = itemView.findViewById(R.id.txt_publicaciones);
            tvSeguidores = itemView.findViewById(R.id.txt_seguidores);
            imgFotoPerfil = itemView.findViewById(R.id.img_foto_perfil);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas);
        }

        void bindBasicData(UsuarioPerfilRecuperado usuario) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getPrimerApellido();
            if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isEmpty()) {
                nombreCompleto += " " + usuario.getSegundoApellido();
            }

            tvNombreCompleto.setText(nombreCompleto);
            tvNombreUsuario.setText("@" + usuario.getNombreUsuario());
            tvNivelEducativo.setText(usuario.getNivelEducativo());
            tvPublicaciones.setText(String.format("%d Publicaciones", usuario.getNumeroPublicaciones()));
            tvSeguidores.setText(String.format("%d Seguidores", usuario.getNumeroSeguidores()));

            // Configurar listeners
            itemView.setOnClickListener(v -> listener.onItemClick(usuario));
            btnVerMas.setOnClickListener(v -> listener.onVerMasClick(usuario));
        }

        void loadProfileImage(String imagePath) {
            cancelPendingOperations();
            currentImagePath = imagePath;
            imgFotoPerfil.setImageResource(R.drawable.ic_perfil);
            if (imagePath == null || imagePath.isEmpty()) {
                return;
            }
            Bitmap cachedBitmap = memoryCache.get(imagePath);
            if (cachedBitmap != null) {
                imgFotoPerfil.setImageBitmap(cachedBitmap);
                return;
            }
            fileServiceClient.downloadImage(imagePath, new FileServiceClient.DownloadCallback() {
                @Override
                public void onSuccess(byte[] imageData, String filename) {
                    if (!imagePath.equals(currentImagePath)) {
                        return;
                    }
                    processImageAsync(imagePath, imageData);
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }

        private void processImageAsync(String imagePath, byte[] imageData) {
            imageProcessingFuture = imageProcessingExecutor.submit(() -> {
                try {
                    Bitmap originalBitmap = ImageUtil.binaryToBitmap(imageData);
                    if (originalBitmap == null) return;
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                            originalBitmap,
                            200,
                            200,
                            true
                    );
                    memoryCache.put(imagePath, resizedBitmap);
                    if (imagePath.equals(currentImagePath)) {
                        imgFotoPerfil.post(() -> {
                            if (imagePath.equals(currentImagePath)) {
                                imgFotoPerfil.setImageBitmap(resizedBitmap);
                            }
                        });
                    }
                } catch (Exception e) {
                    // Manejar error de procesamiento
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
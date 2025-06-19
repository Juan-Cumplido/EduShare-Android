package com.example.edushareandroid.ui.verarchivo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> comentarios;
    private OnItemClickListener listener;
    private Context context;
    private FileServiceClient fileServiceClient;
    private final int idUsuarioLogueado;

    public interface OnItemClickListener {
        void onEliminarClick(int position);
    }

    public ComentarioAdapter(Context context, List<Comentario> comentarios, int idUsuarioLogueado, OnItemClickListener listener, FileServiceClient fileServiceClient) {
        this.context = context;
        this.comentarios = comentarios != null ? comentarios : new ArrayList<>();
        this.idUsuarioLogueado = idUsuarioLogueado;
        this.listener = listener;
        this.fileServiceClient = fileServiceClient;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        Log.d("ComentarioAdapter", "Comentario de usuario: " + comentario.getIdUsuarioRegistrado());

        String nombreUsuario = "Usuario anónimo";
        if (comentario.getNombre() != null && !comentario.getNombre().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(comentario.getNombre());
            if (comentario.getPrimerApellido() != null && !comentario.getPrimerApellido().isEmpty()) {
                sb.append(" ").append(comentario.getPrimerApellido());
            }
            if (comentario.getSegundoApellido() != null && !comentario.getSegundoApellido().isEmpty()) {
                sb.append(" ").append(comentario.getSegundoApellido());
            }
            nombreUsuario = sb.toString();
        }
        holder.txtNombreUsuario.setText(nombreUsuario);

        String fechaComentario = comentario.getFecha();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());  // Formato deseado
        try {
            Date date = inputFormat.parse(fechaComentario);
            String formattedDate = outputFormat.format(date);
            holder.txtFechaComentario.setText(formattedDate);
        } catch (Exception e) {
            holder.txtFechaComentario.setText(fechaComentario);
        }

        holder.txtComentario.setText(comentario.getContenido());

        String coverImagePath = comentario.getFotoPerfil();
        if (coverImagePath != null && !coverImagePath.isEmpty()) {
            fileServiceClient.downloadImage(coverImagePath, new FileServiceClient.DownloadCallback() {
                @Override
                public void onSuccess(byte[] fileData, String filename) {
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(() -> {
                            Bitmap bitmap = ImageUtil.binaryToBitmap(fileData);
                            holder.imgAvatar.setImageBitmap(bitmap);
                            holder.imgAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(() -> {
                            holder.imgAvatar.setImageResource(R.drawable.ic_perfil);
                            Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_perfil);
        }

        int idLogueado = SesionUsuario.obtenerDatosUsuario(context).getIdUsuario();
        Log.d("ComentarioAdapter", "ID logueado: " + idLogueado);
        if (comentario.getIdUsuarioRegistrado() == idLogueado) {
            holder.btnEliminarComentario.setVisibility(View.VISIBLE);
        } else {
            holder.btnEliminarComentario.setVisibility(View.GONE);
        }

        holder.btnEliminarComentario.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return comentarios != null ? comentarios.size() : 0;
    }

    public void setComentarios(List<Comentario> nuevosComentarios) {
        List<Comentario> copiaComentarios = new ArrayList<>(nuevosComentarios);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ComentarioDiffCallback(this.comentarios, copiaComentarios));

        this.comentarios.clear();
        this.comentarios.addAll(copiaComentarios);
        diffResult.dispatchUpdatesTo(this);
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        ImageButton btnEliminarComentario;
        TextView txtNombreUsuario, txtFechaComentario, txtComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            btnEliminarComentario = itemView.findViewById(R.id.btn_eliminar_comentario);
            txtNombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario);
            txtFechaComentario = itemView.findViewById(R.id.txt_fecha_comentario);
            txtComentario = itemView.findViewById(R.id.txt_comentario);
        }
    }
}

package com.example.edushareandroid.model.adapter;

import android.content.Context;
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
import com.example.edushareandroid.model.base_de_datos.Documento;

import java.util.List;

public class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ViewHolder> {

    public interface OnVerMasClickListener {
        void onVerMasClick(Documento documento);
    }

    private List<Documento> documentos;
    private Context context;
    private OnVerMasClickListener listener;
    private boolean esPropietario;


    // Constructor con listener
    public ResultadoAdapter(Context context, List<Documento> documentos, OnVerMasClickListener listener) {
        this.context = context;
        this.documentos = documentos;
        this.listener = listener;
    }

    // Constructor sin listener
    public ResultadoAdapter(Context context, List<Documento> documentos, boolean esPropietario, OnVerMasClickListener listener) {
        this.context = context;
        this.documentos = documentos;
        this.esPropietario = esPropietario;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resultado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Documento doc = documentos.get(position);
        holder.titulo.setText(doc.getTitulo());
        holder.subtitulo.setText(doc.getSubtitulo());
        holder.detalles.setText(doc.getDetalles());
        holder.imagen.setImageResource(doc.getImagenId());

        // Mostrar/ocultar botón de opciones
        if (esPropietario) {
            holder.btnOpciones.setVisibility(View.VISIBLE);
            holder.btnOpciones.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(R.menu.menu_publicacion);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_eliminar) {
                        documentos.remove(position);
                        notifyItemRemoved(position);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        } else {
            holder.btnOpciones.setVisibility(View.GONE);
        }

        // Listener de Ver Más
        holder.boton.setVisibility(listener != null ? View.VISIBLE : View.GONE);
        holder.boton.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onVerMasClick(documentos.get(pos));
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return documentos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, subtitulo, detalles;
        ImageView imagen;
        ImageButton boton, btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.img_portada);
            titulo = itemView.findViewById(R.id.txt_titulo_documento);
            subtitulo = itemView.findViewById(R.id.txt_subtitulo);
            detalles = itemView.findViewById(R.id.txt_detalles);
            boton = itemView.findViewById(R.id.btn_ver_mas);
            btnOpciones = itemView.findViewById(R.id.btn_opciones);

        }
    }
}

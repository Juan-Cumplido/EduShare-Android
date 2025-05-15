package com.example.edushareandroid.model.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.home.bd.Documento;

import java.util.List;

public class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ViewHolder> {
    public interface OnVerMasClickListener {
        void onVerMasClick(Documento documento);
    }

    private List<Documento> documentos;
    private Context context;
    private OnVerMasClickListener listener;

    public ResultadoAdapter(Context context, List<Documento> documentos, OnVerMasClickListener listener) {
        this.context = context;
        this.documentos = documentos;
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
    }

    @Override
    public int getItemCount() {
        return documentos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, subtitulo, detalles;
        ImageView imagen;
        ImageButton boton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagePortada);
            titulo = itemView.findViewById(R.id.textTitulo);
            subtitulo = itemView.findViewById(R.id.textSubtitulo);
            detalles = itemView.findViewById(R.id.textDetalles);
            boton = itemView.findViewById(R.id.buttonVerMas);
            boton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onVerMasClick(documentos.get(pos));
                }
            });
        }
    }
}

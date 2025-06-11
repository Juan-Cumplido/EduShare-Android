package com.example.edushareandroid.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.bd.Message;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PROPIO = 1;
    private static final int VIEW_TYPE_AJENO = 2;

    private List<Message> mensajes;

    public ChatMessageAdapter(List<Message> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsPropio() ? VIEW_TYPE_PROPIO : VIEW_TYPE_AJENO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_PROPIO) {
            View view = inflater.inflate(R.layout.item_mensaje_propio, parent, false);
            return new MensajePropioViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_mensaje_ajeno, parent, false);
            return new MensajeAjenoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message mensaje = mensajes.get(position);

        if (holder instanceof MensajePropioViewHolder) {
            MensajePropioViewHolder viewHolder = (MensajePropioViewHolder) holder;
            viewHolder.tvContenido.setText(mensaje.getContenido());
            viewHolder.tvFecha.setText(mensaje.getFecha());
        } else {
            MensajeAjenoViewHolder viewHolder = (MensajeAjenoViewHolder) holder;
            viewHolder.tvAutor.setText(mensaje.getAutor());
            viewHolder.tvContenido.setText(mensaje.getContenido());
            viewHolder.tvFecha.setText(mensaje.getFecha());
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajePropioViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido, tvFecha;

        MensajePropioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.txt_contenido_mensaje);
            tvFecha = itemView.findViewById(R.id.txt_fecha_mensaje);
        }
    }

    static class MensajeAjenoViewHolder extends RecyclerView.ViewHolder {
        TextView tvAutor, tvContenido, tvFecha;

        MensajeAjenoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAutor = itemView.findViewById(R.id.txt_autor_mensaje);
            tvContenido = itemView.findViewById(R.id.txt_contenido_mensaje);
            tvFecha = itemView.findViewById(R.id.txt_fecha_mensaje);
        }
    }
}

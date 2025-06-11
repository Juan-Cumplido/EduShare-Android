package com.example.edushareandroid.model.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.bd.AgendaChat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<AgendaChat> lista;
    private final NavController navController;
    public ChatAdapter(List<AgendaChat> lista,NavController navController) {
        this.lista = lista;
        this.navController = navController;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_agendado, parent, false);
        return new ChatViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        AgendaChat chat = lista.get(position);
        holder.tvTitulo.setText(chat.getTitulo());
        holder.tvDescripcion.setText(chat.getDescripcion());
        holder.tvFechaHora.setText(chat.getFecha() + " " + chat.getHora());
        holder.tvUsuario.setText(chat.getUsuario());
        holder.verMas.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("titulo", chat.getTitulo());
            bundle.putString("descripcion", chat.getDescripcion());
            bundle.putString("fecha", chat.getFecha());
            bundle.putString("hora", chat.getHora());
            bundle.putString("usuario", chat.getUsuario());

            navController.navigate(R.id.action_navigation_chat_to_vistaChatFragment, bundle);
        });
    }
    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFechaHora, tvUsuario;
        ImageButton verMas;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.txt_titulo_chat);
            tvDescripcion = itemView.findViewById(R.id.txt_descripcion_chat);
            tvFechaHora = itemView.findViewById(R.id.txt_fecha_hora_chat);
            tvUsuario = itemView.findViewById(R.id.txt_usuario);
            verMas = itemView.findViewById(R.id.btn_ver_mas); // Asegúrate de que este ID exista
        }

    }
}

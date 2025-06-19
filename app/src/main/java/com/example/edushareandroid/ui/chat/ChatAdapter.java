package com.example.edushareandroid.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<AgendaChat> chatList;
    private final NavController navController;

    public ChatAdapter(List<AgendaChat> chatList, NavController navController) {
        this.chatList = chatList;
        this.navController = navController;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_agendado, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        AgendaChat chat = chatList.get(position);
        holder.tituloTextView.setText(chat.getTitulo());
        holder.descripcionTextView.setText(chat.getDescripcion());
        holder.fechaTextView.setText(chat.getFecha() + " " + chat.getHora());
        holder.usuarioTextView.setText(chat.getUsuario());

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chat.getId());
            bundle.putString("titulo", chat.getTitulo());
            bundle.putString("descripcion", chat.getDescripcion());
            bundle.putString("fecha", chat.getFecha());
            bundle.putString("hora", chat.getHora());
            bundle.putString("usuario", chat.getUsuario());
            navController.navigate(R.id.action_navigation_chat_to_vistaChatFragment, bundle);

            String chatId = chat.getId();
            String userId = String.valueOf(SesionUsuario.obtenerDatosUsuario(v.getContext()).getIdUsuario());
            String username = "Erick";

            WebSocketManager.getInstance().unirseChat(chatId, userId, username);
        });

        holder.verMasButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("IdChat", chat.getId());
            bundle.putString("titulo", chat.getTitulo());
            bundle.putString("descripcion", chat.getDescripcion());
            bundle.putString("fecha", chat.getFecha());
            bundle.putString("hora", chat.getHora());
            bundle.putString("usuario", chat.getUsuario());
            navController.navigate(R.id.action_navigation_chat_to_vistaChatFragment, bundle);

            String chatId = chat.getId();
            String idUsuario = String.valueOf(SesionUsuario.obtenerDatosUsuario(v.getContext()).getIdUsuario());
            String username = "USUARIO";

            WebSocketManager.getInstance().unirseChat(chatId, idUsuario, username);
        });
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView fechaTextView;
        TextView usuarioTextView;
        ImageButton verMasButton;

        public ChatViewHolder(View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.txt_titulo_chat);
            descripcionTextView = itemView.findViewById(R.id.txt_descripcion_chat);
            fechaTextView = itemView.findViewById(R.id.txt_fecha_hora_chat);
            usuarioTextView = itemView.findViewById(R.id.txt_usuario);
            verMasButton = itemView.findViewById(R.id.btn_ver_mas);
        }
    }
}

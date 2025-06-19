package com.example.edushareandroid.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentChatBinding;
import com.example.edushareandroid.utils.SesionUsuario;
import com.example.edushareandroid.network.websocket.WebSocketManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private final List<AgendaChat> chatList = new ArrayList<>();
    private ChatAdapter adapter;
    private static final String TAG = "ChatFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        boolean usuarioAutenticado = SesionUsuario.isUsuarioLogueado(requireContext());

        if (usuarioAutenticado) {
            binding.txtNoSesion.setVisibility(View.GONE);
            binding.txtChat.setVisibility(View.VISIBLE);
            binding.rvAgendaChats.setVisibility(View.VISIBLE);
            binding.btnProgramarChat.setVisibility(View.VISIBLE);

            configurarRecyclerView();
            obtenerChatsActivos();

            binding.btnProgramarChat.setOnClickListener(v -> {
                try {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_navigation_chat_to_programarChatFragment);
                } catch (Exception e) {
                    Log.e(TAG, "Error al navegar a ProgramarChatFragment", e);
                }
            });

        } else {
            binding.txtNoSesion.setVisibility(View.VISIBLE);
            binding.txtChat.setVisibility(View.GONE);
            binding.rvAgendaChats.setVisibility(View.GONE);
            binding.btnProgramarChat.setVisibility(View.GONE);
        }

        return root;
    }

    private void configurarRecyclerView() {
        try {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            adapter = new ChatAdapter(chatList, navController);
            binding.rvAgendaChats.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvAgendaChats.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar RecyclerView", e);
        }
    }

    private void obtenerChatsActivos() {
        try {
            String usuarioId = String.valueOf(SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario());

            WebSocketManager.getInstance().setCallback(new WebSocketManager.WebSocketCallback() {
                @Override
                public void onMessageReceived(String action, JSONObject data) {
                    if ("respuesta_chats_activos".equals(action)) {
                        parsearChatsActivos(data);
                    }
                }

                @Override
                public void onConnectionOpened() {
                    Log.d(TAG, "Conexión WebSocket ya está abierta");
                }

                @Override
                public void onConnectionClosed() {
                    Log.d(TAG, "Conexión WebSocket cerrada");
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error en WebSocket: " + error);
                }
            });

            WebSocketManager.getInstance().obtenerChatsActivos();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener chats activos", e);
        }
    }


    private void parsearChatsActivos(JSONObject data) {
        try {
            Log.d(TAG, "Parseando chats activos: " + data.toString());

            if (data.has("chats")) {
                JSONArray chatsArray = data.getJSONArray("chats");
                chatList.clear();
                for (int i = 0; i < chatsArray.length(); i++) {
                    JSONObject chatData = chatsArray.getJSONObject(i);
                    String id = chatData.optString("IdChat", "");
                    String titulo = chatData.optString("Titulo", "");
                    String descripcion = chatData.optString("Descripcion", "");
                    String fecha = chatData.optString("Fecha", "");
                    String hora = chatData.optString("Hora", "");
                    String usuario = chatData.optString("IdAutor", "Usuario desconocido");
                    if (fecha.isEmpty()) {
                        fecha = "Fecha no especificada";
                    }
                    if (hora.isEmpty()) {
                        hora = "Hora no especificada";
                    }

                    Log.d(TAG, "Chat parseado - ID: " + id + ", Título: " + titulo + ", Descripción: " + descripcion);

                    AgendaChat chat = new AgendaChat(id, titulo, descripcion, fecha, hora, usuario);
                    chatList.add(chat);
                }
                Log.d(TAG, "Total de chats agregados: " + chatList.size());

                requireActivity().runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Adapter es null al intentar actualizar");
                    }
                });
            } else {
                Log.w(TAG, "No se encontró la clave 'chats' en la respuesta");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al parsear chats activos", e);
        }
    }
}
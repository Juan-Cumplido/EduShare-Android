package com.example.edushareandroid.ui.vistachat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.databinding.FragmentVistachatBinding;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.example.edushareandroid.utils.SesionUsuario;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VistaChatFragment extends Fragment {
    private TextView txtTituloDetalle, txtDescripcionDetalle, txtFechaHoraDetalle, txtUsuarioDetalle;
    private FragmentVistachatBinding binding;
    private ChatMessageAdapter adapter;
    private List<Message> mensajes = new ArrayList<>();
    private String chatId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVistachatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtTituloDetalle = binding.txtTituloDetalle;
        txtDescripcionDetalle = binding.txtDescripcionDetalle;
        txtFechaHoraDetalle = binding.txtFechaHoraDetalle;
        txtUsuarioDetalle = binding.txtUsuarioDetalle;

        if (getArguments() != null) {
            String titulo = getArguments().getString("titulo");
            String descripcion = getArguments().getString("descripcion");
            String fecha = getArguments().getString("fecha");
            String hora = getArguments().getString("hora");
            String usuario = getArguments().getString("usuario");

            txtTituloDetalle.setText(titulo);
            txtDescripcionDetalle.setText(descripcion);
            txtFechaHoraDetalle.setText(fecha + " " + hora);
            txtUsuarioDetalle.setText(usuario);
        }

        setupChat();
        setUpWebSocketListener();

        return root;
    }


    private void setupChat() {
        mensajes.clear();

        adapter = new ChatMessageAdapter(mensajes);
        binding.rvMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMensajes.setAdapter(adapter);
        binding.rvMensajes.scrollToPosition(mensajes.size() - 1);

        binding.btnEnviarMensaje.setOnClickListener(v -> {
            String nuevoMensaje = binding.edtMensaje.getText().toString().trim();
            if (!TextUtils.isEmpty(nuevoMensaje)) {
                String id = String.valueOf(System.currentTimeMillis());
                String autor = SesionUsuario.obtenerDatosUsuario(getContext()).getNombreUsuario();
                String fecha = obtenerFechaActual();
                boolean esPropio = true;

                Message mensaje = new Message(id, autor, nuevoMensaje, fecha, esPropio);
                mensajes.add(mensaje);
                adapter.notifyItemInserted(mensajes.size() - 1);
                binding.rvMensajes.scrollToPosition(mensajes.size() - 1);
                binding.edtMensaje.setText("");

                enviarMensajeWebSocket(nuevoMensaje);
            }
        });
    }

    private void enviarMensajeWebSocket(String mensajeTexto) {
        int usuarioId = SesionUsuario.obtenerDatosUsuario(getContext()).getIdUsuario();
        String nombreUsuario = SesionUsuario.obtenerDatosUsuario(getContext()).getNombreUsuario();
        String hora = obtenerFechaActual();

        WebSocketManager.getInstance().enviarMensajeChat(chatId, nombreUsuario, hora, "", mensajeTexto);
    }

    private void setUpWebSocketListener() {
        WebSocketManager.getInstance().setCallback(new WebSocketManager.WebSocketCallback() {
            @Override
            public void onMessageReceived(String action, JSONObject data) {
                if ("mensaje_recibido".equals(action)) {
                    String autor = data.optString("NombreUsuario", "");
                    String mensaje = data.optString("Mensaje", "");
                    String fecha = data.optString("Hora", "");
                    String id = data.optString("IdMensaje", "");

                    recibirMensaje(id, autor, mensaje, fecha);
                }
            }

            @Override
            public void onConnectionOpened() {
            }

            @Override
            public void onConnectionClosed() {
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void recibirMensaje(String id, String autor, String mensaje, String fecha) {
        boolean esPropio = false;
        Message mensajeRecibido = new Message(id, autor, mensaje, fecha, esPropio);
        mensajes.add(mensajeRecibido);

        adapter.notifyItemInserted(mensajes.size() - 1);
        binding.rvMensajes.scrollToPosition(mensajes.size() - 1);
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        WebSocketManager.getInstance().startReconnections();
    }

    @Override
    public void onPause() {
        super.onPause();
        WebSocketManager.getInstance().stopReconnections();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

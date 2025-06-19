package com.example.edushareandroid.ui.vistachat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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
    private static final String TAG = "VistaChatFragment";

    private TextView txtTituloDetalle, txtDescripcionDetalle, txtFechaHoraDetalle, txtUsuarioDetalle;
    private FragmentVistachatBinding binding;
    private ChatMessageAdapter adapter;
    private List<Message> mensajes = new ArrayList<>();
    private String chatId;
    private Handler mainHandler;
    private WebSocketManager.WebSocketCallback originalCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVistachatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainHandler = new Handler(Looper.getMainLooper());

        txtTituloDetalle = binding.txtTituloDetalle;
        txtDescripcionDetalle = binding.txtDescripcionDetalle;
        txtFechaHoraDetalle = binding.txtFechaHoraDetalle;
        txtUsuarioDetalle = binding.txtUsuarioDetalle;

        if (getArguments() != null) {
            chatId = getArguments().getString("IdChat");
            String titulo = getArguments().getString("titulo");
            String descripcion = getArguments().getString("descripcion");
            String fecha = getArguments().getString("fecha");
            String hora = getArguments().getString("hora");
            String usuario = getArguments().getString("usuario");

            txtTituloDetalle.setText(titulo);
            txtDescripcionDetalle.setText(descripcion);
            txtFechaHoraDetalle.setText(fecha + " " + hora);
            txtUsuarioDetalle.setText(usuario);

            int usuarioId = SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario();
            String nombreUsuario = SesionUsuario.obtenerDatosUsuario(requireContext()).getNombreUsuario();

            Log.d(TAG, "Chat inicializado para usuario: " + nombreUsuario + " en chat: " + chatId);
        }

        setupChat();
        setUpWebSocketListener();

        if (chatId != null) {
            unirseAlChat();
        }

        return root;
    }

    private void setupChat() {
        mensajes.clear();

        adapter = new ChatMessageAdapter(mensajes);
        binding.rvMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMensajes.setAdapter(adapter);

        binding.btnEnviarMensaje.setOnClickListener(v -> {
            String nuevoMensaje = binding.edtMensaje.getText().toString().trim();
            if (!TextUtils.isEmpty(nuevoMensaje)) {
                binding.edtMensaje.setText("");

                enviarMensajeWebSocket(nuevoMensaje);

                Log.d(TAG, "Mensaje enviado al servidor: " + nuevoMensaje);
            }
        });

    }

    private void enviarMensajeWebSocket(String mensajeTexto) {
        String usuarioId = String.valueOf(SesionUsuario.obtenerDatosUsuario(getContext()).getIdUsuario());
        String nombreUsuario = SesionUsuario.obtenerDatosUsuario(getContext()).getNombreUsuario();
        String hora = obtenerFechaActual();

        WebSocketManager.getInstance().enviarMensajeChat(chatId, usuarioId, nombreUsuario, hora, "", mensajeTexto);
    }

    private void unirseAlChat() {
        String usuarioId = String.valueOf(SesionUsuario.obtenerDatosUsuario(getContext()).getIdUsuario());
        String nombreUsuario = SesionUsuario.obtenerDatosUsuario(getContext()).getNombreUsuario();

        WebSocketManager.getInstance().unirseChat(chatId, usuarioId, nombreUsuario);
        Log.d(TAG, "Uniéndose al chat: " + chatId + " como usuario: " + nombreUsuario);
    }

    private void setUpWebSocketListener() {
        WebSocketManager.WebSocketCallback chatCallback = new WebSocketManager.WebSocketCallback() {
            @Override
            public void onMessageReceived(String action, JSONObject data) {
                Log.d(TAG, "Mensaje WebSocket recibido - Acción: " + action + ", Data: " + data.toString());

                if ("mensaje_chat".equals(action) || "enviar_mensaje".equals(action)) {
                    String chatIdRecibido = data.optString("IdChat", "");

                    if (chatId != null && chatId.equals(chatIdRecibido)) {
                        String autorId = data.optString("IdUsuario", "");
                        String autor = data.optString("NombreUsuario", "Usuario");
                        String mensaje = data.optString("Mensaje", "");
                        String fecha = data.optString("Hora", "");
                        String id = data.optString("IdMensaje", String.valueOf(System.currentTimeMillis()));

                        String miUsuarioId = String.valueOf(SesionUsuario.obtenerDatosUsuario(getContext()).getIdUsuario());
                        if (!autorId.equals(miUsuarioId)) {
                            mainHandler.post(() -> recibirMensaje(id, autor, mensaje, fecha));
                        }
                    }
                }

                if ("notificacion".equals(action)) {
                    Log.d(TAG, "Notificación recibida: " + data.toString());
                }
            }

            @Override
            public void onConnectionOpened() {
                Log.d(TAG, "Conexión WebSocket abierta");
                if (chatId != null) {
                    mainHandler.postDelayed(() -> unirseAlChat(), 1000);
                }
            }

            @Override
            public void onConnectionClosed() {
                Log.d(TAG, "Conexión WebSocket cerrada");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error en WebSocket: " + error);
            }
        };

        WebSocketManager.getInstance().setCallback(chatCallback);
    }

    private void recibirMensaje(String id, String autor, String mensaje, String fecha) {
        Log.d(TAG, "Recibiendo mensaje de: " + autor + " - " + mensaje);

        boolean esPropio = false;
        Message mensajeRecibido = new Message(id, autor, mensaje, fecha, esPropio);
        mensajes.add(mensajeRecibido);

        if (adapter != null) {
            adapter.notifyItemInserted(mensajes.size() - 1);
            binding.rvMensajes.scrollToPosition(mensajes.size() - 1);
            Log.d(TAG, "Mensaje agregado al RecyclerView. Total mensajes: " + mensajes.size());
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        WebSocketManager.getInstance().startReconnections();

        setUpWebSocketListener();

        if (chatId != null) {
            mainHandler.postDelayed(() -> unirseAlChat(), 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (chatId != null) {
            String usuarioId = String.valueOf(SesionUsuario.obtenerDatosUsuario(getContext()).getIdUsuario());
            WebSocketManager.getInstance().salirChat(chatId, usuarioId);
        }

        binding = null;
    }
}
package com.example.edushareandroid.network.websocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.edushareandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private static final String SERVER_URL = "ws://192.168.1.66:8765";

    private static WebSocketManager instance;
    private WebSocket webSocket;

    public interface WebSocketCallback {
        void onMessageReceived(String action, JSONObject data);
        void onConnectionOpened();
        void onConnectionClosed();
        void onError(String error);
    }

    private WebSocketCallback callback;

    private WebSocketManager() {}

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect(WebSocketCallback callback, String usuarioId) {
        this.callback = callback;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "Conexión abierta");
                enviarMensajeConexion(usuarioId);
                if (callback != null) callback.onConnectionOpened();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Mensaje recibido: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    String accion = json.optString("accion");

                    if ("notificacion".equals(accion)) {
                        mostrarNotificacionSistema(json);
                    }

                    if (callback != null) {
                        callback.onMessageReceived(accion, json);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error al parsear JSON", e);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "Error en conexión WebSocket", t);
                if (callback != null) callback.onError(t.getMessage());
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "Conexión cerrándose: " + reason);
                if (callback != null) callback.onConnectionClosed();
            }
        });
    }

    // Método para mostrar la notificación
    public void mostrarNotificacionSistema(JSONObject json) {
        try {
            String titulo = json.optString("Titulo", "Notificación");
            String mensaje = json.optString("Mensaje", "");

            Context context = MyApplication.getContext();

            // Crear el canal de notificación (si no está creado aún)
            NotificationHelper.crearCanal(context);

            // Crear la notificación con un icono, título y mensaje
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)  // Asegúrate de tener este ícono en el drawable
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)  // Específicamente para que sea una notificación emergente
                    .setAutoCancel(true)  // Se cancela cuando el usuario la toca
                    .setDefaults(NotificationCompat.DEFAULT_ALL)  // Asegura que suene y se muestre adecuadamente
                    .setVibrate(new long[]{0, 500, 1000})  // Agrega vibración si es necesario
                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);  // Sonido por defecto

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Utiliza un ID único para cada notificación
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar notificación", e);
        }
    }

    // Método para enviar mensajes a través del WebSocket
    public void enviar(JSONObject mensaje) {
        if (webSocket != null) {
            webSocket.send(mensaje.toString());
        }
    }

    public void cerrarConexion() {
        if (webSocket != null) {
            webSocket.close(1000, "Cerrado por el usuario");
        }
    }

    private void enviarMensajeConexion(String usuarioId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "conectar");
            obj.put("UsuarioId", usuarioId);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al enviar mensaje de conexión", e);
        }
    }

    public void desconectar(String usuarioId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "desconectar");
            obj.put("UsuarioId", usuarioId);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al enviar desconexión", e);
        }
    }

    public void enviarNotificacion(JSONObject mensaje) {
        if (webSocket != null) {
            webSocket.send(mensaje.toString());
        }
    }

    public void crearChat(JSONObject chatData) {
        try {
            chatData.put("accion", "crear_chat");
            enviar(chatData);
        } catch (JSONException e) {
            Log.e(TAG, "Error al crear chat", e);
        }
    }

    public void unirseChat(String idChat, String idUsuario, String nombreUsuario) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "unirse_chat");
            obj.put("IdChat", idChat);
            obj.put("IdUsuario", idUsuario);
            obj.put("NombreUsuario", nombreUsuario);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al unirse a chat", e);
        }
    }

    public void salirChat(String idChat, String idUsuario) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "salir_chat");
            obj.put("IdChat", idChat);
            obj.put("IdUsuario", idUsuario);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al salir del chat", e);
        }
    }

    public void eliminarMensaje(String idChat, String idMensaje, String idUsuario, String nombreUsuario) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "eliminar_mensaje");
            obj.put("IdChat", idChat);
            obj.put("IdMensaje", idMensaje);
            obj.put("IdUsuario", idUsuario);
            obj.put("NombreUsuario", nombreUsuario);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al eliminar mensaje", e);
        }
    }

    public void enviarMensajeChat(String idChat, String nombreUsuario, String hora, String fotoPerfil, String mensaje) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "enviar_mensaje");
            obj.put("IdChat", idChat);
            obj.put("NombreUsuario", nombreUsuario);
            obj.put("Hora", hora);
            obj.put("FotoPerfil", fotoPerfil);
            obj.put("Mensaje", mensaje);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al enviar mensaje de chat", e);
        }
    }

    public void enviarNotificacionAccion(String titulo, String mensaje, String tipo, String fecha, String usuarioDestinoId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "notificacion");
            obj.put("Titulo", titulo);
            obj.put("Mensaje", mensaje);
            obj.put("Tipo", tipo);
            obj.put("FechaCreacion", fecha);
            obj.put("UsuarioDestinoId", usuarioDestinoId);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al enviar notificación de acción", e);
        }
    }

    public void obtenerChatsActivos() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "obtener_chats_activos");
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al obtener chats activos", e);
        }
    }

    public void obtenerMisChats(String idAutor) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("accion", "obtener_mis_chats");
            obj.put("IdAutor", idAutor);
            enviar(obj);
        } catch (JSONException e) {
            Log.e(TAG, "Error al obtener mis chats", e);
        }
    }
}

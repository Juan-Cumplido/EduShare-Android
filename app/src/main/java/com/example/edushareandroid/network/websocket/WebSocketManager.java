package com.example.edushareandroid.network.websocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
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
    private static final long RECONNECT_DELAY = 5000; // 5 segundos
    private static final String PREF_USER_ID = "user_id";

    private static WebSocketManager instance;
    private WebSocket webSocket;
    private Handler reconnectHandler;
    private boolean shouldReconnect = true;
    private String currentUserId;
    private Context applicationContext;

    public interface WebSocketCallback {
        void onMessageReceived(String action, JSONObject data);
        void onConnectionOpened();
        void onConnectionClosed();
        void onError(String error);
    }

    private WebSocketCallback callback;

    private WebSocketManager() {
        reconnectHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void initialize(Context context, String userId) {
        this.applicationContext = context.getApplicationContext();
        this.currentUserId = userId;

        SharedPreferences prefs = context.getSharedPreferences("websocket_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_USER_ID, userId).apply();

        NotificationHelper.crearCanal(applicationContext);

        connectWithReconnect();
    }

    private void connectWithReconnect() {
        if (currentUserId == null) {
            if (applicationContext != null) {
                SharedPreferences prefs = applicationContext.getSharedPreferences("websocket_prefs", Context.MODE_PRIVATE);
                currentUserId = prefs.getString(PREF_USER_ID, null);
            }

            if (currentUserId == null) {
                Log.e(TAG, "No se puede conectar: userId es null");
                return;
            }
        }

        shouldReconnect = true;
        connect(new WebSocketCallback() {
            @Override
            public void onMessageReceived(String action, JSONObject data) {
                if ("notificacion".equals(action)) {
                    mostrarNotificacionSistema(data);
                }
                if (callback != null) {
                    callback.onMessageReceived(action, data);
                }
            }

            @Override
            public void onConnectionOpened() {
                Log.d(TAG, "Conexión WebSocket establecida y usuario conectado");
                if (callback != null) {
                    callback.onConnectionOpened();
                }
            }

            @Override
            public void onConnectionClosed() {
                Log.d(TAG, "Conexión WebSocket cerrada");
                if (callback != null) {
                    callback.onConnectionClosed();
                }


                if (shouldReconnect) {
                    scheduleReconnect();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error en WebSocket: " + error);
                if (callback != null) {
                    callback.onError(error);
                }

                if (shouldReconnect) {
                    scheduleReconnect();
                }
            }
        }, currentUserId);
    }
    public void setCallback(WebSocketCallback callback) {
        this.callback = callback;
    }

    private void scheduleReconnect() {
        Log.d(TAG, "Programando reconexión en " + RECONNECT_DELAY + "ms");
        reconnectHandler.postDelayed(() -> {
            if (shouldReconnect && (webSocket == null || !isConnected())) {
                Log.d(TAG, "Intentando reconectar...");
                connectWithReconnect();
            }
        }, RECONNECT_DELAY);
    }

    public boolean isConnected() {
        return webSocket != null;
    }

    public void connect(WebSocketCallback callback, String usuarioId) {
        this.callback = callback;
        this.currentUserId = usuarioId;

        if (webSocket != null) {
            webSocket.close(1000, "Reconectando");
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .pingInterval(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "Conexión abierta para usuario: " + usuarioId);
                enviarMensajeConexion(usuarioId);
                if (WebSocketManager.this.callback != null) {
                    WebSocketManager.this.callback.onConnectionOpened();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Mensaje recibido: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    String accion = json.optString("accion");

                    if ("notificacion".equals(accion)) {
                        Log.d(TAG, "Procesando notificación recibida");
                        mostrarNotificacionSistema(json);
                    }

                    if (WebSocketManager.this.callback != null) {
                        WebSocketManager.this.callback.onMessageReceived(accion, json);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error al parsear JSON", e);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "Error en conexión WebSocket", t);
                WebSocketManager.this.webSocket = null; // Marcar como desconectado
                if (WebSocketManager.this.callback != null) {
                    WebSocketManager.this.callback.onError(t.getMessage());
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "Conexión cerrándose: " + reason);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "Conexión cerrada: " + reason);
                WebSocketManager.this.webSocket = null;
                if (WebSocketManager.this.callback != null) {
                    WebSocketManager.this.callback.onConnectionClosed();
                }
            }
        });
    }


    @SuppressLint("MissingPermission")
    public void mostrarNotificacionSistema(JSONObject json) {
        try {
            String titulo = json.optString("Titulo", "Notificación");
            String mensaje = json.optString("Mensaje", "");

            Log.d(TAG, "Mostrando notificación: " + titulo + " - " + mensaje);

            if (applicationContext == null) {
                applicationContext = MyApplication.getContext();
            }

            NotificationHelper.crearCanal(applicationContext);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

            Log.d(TAG, "Notificación mostrada exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar notificación", e);
        }
    }

    public void stopReconnections() {
        shouldReconnect = false;
        reconnectHandler.removeCallbacksAndMessages(null);
    }

    public void startReconnections() {
        shouldReconnect = true;
        if (!isConnected()) {
            connectWithReconnect();
        }
    }

    public void enviar(JSONObject mensaje) {
        if (webSocket != null) {
            webSocket.send(mensaje.toString());
        } else {
            Log.w(TAG, "Intentando enviar mensaje pero WebSocket no está conectado");
        }
    }

    public void cerrarConexion() {
        shouldReconnect = false;
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
            Log.d(TAG, "Mensaje de conexión enviado para usuario: " + usuarioId);
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

    public void unirseChat(String idChat, int idUsuario, String nombreUsuario) {
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

    public void salirChat(String idChat, int idUsuario) {
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
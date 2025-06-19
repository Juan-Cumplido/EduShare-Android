package com.example.edushareandroid.network.websocket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class NotificationHelper {

    public static final String CHANNEL_ID = "canal_notificaciones_edushare";

    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Notificaciones EduShare";
            String descripcion = "Canal para notificaciones generales de EduShare";
            int importancia = NotificationManager.IMPORTANCE_HIGH;  // Prioridad alta para emergentes

            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            // Se puede añadir otras configuraciones aquí si es necesario
            canal.enableLights(true);
            canal.setLightColor(Color.RED);
            canal.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }
}

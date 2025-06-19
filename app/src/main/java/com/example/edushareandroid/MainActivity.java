package com.example.edushareandroid;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.edushareandroid.databinding.ActivityMainBinding;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;  // Código de solicitud para permisos
    private ActivityMainBinding binding;
    private BroadcastReceiver sesionExpiradaReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_chat,
                R.id.navigation_notifications,
                R.id.navigation_perfil,
                R.id.navigation_usuarios)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Verificar permisos y solicitarlos si no están concedidos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        } else {
            // Si ya tiene el permiso, puedes mostrar la notificación
            mostrarNotificacionSistema();  // Llamar al método para mostrar notificaciones
        }

        // Registrar el receiver para escuchar la expiración de sesión
        sesionExpiradaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Sesión caducada")
                        .setMessage("Tu sesión ha caducado. Inicia sesión nuevamente. Mientras tanto, puedes seguir explorando los documentos.")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", (dialog, which) -> {
                            // Ir al home
                            NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                            navController.navigate(R.id.navigation_home);
                        })
                        .show();
            }
        };

        // Registrar el BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(sesionExpiradaReceiver,
                new IntentFilter("SESION_EXPIRADA"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sesionExpiradaReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    // Manejar la respuesta a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {  // Verificar el código de la solicitud
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes mostrar la notificación
                mostrarNotificacionSistema();  // Llamar al método para mostrar notificaciones
            } else {
                // Permiso rechazado
                Log.e("MainActivity", "Permiso de notificación no concedido.");
            }
        }
    }

    // Mostrar una notificación, llamando al método de WebSocketManager
    private void mostrarNotificacionSistema() {
        // Verificar si el permiso está concedido antes de intentar mostrar la notificación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // Aquí es donde puedes llamar al WebSocketManager para mostrar la notificación cuando ya tienes el permiso
            JSONObject mensajeJson = new JSONObject();
            // Puedes llenar 'mensajeJson' con los datos que desees
            WebSocketManager.getInstance().mostrarNotificacionSistema(mensajeJson);
        } else {
            Log.e("MainActivity", "Permiso de notificación no concedido.");
        }
    }
}

package com.example.edushareandroid.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;
import com.example.edushareandroid.ui.crearcuenta.CreateAccountActivity;
import com.example.edushareandroid.ui.recuperarcontrasenia.RecoverypasswordActivity;
import com.example.edushareandroid.utils.HashUtil;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.example.edushareandroid.utils.SesionUsuario;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etIdentificador, etPassword;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etIdentificador = findViewById(R.id.edt_correo);
        etPassword = findViewById(R.id.edt_contraseña);
        ImageView imageViewTogglePassword = findViewById(R.id.img_alternar_contraseña);
        Button loginButton = findViewById(R.id.btn_iniciar_sesion);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        imageViewTogglePassword.setOnClickListener(v -> togglePasswordVisibility(etPassword, imageViewTogglePassword));

        loginButton.setOnClickListener(v -> {
            String identificador = etIdentificador.getText().toString().trim();
            String contrasenia = etPassword.getText().toString().trim();

            if (identificador.isEmpty() || contrasenia.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String contraseniaHash = HashUtil.sha256(contrasenia);
            loginViewModel.login(identificador, contraseniaHash);
        });

        loginViewModel.getLoginResult().observe(this, response -> {
            if (!response.isError()) {
                loginViewModel.manejarRespuestaLogin(response, this);

                Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

                UsuarioData usuario = response.getDatos();
                int idUsuario = usuario.getIdUsuario();
                String nombreUsuario = usuario.getNombre();

                getSharedPreferences("sesion", MODE_PRIVATE)
                        .edit()
                        .putString("usuario_id", String.valueOf(idUsuario))
                        .apply();

                SesionUsuario.guardarNombreUsuario(this, nombreUsuario);

                WebSocketManager.getInstance().connect(new WebSocketManager.WebSocketCallback() {
                    @Override
                    public void onMessageReceived(String action, JSONObject data) {
                        Log.d("WebSocket", "Acción: " + action + " - Datos: " + data.toString());
                    }

                    @Override
                    public void onConnectionOpened() {
                        Log.d("WebSocket", "Conexión WebSocket establecida.");
                    }

                    @Override
                    public void onConnectionClosed() {
                        Log.d("WebSocket", " Conexión WebSocket cerrada.");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("WebSocket", "Error en WebSocket: " + error);
                    }
                }, String.valueOf(idUsuario));

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, response.getMensaje(), Toast.LENGTH_LONG).show();
            }
        });


        loginViewModel.getErrorMessage().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btn_olvido_contraseña).setOnClickListener(v ->
                startActivity(new Intent(this, RecoverypasswordActivity.class))
        );

        findViewById(R.id.btn_registrar).setOnClickListener(v ->
                startActivity(new Intent(this, CreateAccountActivity.class))
        );
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed);
        }
        editText.setSelection(editText.getText().length());
    }
}

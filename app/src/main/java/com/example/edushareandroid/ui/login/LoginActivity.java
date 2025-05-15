package com.example.edushareandroid.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;
import com.example.edushareandroid.ui.createacount.CreateAcountActivity;
import com.example.edushareandroid.ui.recoverypassword.RecoverypasswordActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button forgotPasswordButton = findViewById(R.id.ForgotPasword);
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoverypasswordActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(v -> {
            // Aquí podrías validar campos de correo/contraseña antes de continuar

            // Navegar a la pantalla principal (MainActivity)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Opcional: cerrar LoginActivity para que no se regrese con botón atrás
            finish();
        });

        Button registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(v ->{
            Intent intent = new Intent(LoginActivity.this, CreateAcountActivity.class );
            startActivity(intent);
        });

    }
}
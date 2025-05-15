package com.example.edushareandroid.ui.createacount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;

public class CreateAcountActivity extends AppCompatActivity {

    private Button buttonCancelar;
    private Button buttonCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacount);

        // Referencias a los botones
        buttonCancelar = findViewById(R.id.buttonCancelar);
        buttonCrearCuenta = findViewById(R.id.buttonCrearCuenta);

        // Cancelar registro: cerrar esta actividad
        buttonCancelar.setOnClickListener(v -> finish());

        // Crear cuenta: iniciar MainActivity
        buttonCrearCuenta.setOnClickListener(v -> {
            // Aquí podrías validar los datos antes de continuar si lo deseas
            Intent intent = new Intent(CreateAcountActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra la pantalla de registro
        });
    }
}

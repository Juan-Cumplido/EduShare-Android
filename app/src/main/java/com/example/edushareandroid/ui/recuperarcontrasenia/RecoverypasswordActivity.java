package com.example.edushareandroid.ui.recuperarcontrasenia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.example.edushareandroid.R;
import com.example.edushareandroid.utils.HashUtil;

public class RecoverypasswordActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextCodigo, editTextNuevaContraseña;
    private Group groupVerificacion;
    private CardView cardVerificacion;
    private Button btnRecuperar, btnAceptarVerificacion, btnVolverCodigo;

    private RecoveryPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recoverypassword);

        editTextEmail = findViewById(R.id.edt_correo_electronico);
        editTextCodigo = findViewById(R.id.edt_codigo_verificacion);
        editTextNuevaContraseña = findViewById(R.id.edt_nueva_contraseña);

        groupVerificacion = findViewById(R.id.grupo_verificacion);
        cardVerificacion = findViewById(R.id.card_verificacion);

        btnRecuperar = findViewById(R.id.btn_recuperar_contraseña);
        btnAceptarVerificacion = findViewById(R.id.btn_aceptar_verificacion);
        btnVolverCodigo = findViewById(R.id.btn_volver_codigo);

        viewModel = new ViewModelProvider(this).get(RecoveryPasswordViewModel.class);

        setupObservers();

        btnRecuperar.setOnClickListener(v -> {
            String correo = editTextEmail.getText().toString().trim();
            if (correo.isEmpty() || !correo.contains("@")) {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.recuperarContrasena(correo);
        });

        btnAceptarVerificacion.setOnClickListener(v -> {
            String codigo = editTextCodigo.getText().toString().trim();
            String nueva = editTextNuevaContraseña.getText().toString().trim();

            if (codigo.isEmpty()) {
                Toast.makeText(this, "Ingrese el código enviado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nueva.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            String correo = editTextEmail.getText().toString().trim();
            String nuevaHash = HashUtil.sha256(nueva);

            viewModel.cambiarContrasena(correo, codigo, nuevaHash);
        });

        btnVolverCodigo.setOnClickListener(v -> finish());
    }

    private void setupObservers() {
        viewModel.getRecuperarContrasenaResponse().observe(this, response -> {
            if (response == null) {
                Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!response.isError()) {
                groupVerificacion.setVisibility(View.VISIBLE);
                cardVerificacion.setVisibility(View.VISIBLE);
                editTextEmail.setEnabled(false);
                btnRecuperar.setEnabled(false);
                editTextCodigo.setText("");
                editTextNuevaContraseña.setText("");
                editTextCodigo.requestFocus();
                Toast.makeText(this, "Código enviado al correo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: " + response.getMensaje(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getCambiarContrasenaResponse().observe(this, response -> {
            if (response == null) {
                Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!response.isError()) {
                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + response.getMensaje(), Toast.LENGTH_LONG).show();
                editTextCodigo.setText("");
                editTextNuevaContraseña.setText("");
                editTextCodigo.requestFocus();
            }
        });
    }
}

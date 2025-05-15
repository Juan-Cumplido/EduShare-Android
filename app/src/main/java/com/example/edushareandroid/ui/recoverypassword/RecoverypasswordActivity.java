package com.example.edushareandroid.ui.recoverypassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import com.example.edushareandroid.R;

public class RecoverypasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextCodigo;
    private EditText editTextNuevaContraseña;
    private Group groupCodigo;
    private Group groupNuevaContraseña;
    private Group primerGrupo;
    private Button btnRecuperar;
    private Button btnAceptarCodigo;
    private Button btnCancelarCodigo;
    private Button btnAceptarContraseña;
    private Button btnVolver;
    private Button btnVolverCodigo;

    private final String codigoSimulado = "12345"; // simulamos que se envía este código al correo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recoverypassword);

        // Inicialización de vistas
        editTextEmail = findViewById(R.id.editTextEmailAddress);
        editTextCodigo = findViewById(R.id.editTextText);
        editTextNuevaContraseña = findViewById(R.id.editTextNuevContraseña);

        groupCodigo = findViewById(R.id.groupRecovery);
        primerGrupo = findViewById(R.id.primergroup);
        groupNuevaContraseña = findViewById(R.id.groupNewPassword);

        btnRecuperar = findViewById(R.id.btnRecuprarContraseña);
        btnAceptarCodigo = findViewById(R.id.buttonAcept);
        btnCancelarCodigo = findViewById(R.id.butonCancel);
        btnAceptarContraseña = findViewById(R.id.buttonAceptarContraseña);
        btnVolver = findViewById(R.id.butonVolver);
        btnVolverCodigo = findViewById(R.id.btnVolverCodigo);

        // Botón Recuperar
        btnRecuperar.setOnClickListener(v -> {
            String correo = editTextEmail.getText().toString().trim();
            if (!correo.isEmpty() && correo.contains("@")) {
                groupCodigo.setVisibility(View.VISIBLE);
                primerGrupo.setVisibility(View.GONE);
                enableGroup(groupCodigo, true);
                btnRecuperar.setEnabled(false);
                editTextEmail.setEnabled(false);
                // Aquí normalmente se enviaría el código por correo
            }
        });

        // Botón Aceptar Código
        btnAceptarCodigo.setOnClickListener(v -> {
            String codigoIngresado = editTextCodigo.getText().toString().trim();
            if (codigoIngresado.equals(codigoSimulado)) {
                groupCodigo.setVisibility(View.GONE);
                groupNuevaContraseña.setVisibility(View.VISIBLE);
                enableGroup(groupCodigo, false);
                enableGroup(groupNuevaContraseña, true);
            }
        });

        // Botón Cancelar Código
        btnCancelarCodigo.setOnClickListener(v -> {
            groupCodigo.setVisibility(View.GONE);
            primerGrupo.setVisibility(View.VISIBLE);
            enableGroup(groupCodigo, false);
            editTextEmail.setEnabled(true);
            btnRecuperar.setEnabled(true);
        });

        // Botón Aceptar Nueva Contraseña
        btnAceptarContraseña.setOnClickListener(v -> {
            String nuevaContra = editTextNuevaContraseña.getText().toString();
            if (nuevaContra.length() >= 5) {
                // Aquí deberías guardar la nueva contraseña en el sistema o enviarla a backend
                finish(); // Cerramos la actividad tras actualizar la contraseña
            }
        });

        // Botón Volver
        btnVolver.setOnClickListener(v -> {
            groupNuevaContraseña.setVisibility(View.GONE);
            enableGroup(groupNuevaContraseña, false);
            editTextEmail.setEnabled(true);
            btnRecuperar.setEnabled(true);
        });

        // Botón volver (desde código)
        btnVolverCodigo.setOnClickListener(v -> finish());
    }

    private void enableGroup(Group group, boolean enabled) {
        int[] ids = group.getReferencedIds();
        for (int id : ids) {
            View v = findViewById(id);
            if (v != null) v.setEnabled(enabled);
        }
    }
}

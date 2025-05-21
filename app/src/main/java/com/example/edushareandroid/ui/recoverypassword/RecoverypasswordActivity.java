package com.example.edushareandroid.ui.recoverypassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.ChangePasswordRequest;
import com.example.edushareandroid.model.base_de_datos.RecoveryRequest;
import com.example.edushareandroid.network.ApiResponse;
import com.example.edushareandroid.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverypasswordActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextCodigo, editTextNuevaContraseña;
    private Group groupCodigo, groupNuevaContraseña, primerGrupo;
    private Button btnRecuperar, btnAceptarCodigo, btnCancelarCodigo, btnAceptarContraseña, btnVolver, btnVolverCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recoverypassword);

        // Inicializar vistas
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

        // Paso 1: Solicitar código de recuperación
        btnRecuperar.setOnClickListener(v -> {
            String correo = editTextEmail.getText().toString().trim();
            if (!correo.isEmpty() && correo.contains("@")) {
                RecoveryRequest request = new RecoveryRequest(correo);
                RetrofitClient.getApiService().recuperarContrasena(request).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        ApiResponse apiResponse = response.body();

                        if (response.isSuccessful() && apiResponse != null && !apiResponse.isError()) {
                            groupCodigo.setVisibility(View.VISIBLE);
                            primerGrupo.setVisibility(View.GONE);
                            enableGroup(groupCodigo, true);
                            btnRecuperar.setEnabled(false);
                            editTextEmail.setEnabled(false);
                            Toast.makeText(RecoverypasswordActivity.this, "Código enviado al correo", Toast.LENGTH_SHORT).show();
                        } else {
                            String mensajeError = (apiResponse != null) ? apiResponse.getMensaje() : "Error inesperado del servidor";
                            Toast.makeText(RecoverypasswordActivity.this, "Error: " + mensajeError, Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RecoverypasswordActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
            }
        });

        // Paso 2: Validar código ingresado
        btnAceptarCodigo.setOnClickListener(v -> {
            String codigoIngresado = editTextCodigo.getText().toString().trim();
            if (!codigoIngresado.isEmpty()) {
                groupCodigo.setVisibility(View.GONE);
                groupNuevaContraseña.setVisibility(View.VISIBLE);
                enableGroup(groupCodigo, false);
                enableGroup(groupNuevaContraseña, true);
            } else {
                Toast.makeText(this, "Ingrese el código enviado", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancelar código y volver al inicio
        btnCancelarCodigo.setOnClickListener(v -> {
            groupCodigo.setVisibility(View.GONE);
            primerGrupo.setVisibility(View.VISIBLE);
            enableGroup(groupCodigo, false);
            editTextEmail.setEnabled(true);
            btnRecuperar.setEnabled(true);
        });

        // Paso 3: Cambiar contraseña
        btnAceptarContraseña.setOnClickListener(v -> {
            String correo = editTextEmail.getText().toString().trim();
            String codigo = editTextCodigo.getText().toString().trim();
            String nuevaContra = editTextNuevaContraseña.getText().toString();

            if (nuevaContra.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            ChangePasswordRequest request = new ChangePasswordRequest(correo, codigo, nuevaContra);
            RetrofitClient.getApiService().cambiarContrasena(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    ApiResponse apiResponse = response.body();

                    if (response.isSuccessful() && apiResponse != null && !apiResponse.isError()) {
                        Toast.makeText(RecoverypasswordActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                        finish(); // Cierra la actividad
                    } else {
                        String mensajeError = (apiResponse != null) ? apiResponse.getMensaje() : "Error inesperado";
                        Toast.makeText(RecoverypasswordActivity.this, "Error: " + mensajeError, Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(RecoverypasswordActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Botón para volver desde pantalla de nueva contraseña
        btnVolver.setOnClickListener(v -> {
            groupNuevaContraseña.setVisibility(View.GONE);
            enableGroup(groupNuevaContraseña, false);
            editTextEmail.setEnabled(true);
            btnRecuperar.setEnabled(true);
        });

        // Botón para cerrar desde pantalla del código
        btnVolverCodigo.setOnClickListener(v -> finish());
    }

    // Habilita o deshabilita los elementos dentro de un Group
    private void enableGroup(Group group, boolean enabled) {
        int[] ids = group.getReferencedIds();
        for (int id : ids) {
            View v = findViewById(id);
            if (v != null) v.setEnabled(enabled);
        }
    }
}

package com.example.edushareandroid.ui.createacount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;
import com.example.edushareandroid.model.home.bd.UsuarioRegistro;
import com.example.edushareandroid.network.ApiResponse;
import com.example.edushareandroid.network.ApiService;
import com.example.edushareandroid.network.RetrofitClient;
import com.example.edushareandroid.utils.HashUtil;
import com.example.edushareandroid.utils.ValidationUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAcountActivity extends AppCompatActivity {

    private EditText editTextCorreo, editTextContrasenia, editTextUsuario;
    private EditText editTextNombre, editTextPrimerApellido, editTextSegundoApellido;
    private EditText editTextInstitucion, editTextCarrera, editTextNivelEducativo;
    private Button buttonCancelar, buttonCrearCuenta;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private ImageView imageViewFotoPerfil;
    private Button buttonSeleccionarFoto;
    private Bitmap imagenSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacount);

        // Referencias de UI
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContrasenia = findViewById(R.id.editTextPassword);
        editTextUsuario = findViewById(R.id.editTextUsuario);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPrimerApellido = findViewById(R.id.editTextPrimerApellido);
        editTextSegundoApellido = findViewById(R.id.editTextSegundoApellido);

        editTextInstitucion = findViewById(R.id.editTextInstitucionEducativa);
        editTextCarrera = findViewById(R.id.editTextCarrera);
        editTextNivelEducativo = findViewById(R.id.editTextNivelEducativo);

        buttonCancelar = findViewById(R.id.buttonCancelar);
        buttonCrearCuenta = findViewById(R.id.buttonCrearCuenta);

        buttonCancelar.setOnClickListener(v -> {
            limpiarCampos();
            finish(); // Cierra la actividad después de limpiar los campos
        });
        imageViewFotoPerfil = findViewById(R.id.imageViewFotoPerfil);
        buttonSeleccionarFoto = findViewById(R.id.buttonSeleccionarImagen);

        buttonSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });


        buttonCrearCuenta.setOnClickListener(v -> {
            if (validarCampos()) {
                String correo = editTextCorreo.getText().toString();
                String password = editTextContrasenia.getText().toString();
                String passwordHash = HashUtil.sha256(password);

                String nombreUsuario = editTextUsuario.getText().toString();
                String nombre = editTextNombre.getText().toString();
                String primerApellido = editTextPrimerApellido.getText().toString();
                String segundoApellido = editTextSegundoApellido.getText().toString();

                String institucion = editTextInstitucion.getText().toString();
                String carrera = editTextCarrera.getText().toString();
                String nivel = editTextNivelEducativo.getText().toString();

                // Por ahora usamos un ID de institución fijo (esto lo cambiaremos después)
                int idInstitucion = 1;

                UsuarioRegistro nuevoUsuario = new UsuarioRegistro(
                        correo,
                        passwordHash,
                        nombreUsuario,
                        nombre,
                        primerApellido,
                        segundoApellido,
                        idInstitucion
                );

                ApiService apiService = RetrofitClient.getApiService();
                apiService.registrarUsuario(nuevoUsuario).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (!apiResponse.isError()) {
                                Toast.makeText(CreateAcountActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                                // Ir a Login (MainActivity)
                                Intent intent = new Intent(CreateAcountActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(CreateAcountActivity.this, "Error: " + apiResponse.getMensaje(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(CreateAcountActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(CreateAcountActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean validarCampos() {
        String correo = editTextCorreo.getText().toString();
        String password = editTextContrasenia.getText().toString();
        String nombreUsuario = editTextUsuario.getText().toString();

        String nombre = editTextNombre.getText().toString();
        String primerApellido = editTextPrimerApellido.getText().toString();
        String segundoApellido = editTextSegundoApellido.getText().toString();

        String institucion = editTextInstitucion.getText().toString();
        String carrera = editTextCarrera.getText().toString();
        String nivel = editTextNivelEducativo.getText().toString();

        if (!ValidationUtil.isValidCorreo(correo)) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            Toast.makeText(this, "La contraseña debe tener entre 6 y 300 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombreUsuario(nombreUsuario)) {
            Toast.makeText(this, "Nombre de usuario inválido (máx 15 caracteres)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombre(nombre) || !ValidationUtil.isValidApellido(primerApellido)) {
            Toast.makeText(this, "Nombre o primer apellido inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidInstitucion(institucion, carrera, nivel)) {
            Toast.makeText(this, "Datos de institución inválidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        editTextCorreo.setText("");
        editTextContrasenia.setText("");
        editTextUsuario.setText("");
        editTextNombre.setText("");
        editTextPrimerApellido.setText("");
        editTextSegundoApellido.setText("");
        editTextInstitucion.setText("");
        editTextCarrera.setText("");
        editTextNivelEducativo.setText("");
    }
}

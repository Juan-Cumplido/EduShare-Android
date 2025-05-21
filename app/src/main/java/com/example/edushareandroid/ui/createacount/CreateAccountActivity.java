package com.example.edushareandroid.ui.createacount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;
import com.example.edushareandroid.utils.HashUtil;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.ValidationUtil;

import java.io.IOException;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editTextCorreo, editTextContrasenia, editTextUsuario;
    private EditText editTextNombre, editTextPrimerApellido, editTextSegundoApellido;
    private EditText editTextInstitucion, editTextCarrera, editTextNivelEducativo;
    private ImageView imageViewFotoPerfil;
    private Bitmap imagenSeleccionada;
    private static final int REQUEST_SELECT_IMAGE = 1;

    private final CreateAccountViewModel viewModel = new CreateAccountViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacount);

        // Referencias UI
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContrasenia = findViewById(R.id.editTextPassword);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPrimerApellido = findViewById(R.id.editTextPrimerApellido);
        editTextSegundoApellido = findViewById(R.id.editTextSegundoApellido);
        editTextInstitucion = findViewById(R.id.editTextInstitucionEducativa);
        editTextCarrera = findViewById(R.id.editTextCarrera);
        editTextNivelEducativo = findViewById(R.id.editTextNivelEducativo);
        imageViewFotoPerfil = findViewById(R.id.imageViewFotoPerfil);
        ImageView imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword);
        ImageView imageViewToggleConfirmPassword = findViewById(R.id.imageViewToggleConfirmPassword);

        imageViewTogglePassword.setOnClickListener(v -> togglePasswordVisibility(editTextContrasenia, imageViewTogglePassword));
        imageViewToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(findViewById(R.id.editTextConfirmPassword), imageViewToggleConfirmPassword));


        Button buttonCrearCuenta = findViewById(R.id.buttonCrearCuenta);
        Button buttonCancelar = findViewById(R.id.buttonCancelar);

        buttonCancelar.setOnClickListener(v -> {
            limpiarCampos();
            finish();
        });
        Button btnSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagen);
        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });


        buttonCrearCuenta.setOnClickListener(v -> crearCuenta());

        observarViewModel();
    }
    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Mostrar contraseña
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open);
        } else {
            // Ocultar contraseña
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed);
        }
        editText.setSelection(editText.getText().length()); // Mantener el cursor al final
    }

    private void observarViewModel() {
        viewModel.getRegistroExitoso().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, mensaje -> {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        });
    }

    private void crearCuenta() {
        String correo = editTextCorreo.getText().toString().trim();
        String password = editTextContrasenia.getText().toString();
        String confirmarPassword = ((EditText) findViewById(R.id.editTextConfirmPassword)).getText().toString();
        String usuario = editTextUsuario.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();
        String apellido1 = editTextPrimerApellido.getText().toString().trim();
        String apellido2 = editTextSegundoApellido.getText().toString().trim();
        String institucion = editTextInstitucion.getText().toString().trim();
        String carrera = editTextCarrera.getText().toString().trim();
        String nivel = editTextNivelEducativo.getText().toString().trim();

        if (!ValidationUtil.noEstaVacio(correo, password, confirmarPassword, usuario, nombre, apellido1, institucion, carrera, nivel)) {
            Toast.makeText(this, "Todos los campos obligatorios deben estar completos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidCorreo(correo)) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidNombreUsuario(usuario)) {
            Toast.makeText(this, "El nombre de usuario debe tener entre 4 y 15 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidNombre(nombre)) {
            Toast.makeText(this, "Nombre inválido (solo letras y espacios, máximo 30)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidApellido(apellido1)) {
            Toast.makeText(this, "Primer apellido inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!apellido2.isEmpty() && !ValidationUtil.isValidApellido(apellido2)) {
            Toast.makeText(this, "Segundo apellido inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtil.isValidInstitucion(institucion, carrera, nivel)) {
            Toast.makeText(this, "Datos de institución no válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagenBase64 = (imagenSeleccionada != null)
                ? ImageUtil.bitmapToBase64(imagenSeleccionada)
                : "";

        UsuarioRegistro usuarioRegistro = new UsuarioRegistro(
                correo,
                HashUtil.sha256(password),
                usuario,
                nombre,
                apellido1,
                apellido2,
                1,
                imagenBase64
        );

        viewModel.registrarUsuario(usuarioRegistro);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                imagenSeleccionada = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageViewFotoPerfil.setImageBitmap(imagenSeleccionada);
            } catch (IOException e) {
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

package com.example.edushareandroid.ui.crearcuenta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.edushareandroid.MainActivity;
import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.utils.HashUtil;
import com.example.edushareandroid.utils.ValidationUtil;

import java.io.IOException;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editTextCorreo, editTextContrasenia, editTextUsuario;
    private EditText editTextNombre, editTextPrimerApellido, editTextSegundoApellido;
    private ImageView imageViewFotoPerfil;
    private Bitmap imagenSeleccionada;
    private Spinner spnNivelEducativo, spnInstitucionEducativa;
    private CreateAccountViewModel viewModel;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private int idInstitucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacount);
        viewModel = new ViewModelProvider(this).get(CreateAccountViewModel.class);
        inicializarVistas();
        configurarSpinners();
        configurarObservadores();
    }

    private void inicializarVistas() {
        editTextCorreo = findViewById(R.id.edt_correo);
        editTextContrasenia = findViewById(R.id.edt_contraseña);
        editTextUsuario = findViewById(R.id.edt_usuario);
        editTextNombre = findViewById(R.id.edt_nombre);
        editTextPrimerApellido = findViewById(R.id.edt_primer_apellido);
        editTextSegundoApellido = findViewById(R.id.edt_segundo_apellido);
        imageViewFotoPerfil = findViewById(R.id.img_foto_perfil);
        spnNivelEducativo = findViewById(R.id.spn_nivel_educativo);
        spnInstitucionEducativa = findViewById(R.id.spn_institucion_educativa);

        Button btnCrearCuenta = findViewById(R.id.btn_crear_cuenta);
        btnCrearCuenta.setOnClickListener(v -> validarCampos());

        Button btnCancelar = findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(v -> finish());

        ImageView imgTogglePass = findViewById(R.id.img_alternar_contraseña);
        ImageView imgConfirmToggle = findViewById(R.id.img_alternar_confirmar_contraseña);
        imgTogglePass.setOnClickListener(v -> alternarVisibilidad(editTextContrasenia, imgTogglePass));
        imgConfirmToggle.setOnClickListener(v -> {
            EditText edtConfirmPass = findViewById(R.id.edt_confirmar_contraseña);
            alternarVisibilidad(edtConfirmPass, imgConfirmToggle);
        });
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> nivelAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.niveles_educativos,
                android.R.layout.simple_spinner_item
        );
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNivelEducativo.setAdapter(nivelAdapter);

        spnNivelEducativo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String nivel = parent.getItemAtPosition(position).toString();
                    viewModel.cargarInstituciones(nivel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configurarObservadores() {
        viewModel.getInstituciones().observe(this, instituciones -> {
            if (instituciones != null && !instituciones.isEmpty()) {
                spnInstitucionEducativa.setAdapter(null);
                ArrayAdapter<Institucion> adapter = new ArrayAdapter<Institucion>(
                        CreateAccountActivity.this,
                        android.R.layout.simple_spinner_item,
                        instituciones
                ) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);
                        textView.setText(instituciones.get(position).getNombreInstitucion());
                        return view;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);
                        textView.setText(instituciones.get(position).getNombreInstitucion());
                        return view;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnInstitucionEducativa.setAdapter(adapter);

                spnInstitucionEducativa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getItemAtPosition(position);
                        if (item instanceof Institucion) {
                            Institucion seleccion = (Institucion) item;
                            idInstitucion = seleccion != null && seleccion.getIdInstitucion() > 0 ? seleccion.getIdInstitucion() : -1;
                        } else {
                            idInstitucion = -1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        idInstitucion = -1;
                    }
                });
            } else {
                ArrayAdapter<String> adapterVacio = new ArrayAdapter<>(
                        CreateAccountActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"No hay instituciones disponibles"}
                );
                adapterVacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnInstitucionEducativa.setAdapter(adapterVacio);
                idInstitucion = -1;
            }
        });

        viewModel.getResultadoRegistro().observe(this, exito -> {
            if (exito) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void alternarVisibilidad(EditText passwordField, ImageView toggleIcon) {
        if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed);
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private boolean validarCampos() {
        String correo = editTextCorreo.getText().toString().trim();
        String password = editTextContrasenia.getText().toString();
        String confirmarPassword = ((EditText) findViewById(R.id.edt_confirmar_contraseña)).getText().toString();
        String usuario = editTextUsuario.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();
        String apellido1 = editTextPrimerApellido.getText().toString().trim();
        String apellido2 = editTextSegundoApellido.getText().toString().trim();
        String nivelEducativoSeleccionado = spnNivelEducativo.getSelectedItem().toString();
        Object itemInstitucion = spnInstitucionEducativa.getSelectedItem();

        if (nivelEducativoSeleccionado.equals("Seleccione su nivel educativo")) {
            Toast.makeText(this, "Seleccione un nivel educativo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (itemInstitucion == null || !(itemInstitucion instanceof Institucion)) {
            Toast.makeText(this, "Seleccione una institución válida", Toast.LENGTH_SHORT).show();
            Log.d("VALIDAR", "Tipo seleccionado: " + itemInstitucion.getClass().getName());
            return false;
        }

        Institucion institucionSeleccionada = (Institucion) itemInstitucion;
        if (institucionSeleccionada.getIdInstitucion() <= 0) {
            Toast.makeText(this, "Seleccione una institución válida", Toast.LENGTH_SHORT).show();
            return false;
        }

        idInstitucion = institucionSeleccionada.getIdInstitucion();

        if (!ValidationUtil.noEstaVacio(correo, password, confirmarPassword, usuario, nombre, apellido1)) {
            Toast.makeText(this, "Todos los campos obligatorios deben estar completos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidCorreo(correo)) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombreUsuario(usuario)) {
            Toast.makeText(this, "El nombre de usuario debe tener entre 4 y 15 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombre(nombre)) {
            Toast.makeText(this, "Nombre inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidApellido(apellido1)) {
            Toast.makeText(this, "Primer apellido inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!apellido2.isEmpty() && !ValidationUtil.isValidApellido(apellido2)) {
            Toast.makeText(this, "Segundo apellido inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        registrarUsuario("Imagen/porDefecto/1.png");
        return true;
    }

    private void registrarUsuario(String rutaImagen) {
        UsuarioRegistro usuario = new UsuarioRegistro(
                editTextCorreo.getText().toString(),
                HashUtil.sha256(editTextContrasenia.getText().toString()),
                editTextUsuario.getText().toString(),
                editTextNombre.getText().toString(),
                editTextPrimerApellido.getText().toString(),
                editTextSegundoApellido.getText().toString(),
                idInstitucion,
                rutaImagen
        );
        viewModel.registrarUsuario(usuario);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                imagenSeleccionada = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageViewFotoPerfil.setImageBitmap(imagenSeleccionada);
            } catch (IOException e) {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

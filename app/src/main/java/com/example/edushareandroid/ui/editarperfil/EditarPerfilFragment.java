package com.example.edushareandroid.ui.editarperfil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentEditarperfilBinding;
import com.example.edushareandroid.model.base_de_datos.ActualizarPerfil;
import com.example.edushareandroid.model.base_de_datos.AvatarRequest;
import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;
import com.example.edushareandroid.utils.ValidationUtil;


import java.io.IOException;
import java.util.concurrent.Executors;

public class EditarPerfilFragment extends Fragment {

    private FragmentEditarperfilBinding binding;
    private EditarPerfilViewModel viewModel;
    private UsuarioPerfil usuarioPerfil;
    private Uri imagenSeleccionada;
    private int idInstitucionSeleccionada = -1;
    private boolean esCargaInicial = true;
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    try {
                        imagenSeleccionada = result.getData().getData();
                        binding.imgFotoPerfil.setImageURI(imagenSeleccionada);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditarperfilBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(EditarPerfilViewModel.class);

        EditarPerfilFragmentArgs args = EditarPerfilFragmentArgs.fromBundle(getArguments());
        usuarioPerfil = args.getUsuarioPerfil();
        ;

        precargarDatos();
        configurarSpinners();
        ArrayAdapter<CharSequence> nivelAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.niveles_educativos,
                android.R.layout.simple_spinner_item
        );
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnNivelEducativo.setAdapter(nivelAdapter);

        binding.imgFotoPerfil.setOnClickListener(v -> abrirGaleria());
        binding.btnActualizar.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarCambios();
            }
        });

        observarViewModel();

        return binding.getRoot();
    }

    private void precargarDatos() {
        if (usuarioPerfil != null) {
            binding.edtNombre.setText(usuarioPerfil.getNombre());
            binding.edtPrimerApellido.setText(usuarioPerfil.getPrimerApellido());
            binding.edtSegundoApellido.setText(usuarioPerfil.getSegundoApellido());
            binding.edtCorreo.setText(usuarioPerfil.getCorreo());
            binding.edtUsuario.setText(usuarioPerfil.getNombreUsuario());

            String[] niveles = getResources().getStringArray(R.array.niveles_educativos);
            for (int i = 0; i < niveles.length; i++) {
                if (niveles[i].equalsIgnoreCase(usuarioPerfil.getNivelEducativo())) {
                    binding.spnNivelEducativo.setSelection(i);
                    viewModel.cargarInstituciones(niveles[i]);
                    break;
                }
            }

            viewModel.setPerfil(usuarioPerfil);
            viewModel.descargarImagenPerfil();
            esCargaInicial = false;
        }
    }

    private void configurarSpinners() {
        binding.spnNivelEducativo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String nivel = parent.getItemAtPosition(position).toString();
                    if (!esCargaInicial) {
                        viewModel.cargarInstituciones(nivel);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spnInstitucionEducativa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Institucion institucion = (Institucion) parent.getItemAtPosition(position);
                idInstitucionSeleccionada = institucion.getIdInstitucion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void observarViewModel() {
        viewModel.getInstituciones().observe(getViewLifecycleOwner(), instituciones -> {
            ArrayAdapter<Institucion> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, instituciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spnInstitucionEducativa.setAdapter(adapter);
            for (int i = 0; i < instituciones.size(); i++) {
                if (instituciones.get(i).getIdInstitucion() == usuarioPerfil.getIdInstitucion()) {
                    binding.spnInstitucionEducativa.setSelection(i);
                    break;
                }
            }
        });

        viewModel.getImagenPerfilLiveData().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.imgFotoPerfil.setImageBitmap(bitmap);
            }
        });

        viewModel.getResultadoActualizacion().observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(requireContext(), "Perfil actualizado con éxito", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), mensaje -> {
            Toast.makeText(requireContext(), "Error: " + mensaje, Toast.LENGTH_LONG).show();
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarImagenLauncher.launch(intent);
    }

    private void guardarCambios() {
        String nombre = binding.edtNombre.getText().toString().trim();
        String apellido1 = binding.edtPrimerApellido.getText().toString().trim();
        String apellido2 = binding.edtSegundoApellido.getText().toString().trim();
        String correo = binding.edtCorreo.getText().toString().trim();
        String usuario = binding.edtUsuario.getText().toString().trim();

        ActualizarPerfil perfil = new ActualizarPerfil();
        perfil.setNombre(nombre);
        perfil.setCorreo(correo);
        perfil.setPrimerApellido(apellido1);
        perfil.setSegundoApellido(apellido2);
        perfil.setNombreUsuario(usuario);
        perfil.setIdInstitucion(idInstitucionSeleccionada);

        String token = SesionUsuario.obtenerToken(requireContext());

        if (imagenSeleccionada != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    byte[] imageData = ImageUtil.uriToBinary(imagenSeleccionada, requireContext());
                    String extension = ImageUtil.getImageExtensionFromUri(imagenSeleccionada, requireContext());
                    String filenameConExtension = usuario + "." + extension;

                    viewModel.subirImagenGrpc(imageData, usuario, filenameConExtension, new FileServiceClient.UploadCallback() {
                        @Override
                        public void onSuccess(String filePath, String coverImagePath) {
                            requireActivity().runOnUiThread(() -> {
                                AvatarRequest avatarRequest = new AvatarRequest(filePath);
                                viewModel.actualizarAvatar(token, avatarRequest);
                                viewModel.getResultadoActualizacionAvatar().observe(getViewLifecycleOwner(), actualizado -> {
                                    if (actualizado != null && actualizado) {
                                        actualizarPerfilConCallback(token, perfil);
                                    } else {
                                        mostrarError("Error al actualizar avatar");
                                    }
                                });
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            requireActivity().runOnUiThread(() -> mostrarError("Error al subir imagen: " + e.getMessage()));
                        }
                    });

                } catch (IOException e) {
                    requireActivity().runOnUiThread(() -> mostrarError("Error al convertir imagen: " + e.getMessage()));
                    e.printStackTrace();
                }
            });

        } else {
            actualizarPerfilConCallback(token, perfil);
        }
    }

    private void actualizarPerfilConCallback(String token, ActualizarPerfil perfil) {
        viewModel.actualizarPerfil(token, perfil);
        viewModel.getResultadoActualizacion().observe(getViewLifecycleOwner(), exito -> {
            if (exito != null && exito) {
                Toast.makeText(requireContext(), "Perfil actualizado con éxito", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else {
                mostrarError("Error al actualizar perfil");
            }
        });
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private boolean validarCampos() {
        String correo = binding.edtCorreo.getText().toString().trim();
        String usuario = binding.edtUsuario.getText().toString().trim();
        String nombre = binding.edtNombre.getText().toString().trim();
        String apellido1 = binding.edtPrimerApellido.getText().toString().trim();
        String apellido2 = binding.edtSegundoApellido.getText().toString().trim();
        String nivelEducativoSeleccionado = binding.spnNivelEducativo.getSelectedItem().toString();

        if (nivelEducativoSeleccionado.equals("Seleccione su nivel educativo")) {
            Toast.makeText(requireContext(), "Seleccione un nivel educativo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (idInstitucionSeleccionada <= 0) {
            Toast.makeText(requireContext(), "Seleccione una institución válida", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.noEstaVacio(correo, usuario, nombre, apellido1)) {
            Toast.makeText(requireContext(), "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidCorreo(correo)) {
            Toast.makeText(requireContext(), "Correo inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombreUsuario(usuario)) {
            Toast.makeText(requireContext(), "Nombre de usuario inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidNombre(nombre)) {
            Toast.makeText(requireContext(), "Nombre inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationUtil.isValidApellido(apellido1)) {
            Toast.makeText(requireContext(), "Primer apellido inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!apellido2.isEmpty() && !ValidationUtil.isValidApellido(apellido2)) {
            Toast.makeText(requireContext(), "Segundo apellido inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.edushareandroid.ui.usuarios;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentPerfilusuarioBinding;
import com.example.edushareandroid.model.adapter.ResultadoAdapter;
import com.example.edushareandroid.model.base_de_datos.Documento;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;


public class PerfilUsuarioFragment extends Fragment {
    private FragmentPerfilusuarioBinding binding;
    private ResultadoAdapter adapter;
    private List<Documento> documentosFiltrados;
    private List<Documento> documentos;
    private FileServiceClient fileServiceClient;
    private UsuariosViewModel usuariosViewModel;
    private int idUsuarioPerfil; // ID del perfil mostrado
    private String token; // Token del usuario autenticado

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilusuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fileServiceClient = new FileServiceClient();
        usuariosViewModel = new ViewModelProvider(this).get(UsuariosViewModel.class);

        token = SesionUsuario.obtenerToken(requireContext());

        setupRecyclerView();
        loadProfileData();

        return root;
    }

    private void setupRecyclerView() {
        binding.rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        documentos = new ArrayList<>();
        documentos.add(new Documento("Matemáticas I", "Guía para parcial", "...", R.drawable.ic_archivo));
        documentosFiltrados = new ArrayList<>(documentos);

        adapter = new ResultadoAdapter(requireContext(), documentosFiltrados, documento -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("documentoSeleccionado", documento);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_perfilUsuarioFragment_to_navigation_verArchivo, bundle);
        });

        binding.rvPublicaciones.setAdapter(adapter);
    }

    private void loadProfileData() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("idUsuario")) {
            idUsuarioPerfil = args.getInt("idUsuario");

            usuariosViewModel.cargarPerfilPorId(idUsuarioPerfil);
            usuariosViewModel.getPerfilUsuario().observe(getViewLifecycleOwner(), perfil -> {
                if (perfil != null) {
                    displayProfileData(perfil);
                } else {
                    binding.txtNombreCompleto.setText("Perfil no encontrado");
                }
            });

            usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
            usuariosViewModel.getEstaSiguiendo().observe(getViewLifecycleOwner(), this::actualizarBotonesSeguimiento);
        }

        usuariosViewModel.getRespuestaSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
            }
        });

        usuariosViewModel.getRespuestaDejarSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
            }
        });
    }

    private void displayProfileData(UsuarioPerfil perfil) {
        String nombreCompleto = perfil.getNombre() + " " + perfil.getPrimerApellido();
        if (perfil.getSegundoApellido() != null && !perfil.getSegundoApellido().isEmpty()) {
            nombreCompleto += " " + perfil.getSegundoApellido();
        }
        binding.txtNombreCompleto.setText(nombreCompleto);
        binding.txtNombreUsuario.setText("@" + perfil.getNombreUsuario());
        binding.txtNumSeguidores.setText(String.valueOf(perfil.getNumeroSeguidores()));
        binding.txtNumSeguidos.setText(String.valueOf(perfil.getNumeroSeguidos()));
        binding.txtInstitucion.setText(perfil.getNombreInstitucion());
        binding.txtNivelEducativo.setText(perfil.getNivelEducativo());

        loadProfileImage(perfil.getFotoPerfil());
    }

    private void actualizarBotonesSeguimiento(boolean estaSiguiendo) {
        if (estaSiguiendo) {
            binding.btnSeguir.setVisibility(View.GONE);
            binding.btnDejarSeguir.setVisibility(View.VISIBLE);
        } else {
            binding.btnSeguir.setVisibility(View.VISIBLE);
            binding.btnDejarSeguir.setVisibility(View.GONE);
        }

        binding.btnSeguir.setOnClickListener(v -> usuariosViewModel.seguirUsuario(token, idUsuarioPerfil));
        binding.btnDejarSeguir.setOnClickListener(v -> usuariosViewModel.dejarDeSeguirUsuario(token, idUsuarioPerfil));
    }

    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            fileServiceClient.downloadImage(imagePath, new FileServiceClient.DownloadCallback() {
                @Override
                public void onSuccess(byte[] imageData, String filename) {
                    Bitmap bitmap = ImageUtil.binaryToBitmap(imageData);
                    if (bitmap != null) {
                        requireActivity().runOnUiThread(() -> binding.imgPerfil.setImageBitmap(bitmap));
                    }
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() -> binding.imgPerfil.setImageResource(R.drawable.ic_perfil));
                }
            });
        } else {
            binding.imgPerfil.setImageResource(R.drawable.ic_perfil);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

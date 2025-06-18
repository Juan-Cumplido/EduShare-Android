package com.example.edushareandroid.ui.perfil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.databinding.FragmentPerfilBinding;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.publicaciones.PublicacionesAdapter;
import com.example.edushareandroid.ui.publicaciones.PublicacionesViewModel;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private PublicacionesViewModel publicacionesViewModel;
    private PublicacionesAdapter adapter;
    private ProgressDialog progressDialog;
    private boolean isUserLoggedIn = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModels();
        setupRecyclerView();
        checkSessionStatus();
    }

    private void initializeViewModels() {
        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        publicacionesViewModel = new ViewModelProvider(this).get(PublicacionesViewModel.class);
    }

    private void setupRecyclerView() {
        FileServiceClient fileServiceClient = new FileServiceClient();
        adapter = new PublicacionesAdapter(
                new ArrayList<>(),
                fileServiceClient,
                new PublicacionesAdapter.OnItemClickListener() {
                    @Override
                    public void onVerMasClick(DocumentoResponse documento) {
                        navigateToDocumentDetails(documento);
                    }

                    @Override
                    public void onOpcionesClick(DocumentoResponse documento) {
                        showDeleteConfirmation(documento);
                    }

                    @Override
                    public void onEliminarClick(DocumentoResponse documento) {
                        showDeleteConfirmation(documento);
                    }
                },
                requireContext()
        );

        binding.rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPublicaciones.setAdapter(adapter);
    }

    private void checkSessionStatus() {
        isUserLoggedIn = SesionUsuario.isUsuarioLogueado(requireContext());

        if (isUserLoggedIn) {
            initializeForLoggedInUser();
        } else {
            showLoggedOutUI();
        }
    }

    private void initializeForLoggedInUser() {
        showLoggedInUI();
        String token = SesionUsuario.obtenerToken(requireContext());

        if (token == null || token.trim().isEmpty()) {
            handleInvalidToken();
            return;
        }

        setupObservers();
        loadUserData(token);
        setupButtonListeners();
    }

    private void setupObservers() {
        perfilViewModel.getPerfilLiveData().observe(getViewLifecycleOwner(), this::updateProfileUI);
        perfilViewModel.getImagenPerfilLiveData().observe(getViewLifecycleOwner(), this::updateProfileImage);
        publicacionesViewModel.getPublicacionesLiveData().observe(getViewLifecycleOwner(), this::updatePublicationsUI);
        perfilViewModel.getError().observe(getViewLifecycleOwner(), this::showErrorToast);
        observeDeletionStatus();
    }

    private void loadUserData(String token) {
        perfilViewModel.cargarPerfil(token);
        publicacionesViewModel.cargarPublicacionesPropias(token);
    }

    private void updateProfileUI(UsuarioPerfil perfil) {
        if (perfil == null) return;

        SesionUsuario.guardarNombreUsuario(requireContext(), perfil.getNombreUsuario());
        binding.txtNombre.setText(String.format("Nombre: %s %s", perfil.getNombre(), perfil.getPrimerApellido()));
        binding.txtUsuario.setText(String.format("Usuario: %s", perfil.getNombreUsuario()));
        binding.txtNivelEducativo.setText(String.format("Nivel educativo: %s", perfil.getNivelEducativo()));
        binding.txtCarrera.setText(String.format("Institución: %s", perfil.getNombreInstitucion()));

        if (perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
            perfilViewModel.descargarImagenPerfil();
        }
    }

    private void updateProfileImage(Bitmap bitmap) {
        if (bitmap != null) {
            binding.imgPerfil.setImageBitmap(bitmap);
        }
    }

    private void updatePublicationsUI(List<DocumentoResponse> publicaciones) {
        if (publicaciones == null || publicaciones.isEmpty()) {
            showNoPublicationsMessage();
            return;
        }

        binding.rvPublicaciones.setVisibility(View.VISIBLE);
        binding.txtSinPublicaciones.setVisibility(View.GONE);
        adapter.actualizarLista(publicaciones);
    }

    private void showNoPublicationsMessage() {
        binding.txtSinPublicaciones.setVisibility(View.VISIBLE);
        binding.rvPublicaciones.setVisibility(View.GONE);
        binding.txtSinPublicaciones.setText("Aún no has subido publicaciones");
    }

    private void observeDeletionStatus() {
        publicacionesViewModel.getEliminacionExitosaLiveData().observe(getViewLifecycleOwner(), exitosa -> {
            if (exitosa != null && exitosa) {
                reloadPublications();
            }
        });
    }

    private void reloadPublications() {
        String token = SesionUsuario.obtenerToken(requireContext());
        if (token != null) {
            publicacionesViewModel.cargarPublicacionesPropias(token);
        }
    }

    private void handleInvalidToken() {
        SesionUsuario.cerrarSesion(requireContext());
        showLoggedOutUI();
        Toast.makeText(requireContext(), "Sesión inválida", Toast.LENGTH_SHORT).show();
    }

    private void showLoggedInUI() {
        binding.txtIniciarSesion.setVisibility(View.GONE);
        binding.rvPublicaciones.setVisibility(View.VISIBLE);
        binding.btnEditarPerfil.setVisibility(View.VISIBLE);
        binding.btnCerrarSesion.setVisibility(View.VISIBLE);
        binding.clGrupoBotones.setVisibility(View.VISIBLE);
        binding.txtNombre.setVisibility(View.VISIBLE);
        binding.txtUsuario.setVisibility(View.VISIBLE);
        binding.txtNivelEducativo.setVisibility(View.VISIBLE);
        binding.txtCarrera.setVisibility(View.VISIBLE);
        binding.txtPublicaciones.setVisibility(View.VISIBLE);
        binding.txtInformacionPersonal.setVisibility(View.VISIBLE);
        binding.btnSubirPublicacion.setVisibility(View.VISIBLE);
        binding.imgPerfil.setVisibility(View.VISIBLE);
    }

    private void showLoggedOutUI() {
        binding.txtIniciarSesion.setVisibility(View.VISIBLE);
        binding.rvPublicaciones.setVisibility(View.GONE);
        binding.btnEditarPerfil.setVisibility(View.GONE);
        binding.btnCerrarSesion.setVisibility(View.GONE);
        binding.clGrupoBotones.setVisibility(View.GONE);
        binding.txtNombre.setVisibility(View.GONE);
        binding.txtUsuario.setVisibility(View.GONE);
        binding.txtNivelEducativo.setVisibility(View.GONE);
        binding.txtCarrera.setVisibility(View.GONE);
        binding.txtPublicaciones.setVisibility(View.GONE);
        binding.txtInformacionPersonal.setVisibility(View.GONE);
        binding.btnSubirPublicacion.setVisibility(View.GONE);
        binding.imgPerfil.setVisibility(View.GONE);
        binding.txtSinPublicaciones.setVisibility(View.GONE);
        binding.txtPerfil.setVisibility(View.GONE);
    }

    private void setupButtonListeners() {
        binding.btnSubirPublicacion.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        PerfilFragmentDirections.actionNavigationPerfilToNavigationPublicacion()
                )
        );

        binding.btnEditarPerfil.setOnClickListener(v -> {
            UsuarioPerfil usuarioPerfil = perfilViewModel.getPerfilLiveData().getValue();
            if (usuarioPerfil != null) {
                Navigation.findNavController(v).navigate(
                        PerfilFragmentDirections.actionNavigationPerfilToEditarPerfilFragment(usuarioPerfil)
                );
            } else {
                Toast.makeText(requireContext(), "Perfil no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCerrarSesion.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SesionUsuario.cerrarSesion(requireContext());
                    Navigation.findNavController(binding.getRoot()).navigate(
                            PerfilFragmentDirections.actionNavigationPerfilToNavigationLogin()
                    );
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void navigateToDocumentDetails(DocumentoResponse documento) {
        Navigation.findNavController(binding.getRoot()).navigate(
                PerfilFragmentDirections.actionNavigationPerfilToNavigationVerArchivo(documento)
        );
    }

    private void showDeleteConfirmation(DocumentoResponse documento) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar la publicación \"" + documento.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String token = SesionUsuario.obtenerToken(requireContext());
                    if (token != null && !token.trim().isEmpty()) {
                        publicacionesViewModel.confirmarEliminacionPublicacion(
                                documento.getIdPublicacion(), token);
                    } else {
                        Toast.makeText(requireContext(), "Error: Token no válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showErrorToast(String error) {
        if (error != null) {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean currentLoginStatus = SesionUsuario.isUsuarioLogueado(requireContext());
        if (currentLoginStatus != isUserLoggedIn) {
            checkSessionStatus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        publicacionesViewModel.limpiarEstadoCompleto();
    }
}
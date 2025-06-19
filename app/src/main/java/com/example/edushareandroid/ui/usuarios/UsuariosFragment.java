package com.example.edushareandroid.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentUsuariosBinding;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuariosFragment extends Fragment {
    private FragmentUsuariosBinding binding;
    private UsuarioAdapterPerfil adapter;
    private UsuariosViewModel viewModel;
    private FileServiceClient fileServiceClient;
    private List<UsuarioPerfilRecuperado> listaPerfilesOriginal = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);

        fileServiceClient = new FileServiceClient();
        checkAuthentication();
        return binding.getRoot();
    }

    private void checkAuthentication() {
        if (SesionUsuario.isUsuarioLogueado(requireContext())) {
            setupAuthenticatedView();
        } else {
            setupUnauthenticatedView();
        }
    }

    private void setupAuthenticatedView() {
        binding.txtNoSesion.setVisibility(View.GONE);
        binding.seavBuscarDocumento.setVisibility(View.VISIBLE);
        binding.rvResultados.setVisibility(View.VISIBLE);

        initRecyclerView();
        initViewModel();
        setupSearchListener();

        // Carga inicial
        viewModel.buscarPerfiles("");
    }

    private void setupUnauthenticatedView() {
        binding.txtNoSesion.setVisibility(View.VISIBLE);
        binding.seavBuscarDocumento.setVisibility(View.GONE);
        binding.rvResultados.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        binding.rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvResultados.setItemAnimator(null);

        adapter = new UsuarioAdapterPerfil(new ArrayList<>(), createItemClickListener(), fileServiceClient);
        binding.rvResultados.setAdapter(adapter);
    }

    private UsuarioAdapterPerfil.OnItemClickListener createItemClickListener() {
        return new UsuarioAdapterPerfil.OnItemClickListener() {
            @Override
            public void onItemClick(UsuarioPerfilRecuperado usuario) {
                navigateToUserProfile(usuario);
            }

            @Override
            public void onVerMasClick(UsuarioPerfilRecuperado usuario) {
                navigateToUserProfile(usuario);
            }
        };
    }

    private void navigateToUserProfile(UsuarioPerfilRecuperado usuario) {
        Bundle bundle = new Bundle();
        bundle.putInt("idUsuario", usuario.getIdUsuarioRegistrado());
        bundle.putString("nombre", usuario.getNombre() + " " + usuario.getPrimerApellido());
        if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isEmpty()) {
            bundle.putString("nombre", bundle.getString("nombre") + " " + usuario.getSegundoApellido());
        }
        bundle.putString("fotoPerfil", usuario.getFotoPerfil());
        bundle.putString("nombreUsuario", usuario.getNombreUsuario());
        bundle.putInt("numeroPublicaciones", usuario.getNumeroPublicaciones());
        bundle.putInt("numeroSeguidores", usuario.getNumeroSeguidores());
        bundle.putString("nivelEducativo", usuario.getNivelEducativo());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_navigation_usuarios_to_perfilUsuarioFragment, bundle);
    }

    private void showMoreOptions(UsuarioPerfilRecuperado usuario) {
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(UsuariosViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getPerfiles().observe(getViewLifecycleOwner(), this::handleProfilesResult);
        viewModel.getError().observe(getViewLifecycleOwner(), this::handleError);
    }

    private void handleProfilesResult(List<UsuarioPerfilRecuperado> perfiles) {
        if (perfiles == null || perfiles.isEmpty()) {
            showEmptyState();
        } else {
            listaPerfilesOriginal.clear();
            listaPerfilesOriginal.addAll(perfiles);
            showProfiles(perfiles);
        }
    }


    private void showEmptyState() {
        binding.rvResultados.setVisibility(View.GONE);
        binding.txtNoResultados.setVisibility(View.VISIBLE);
    }

    private void showProfiles(List<UsuarioPerfilRecuperado> perfiles) {
        binding.rvResultados.setVisibility(View.VISIBLE);
        binding.txtNoResultados.setVisibility(View.GONE);
        adapter.updateData(perfiles);
    }

    private void handleError(String error) {
        if (error != null && !error.isEmpty()) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchListener() {
        binding.seavBuscarDocumento.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarListaLocal(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarListaLocal(newText);
                return true;
            }
        });
    }

    private void filtrarListaLocal(String textoBusqueda) {
        if (listaPerfilesOriginal == null) return;

        String texto = textoBusqueda.toLowerCase().trim();
        if (texto.isEmpty()) {
            adapter.updateData(listaPerfilesOriginal);
            binding.txtNoResultados.setVisibility(View.GONE);
            binding.rvResultados.setVisibility(View.VISIBLE);
            return;
        }

        List<UsuarioPerfilRecuperado> listaFiltrada = new ArrayList<>();
        for (UsuarioPerfilRecuperado usuario : listaPerfilesOriginal) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getPrimerApellido() +
                    (usuario.getSegundoApellido() != null ? " " + usuario.getSegundoApellido() : "");
            if (nombreCompleto.toLowerCase().contains(texto) ||
                    usuario.getNombreUsuario().toLowerCase().contains(texto)) {
                listaFiltrada.add(usuario);
            }
        }

        if (listaFiltrada.isEmpty()) {
            binding.txtNoResultados.setVisibility(View.VISIBLE);
            binding.rvResultados.setVisibility(View.GONE);
        } else {
            binding.txtNoResultados.setVisibility(View.GONE);
            binding.rvResultados.setVisibility(View.VISIBLE);
        }

        adapter.updateData(listaFiltrada);
    }


    @Override
    public void onDestroyView() {
        if (adapter != null) {
            adapter.clearResources();
        }
        binding = null;
        super.onDestroyView();
    }
}

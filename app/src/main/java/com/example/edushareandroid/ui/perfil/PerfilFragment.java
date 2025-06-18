package com.example.edushareandroid.ui.perfil;

import static com.example.edushareandroid.utils.SesionUsuario.obtenerToken;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.databinding.FragmentPerfilBinding;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.publicaciones.PublicacionesAdapter;
import com.example.edushareandroid.ui.publicaciones.PublicacionesViewModel;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private PublicacionesViewModel publicacionesViewModel;
    private UsuarioPerfil perfilActual;
    private PublicacionesAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("PerfilFragment", "onCreateView()");
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar ViewModels
        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        publicacionesViewModel = new ViewModelProvider(this).get(PublicacionesViewModel.class);

        // Configurar el adapter con el listener
        FileServiceClient fileServiceClient = new FileServiceClient();
        PublicacionesAdapter.OnItemClickListener listener = new PublicacionesAdapter.OnItemClickListener() {
            @Override
            public void onVerMasClick(DocumentoResponse documento) {
                PerfilFragmentDirections.ActionNavigationPerfilToNavigationVerArchivo action =
                        PerfilFragmentDirections.actionNavigationPerfilToNavigationVerArchivo(documento);
                NavHostFragment.findNavController(PerfilFragment.this).navigate(action);
            }

            @Override
            public void onOpcionesClick(DocumentoResponse documento) {
                mostrarDialogoConfirmacionEliminacion(documento);
            }

            @Override
            public void onEliminarClick(DocumentoResponse documento) {
                mostrarDialogoConfirmacionEliminacion(documento);
            }
        };

        // Configurar RecyclerView con el nuevo adapter
        RecyclerView recyclerView = binding.rvPublicaciones;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicacionesAdapter(new ArrayList<>(), fileServiceClient, listener, requireContext());
        recyclerView.setAdapter(adapter);

        // Configurar observadores
        observarViewModel();

        // Verificar estado de sesión
        if (!SesionUsuario.isUsuarioLogueado(requireContext())) {
            mostrarVistaDeslogueado();
        } else {
            mostrarVistaLogueado();
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SesionUsuario.isUsuarioLogueado(requireContext())) {
            mostrarVistaLogueado();
        }
    }

    private void mostrarVistaDeslogueado() {
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
        binding.txtPerfil.setVisibility(View.GONE);
        binding.imgPerfil.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVistaLogueado() {
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

        String token = obtenerToken(requireContext());

        if (token == null || token.trim().isEmpty()) {
            mostrarVistaDeslogueado();
            SesionUsuario.cerrarSesion(requireContext());
            return;
        }

        // Cargar perfil y publicaciones
        perfilViewModel.cargarPerfil(token);
        publicacionesViewModel.cargarPublicacionesPropias(token);

        configurarListeners();
    }

    private void configurarListeners() {
        binding.btnSubirPublicacion.setOnClickListener(v -> {
            NavDirections action = PerfilFragmentDirections.actionNavigationPerfilToNavigationPublicacion();
            Navigation.findNavController(v).navigate(action);
        });

        binding.btnEditarPerfil.setOnClickListener(v -> {
            UsuarioPerfil usuarioPerfil = perfilViewModel.getPerfilLiveData().getValue();
            if (usuarioPerfil != null) {
                NavDirections action = PerfilFragmentDirections
                        .actionNavigationPerfilToEditarPerfilFragment(usuarioPerfil);
                Navigation.findNavController(v).navigate(action);
            } else {
                Toast.makeText(requireContext(), "Perfil no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCerrarSesion.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        SesionUsuario.cerrarSesion(requireContext());
                        NavDirections action = PerfilFragmentDirections.actionNavigationPerfilToNavigationLogin();
                        Navigation.findNavController(v).navigate(action);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void observarViewModel() {
        // Observar cambios en el perfil
        perfilViewModel.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil != null) {
                perfilActual = perfil;
                SesionUsuario.guardarNombreUsuario(requireContext(), perfil.getNombreUsuario());
                binding.txtNombre.setText("Nombre: " + perfil.getNombre() + " " + perfil.getPrimerApellido());
                binding.txtUsuario.setText("Usuario: " + perfil.getNombreUsuario());
                binding.txtNivelEducativo.setText("Nivel educativo: " + perfil.getNivelEducativo());
                binding.txtCarrera.setText("Institución: " + perfil.getNombreInstitucion());

                if (perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
                    perfilViewModel.descargarImagenPerfil();
                }
            }
        });

        perfilViewModel.getImagenPerfilLiveData().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.imgPerfil.setImageBitmap(bitmap);
            }
        });

        // Observar publicaciones propias
        publicacionesViewModel.getPublicacionesLiveData().observe(getViewLifecycleOwner(), publicaciones -> {
            Log.d("PerfilFragment", "Publicaciones obtenidas: " + (publicaciones != null ? publicaciones.size() : 0));

            if (publicaciones != null && !publicaciones.isEmpty()) {
                binding.rvPublicaciones.setVisibility(View.VISIBLE);
                binding.txtSinPublicaciones.setVisibility(View.GONE);
                adapter.actualizarLista(publicaciones);
            } else {
                binding.txtSinPublicaciones.setVisibility(View.VISIBLE);
                binding.rvPublicaciones.setVisibility(View.GONE);
                binding.txtSinPublicaciones.setText("Aún no has subido publicaciones");
            }
        });

        // Observar mensajes
        publicacionesViewModel.getMensajeLiveData().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar estado de carga
        publicacionesViewModel.getCargandoLiveData().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando != null) {
                //binding.progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            }
        });

        // Observar errores
        perfilViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar estado de eliminación
        observarEliminacion();
    }

    private void observarEliminacion() {
        publicacionesViewModel.getCargandoEliminacionLiveData().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando != null) {
                if (cargando) {
                    mostrarProgressDialog("Eliminando publicación...");
                } else {
                    ocultarProgressDialog();
                }
            }
        });

        publicacionesViewModel.getEliminacionExitosaLiveData().observe(getViewLifecycleOwner(), exitosa -> {
            if (exitosa != null) {
                String mensaje = publicacionesViewModel.getMensajeEliminacionLiveData().getValue();
                if (mensaje != null) {
                    Toast.makeText(getContext(), mensaje,
                            exitosa ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

                    // Recargar publicaciones después de eliminar
                    if (exitosa) {
                        String token = obtenerToken(requireContext());
                        if (token != null) {
                            publicacionesViewModel.cargarPublicacionesPropias(token);
                        }
                    }
                }
                publicacionesViewModel.limpiarMensajesEliminacion();
            }
        });
    }

    private void mostrarDialogoConfirmacionEliminacion(DocumentoResponse documento) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar la publicación \"" + documento.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String token = obtenerToken(requireContext());
                    if (token != null && !token.trim().isEmpty()) {
                        publicacionesViewModel.confirmarEliminacionPublicacion(
                                documento.getIdPublicacion(), token);
                    } else {
                        Toast.makeText(requireContext(),
                                "Error: Token no válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarProgressDialog(String mensaje) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(mensaje);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void ocultarProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ocultarProgressDialog();
        if (adapter != null) {
            adapter.cleanup();
        }
        binding = null;
    }
}
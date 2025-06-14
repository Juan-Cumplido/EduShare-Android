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
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private UsuarioPerfil perfilActual;
    private DocumentoAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        FileServiceClient fileServiceClient = new FileServiceClient();

        // Configurar el listener del adapter
        DocumentoAdapter.OnItemClickListener listener = new DocumentoAdapter.OnItemClickListener() {
            @Override
            public void onVerMasClick(DocumentoResponse documento) {
                PerfilFragmentDirections.ActionNavigationPerfilToNavigationVerArchivo action =
                        PerfilFragmentDirections.actionNavigationPerfilToNavigationVerArchivo(documento);
                NavHostFragment.findNavController(PerfilFragment.this).navigate(action);
            }

            @Override
            public void onOpcionesClick(DocumentoResponse documento) {
                // Método existente - puedes mantenerlo para otras opciones
                mostrarDialogoConfirmacionEliminacion(documento);
            }

            @Override
            public void onEliminarClick(DocumentoResponse documento) {
                //mostrarDialogoConfirmacionEliminacion(documento);
            }
        };

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.rvPublicaciones;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DocumentoAdapter(new ArrayList<>(), fileServiceClient, listener);
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

        perfilViewModel.cargarPerfil(token);
        perfilViewModel.cargarPublicacionesUsuario(token);

        // Configurar listeners de botones
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
    }

    private void observarViewModel() {
        // Observar perfil
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

        // Observar imagen de perfil
        perfilViewModel.getImagenPerfilLiveData().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.imgPerfil.setImageBitmap(bitmap);
            }
        });

        // Observar publicaciones
        perfilViewModel.getPublicaciones().observe(getViewLifecycleOwner(), publicaciones -> {
            Log.d("PerfilFragment", "Obtenidas publicaciones en Fragment: " + (publicaciones != null ? publicaciones.size() : "null"));

            if (publicaciones != null && !publicaciones.isEmpty()) {
                binding.rvPublicaciones.setVisibility(View.VISIBLE);
                binding.txtSinPublicaciones.setVisibility(View.GONE);

                try {
                    Log.d("PerfilFragment", "Actualizando adapter con publicaciones...");
                    adapter.actualizarLista(publicaciones);
                    Log.d("PerfilFragment", "Adapter actualizado correctamente.");
                } catch (Exception e) {
                    Log.e("PerfilFragment", "Error al actualizar el adapter: " + e.getMessage(), e);
                }

            } else {
                binding.txtSinPublicaciones.setVisibility(View.VISIBLE);
                binding.rvPublicaciones.setVisibility(View.GONE);
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
        // Observar diálogo de confirmación
        perfilViewModel.getMostrandoDialogoLiveData().observe(getViewLifecycleOwner(), mostrandoDialogo -> {
            // Este observer se mantiene para compatibilidad, pero el diálogo se maneja directamente
        });

        // Observar carga de eliminación
        perfilViewModel.getCargandoEliminacionLiveData().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando != null) {
                if (cargando) {
                    mostrarProgressDialog("Eliminando publicación...");
                } else {
                    ocultarProgressDialog();
                }
            }
        });

        // Observar resultado de eliminación
        perfilViewModel.getEliminacionExitosaLiveData().observe(getViewLifecycleOwner(), exitosa -> {
            if (exitosa != null) {
                String mensaje = perfilViewModel.getMensajeEliminacionLiveData().getValue();
                if (mensaje != null) {
                    if (exitosa) {
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error: " + mensaje, Toast.LENGTH_LONG).show();
                    }
                }
                // Limpiar mensajes después de mostrarlos
                perfilViewModel.limpiarMensajesEliminacion();
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
                        perfilViewModel.confirmarEliminacionPublicacion(documento.getIdPublicacion(), token);
                    } else {
                        Toast.makeText(requireContext(), "Error: Token no válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    perfilViewModel.cancelarEliminacion();
                })
                .setCancelable(false)
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
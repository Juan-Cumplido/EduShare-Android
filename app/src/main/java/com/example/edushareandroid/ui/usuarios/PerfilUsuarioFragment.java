package com.example.edushareandroid.ui.usuarios;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentPerfilusuarioBinding;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.ui.publicaciones.PublicacionesAdapter;
import com.example.edushareandroid.ui.publicaciones.PublicacionesViewModel;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;

public class PerfilUsuarioFragment extends Fragment implements PublicacionesAdapter.OnItemClickListener {
    private static final String TAG = "PerfilUsuarioFragment";

    private FragmentPerfilusuarioBinding binding;
    private PublicacionesAdapter publicacionesAdapter;
    private FileServiceClient fileServiceClient;
    private UsuariosViewModel usuariosViewModel;
    private NavController navController;

    private int idUsuarioPerfil = -1;
    private String token;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilusuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Log.d(TAG, "Iniciando PerfilUsuarioFragment");

        // Inicializar servicios y ViewModels
        initializeServices();

        // Obtener NavController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        // Configurar UI
        setupRecyclerView();
        setupObservers();

        // Cargar datos del perfil
        loadProfileData();

        return root;
    }

    private void initializeServices() {
        fileServiceClient = new FileServiceClient();
        usuariosViewModel = new ViewModelProvider(this).get(UsuariosViewModel.class);
        token = SesionUsuario.obtenerToken(requireContext());
        recargarPublicaciones();
        Log.d(TAG, "Servicios inicializados");
    }


    private void setupRecyclerView() {
        binding.rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adapter con lista vacía
        publicacionesAdapter = new PublicacionesAdapter(
                new ArrayList<>(),
                fileServiceClient,
                this,
                requireContext()
        );

        binding.rvPublicaciones.setAdapter(publicacionesAdapter);
        Log.d(TAG, "RecyclerView configurado");
    }

    private void setupObservers() {
        usuariosViewModel.getPublicacionesPorUsuario().observe(getViewLifecycleOwner(), publicaciones -> {
            Log.d(TAG, "Publicaciones recibidas: " + (publicaciones != null ? publicaciones.size() : 0));
            if (publicaciones != null) {
                publicacionesAdapter.actualizarLista(publicaciones);
            }
        });

        usuariosViewModel.getMensajePublicaciones().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Log.d(TAG, "Mensaje recibido: " + mensaje);
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadProfileData() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("idUsuario")) {
            int nuevoIdUsuario = args.getInt("idUsuario");

            Log.d(TAG, "Cargando perfil para usuario ID: " + nuevoIdUsuario);

            // Si es un usuario diferente o es la primera carga, limpiar datos anteriores
            if (idUsuarioPerfil != nuevoIdUsuario) {
                Log.d(TAG, "Usuario diferente detectado, limpiando datos anteriores");
                idUsuarioPerfil = nuevoIdUsuario;
            }

            // Cargar datos del perfil
            cargarDatosPerfil();

            // Verificar seguimiento
            verificarEstadoSeguimiento();

            // Cargar publicaciones del usuario específico
            cargarPublicacionesUsuario();

        } else {
            Log.e(TAG, "No se encontró ID de usuario en argumentos");
            mostrarErrorPerfil();
        }

        // Configurar observers para respuestas de seguimiento
        setupSeguimientoObservers();
    }



    private void limpiarUIPerfil() {
        if (binding != null) {
            binding.txtNombreCompleto.setText("");
            binding.txtNombreUsuario.setText("");
            binding.txtNumSeguidores.setText("0");
            binding.txtNumSeguidos.setText("0");
            binding.txtInstitucion.setText("");
            binding.txtNivelEducativo.setText("");
            binding.imgPerfil.setImageResource(R.drawable.ic_perfil);

            // Ocultar botones de seguimiento
            binding.btnSeguir.setVisibility(View.GONE);
            binding.btnDejarSeguir.setVisibility(View.GONE);
        }
    }

    private void cargarDatosPerfil() {
        usuariosViewModel.cargarPerfilPorId(idUsuarioPerfil);
        usuariosViewModel.getPerfilUsuario().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil != null) {
                Log.d(TAG, "Datos del perfil cargados para: " + perfil.getNombreUsuario());
                displayProfileData(perfil);
            } else {
                Log.e(TAG, "Perfil no encontrado");
                mostrarErrorPerfil();
            }
        });
    }

    private void verificarEstadoSeguimiento() {
        usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
        usuariosViewModel.getEstaSiguiendo().observe(getViewLifecycleOwner(), this::actualizarBotonesSeguimiento);
    }

    private void cargarPublicacionesUsuario() {
        Log.d(TAG, "Cargando publicaciones del usuario: " + idUsuarioPerfil);
        usuariosViewModel.cargarPublicacionesPorUsuario(idUsuarioPerfil);
    }

    private void setupSeguimientoObservers() {
        // Observer para respuestas de seguimiento
        usuariosViewModel.getRespuestaSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                Log.d(TAG, "Seguimiento exitoso");
                usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
                // Recargar datos del perfil para actualizar contadores
                usuariosViewModel.cargarPerfilPorId(idUsuarioPerfil);
            }
        });

        usuariosViewModel.getRespuestaDejarSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                Log.d(TAG, "Dejar de seguir exitoso");
                usuariosViewModel.verificarSeguimiento(token, idUsuarioPerfil);
                // Recargar datos del perfil para actualizar contadores
                usuariosViewModel.cargarPerfilPorId(idUsuarioPerfil);
            }
        });
    }

    private void mostrarErrorPerfil() {
        if (binding != null) {
            binding.txtNombreCompleto.setText("Perfil no encontrado");
            binding.txtNombreUsuario.setText("");
        }
    }

    private void displayProfileData(UsuarioPerfil perfil) {
        if (binding == null) return;

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

        Log.d(TAG, "Datos del perfil mostrados en UI");
    }

    private void actualizarBotonesSeguimiento(boolean estaSiguiendo) {
        if (binding == null) return;

        Log.d(TAG, "Actualizando botones de seguimiento. Está siguiendo: " + estaSiguiendo);

        if (estaSiguiendo) {
            binding.btnSeguir.setVisibility(View.GONE);
            binding.btnDejarSeguir.setVisibility(View.VISIBLE);
        } else {
            binding.btnSeguir.setVisibility(View.VISIBLE);
            binding.btnDejarSeguir.setVisibility(View.GONE);
        }

        // Configurar listeners
        binding.btnSeguir.setOnClickListener(v -> {
            Log.d(TAG, "Siguiendo usuario: " + idUsuarioPerfil);
            usuariosViewModel.seguirUsuario(token, idUsuarioPerfil);
        });

        binding.btnDejarSeguir.setOnClickListener(v -> {
            Log.d(TAG, "Dejando de seguir usuario: " + idUsuarioPerfil);
            usuariosViewModel.dejarDeSeguirUsuario(token, idUsuarioPerfil);
        });
    }

    private void loadProfileImage(String imagePath) {
        if (binding == null) return;

        if (imagePath != null && !imagePath.isEmpty()) {
            fileServiceClient.downloadImage(imagePath, new FileServiceClient.DownloadCallback() {
                @Override
                public void onSuccess(byte[] imageData, String filename) {
                    Bitmap bitmap = ImageUtil.binaryToBitmap(imageData);
                    if (bitmap != null && binding != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (binding != null) {
                                binding.imgPerfil.setImageBitmap(bitmap);
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Error cargando imagen de perfil", e);
                    if (binding != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (binding != null) {
                                binding.imgPerfil.setImageResource(R.drawable.ic_perfil);
                            }
                        });
                    }
                }
            });
        } else {
            binding.imgPerfil.setImageResource(R.drawable.ic_perfil);
        }
    }

    // Implementación de PublicacionesAdapter.OnItemClickListener
    @Override
    public void onVerMasClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Navegando a ver archivo: " + publicacion.getTitulo());

        try {
            // Crear la acción usando las direcciones del fragment actual
            PerfilUsuarioFragmentDirections.ActionPerfilUsuarioFragmentToNavigationVerArchivo action =
                    PerfilUsuarioFragmentDirections.actionPerfilUsuarioFragmentToNavigationVerArchivo(publicacion);

            // Navegar usando el NavController
            if (navController != null) {
                navController.navigate(action);
            } else {
                // Fallback si navController no está disponible
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al navegar a ver archivo", e);
            Toast.makeText(getContext(), "Error al abrir la publicación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOpcionesClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Click en opciones para publicación: " + publicacion.getTitulo());
        // Método llamado cuando se hace clic en opciones
        // Puede ser usado para analytics o logs
    }

    @Override
    public void onEliminarClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Intentando eliminar publicación: " + publicacion.getTitulo());

        // Solo el usuario propietario de la publicación puede eliminarla
        int idUsuarioActual = SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario();

        if (publicacion.getIdUsuarioRegistrado() == idUsuarioActual) {
            Log.d(TAG, "Usuario autorizado para eliminar publicación");
            // Mostrar diálogo de confirmación para eliminar
            mostrarDialogoConfirmacionEliminacion(publicacion);
        } else {
            Log.w(TAG, "Usuario no autorizado para eliminar publicación");
            Toast.makeText(getContext(), "No tienes permisos para eliminar esta publicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoConfirmacionEliminacion(DocumentoResponse publicacion) {
        if (getContext() == null) return;
    }

    /**
     * Método público para recargar las publicaciones del usuario
     */
    public void recargarPublicaciones() {
        if (idUsuarioPerfil > 0) {
            Log.d(TAG, "Recargando publicaciones del usuario: " + idUsuarioPerfil);
            usuariosViewModel.cargarPublicacionesPorUsuario(idUsuarioPerfil);
        } else {
            Log.w(TAG, "ID de usuario no válido para recargar publicaciones");
        }
    }



    public int getIdUsuarioPerfil() {
        return idUsuarioPerfil;
    }


}
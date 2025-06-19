package com.example.edushareandroid.ui.usuarios;

import android.annotation.SuppressLint;
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
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.ui.publicaciones.PublicacionesAdapter;
import com.example.edushareandroid.utils.SesionUsuario;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        initializeServices();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        setupRecyclerView();
        setupObservers();

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

            if (idUsuarioPerfil != nuevoIdUsuario) {
                Log.d(TAG, "Usuario diferente detectado, limpiando datos anteriores");
                idUsuarioPerfil = nuevoIdUsuario;
            }

            cargarDatosPerfil();
            verificarEstadoSeguimiento();
            cargarPublicacionesUsuario();
        } else {
            Log.e(TAG, "No se encontró ID de usuario en argumentos");
            mostrarErrorPerfil();
        }

        setupSeguimientoObservers();
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
        usuariosViewModel.getRespuestaSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                Log.d(TAG, "Seguimiento exitoso");
                int usuarioLogueadoId = SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario();
                String nombreUsuario = SesionUsuario.obtenerNombreUsuario(requireContext());
                String usuarioDestinoId = String.valueOf(idUsuarioPerfil);
                String titulo = "Nuevo seguidor";
                String mensaje = nombreUsuario + " ha comenzado a seguirte 🎉";
                String tipo = "Acción de seguimiento";
                String fecha = obtenerFechaActual();
                WebSocketManager.getInstance().enviarNotificacionAccion(titulo, mensaje, tipo, fecha, usuarioDestinoId);

                int numeroSeguidores = Integer.parseInt(binding.txtNumSeguidores.getText().toString()) + 1;
                binding.txtNumSeguidores.setText(String.valueOf(numeroSeguidores));

                Toast.makeText(getContext(), "Ahora sigues a este usuario ", Toast.LENGTH_SHORT).show();

                actualizarBotonesSeguimiento(true);
            }
        });

        usuariosViewModel.getRespuestaDejarSeguimiento().observe(getViewLifecycleOwner(), respuesta -> {
            if (respuesta != null && !respuesta.isError()) {
                Log.d(TAG, "Dejar de seguir exitoso");

                int usuarioLogueadoId = SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario();
                String nombreUsuario = SesionUsuario.obtenerNombreUsuario(requireContext());

                String usuarioDestinoId = String.valueOf(idUsuarioPerfil);
                String titulo = "Dejaste de ser seguido";
                String mensaje = "Ya no te sigue " + nombreUsuario;
                String tipo = "Acción de seguimiento";
                String fecha = obtenerFechaActual();
                WebSocketManager.getInstance().enviarNotificacionAccion(titulo, mensaje, tipo, fecha, usuarioDestinoId);

                int numeroSeguidores = Integer.parseInt(binding.txtNumSeguidores.getText().toString()) - 1;
                binding.txtNumSeguidores.setText(String.valueOf(numeroSeguidores));

                Toast.makeText(getContext(), "Has dejado de seguir a este usuario ", Toast.LENGTH_SHORT).show();
                actualizarBotonesSeguimiento(false);
            }
        });
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

        binding.btnSeguir.setOnClickListener(v -> {
            Log.d(TAG, "Siguiendo usuario: " + idUsuarioPerfil);
            usuariosViewModel.seguirUsuario(token, idUsuarioPerfil);
        });

        binding.btnDejarSeguir.setOnClickListener(v -> {
            Log.d(TAG, "Dejando de seguir usuario: " + idUsuarioPerfil);
            usuariosViewModel.dejarDeSeguirUsuario(token, idUsuarioPerfil);
        });
    }

    private String obtenerFechaActual() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formato.format(new Date());
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
    }

    private void mostrarErrorPerfil() {
        if (binding != null) {
            binding.txtNombreCompleto.setText("Perfil no encontrado");
            binding.txtNombreUsuario.setText("");
        }
    }

    @Override
    public void onVerMasClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Navegando a ver archivo: " + publicacion.getTitulo());
        try {
            PerfilUsuarioFragmentDirections.ActionPerfilUsuarioFragmentToNavigationVerArchivo action =
                    PerfilUsuarioFragmentDirections.actionPerfilUsuarioFragmentToNavigationVerArchivo(publicacion);
            navController.navigate(action);
        } catch (Exception e) {
            Log.e(TAG, "Error al navegar a ver archivo", e);
            Toast.makeText(getContext(), "Error al abrir la publicación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOpcionesClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Click en opciones para publicación: " + publicacion.getTitulo());
    }

    @Override
    public void onEliminarClick(DocumentoResponse publicacion) {
        Log.d(TAG, "Intentando eliminar publicación: " + publicacion.getTitulo());

        int idUsuarioActual = SesionUsuario.obtenerDatosUsuario(requireContext()).getIdUsuario();

        if (publicacion.getIdUsuarioRegistrado() == idUsuarioActual) {
            Log.d(TAG, "Usuario autorizado para eliminar publicación");
        } else {
            Log.w(TAG, "Usuario no autorizado para eliminar publicación");
            Toast.makeText(getContext(), "No tienes permisos para eliminar esta publicación", Toast.LENGTH_SHORT).show();
        }
    }

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

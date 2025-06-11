package com.example.edushareandroid.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentPerfilBinding;
import com.example.edushareandroid.model.adapter.ResultadoAdapter;
import com.example.edushareandroid.model.bd.Documento;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

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

    private void mostrarVistaLogueado() {
        // Mostrar elementos
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

        String token = SesionUsuario.obtenerToken(requireContext());

        if (token == null || token.trim().isEmpty()) {
            mostrarVistaDeslogueado();
            SesionUsuario.cerrarSesion(requireContext());
            return;
        }

        perfilViewModel.cargarPerfil(token);
        perfilViewModel.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil != null) {
                binding.txtNombre.setText(perfil.getNombre() + " " + perfil.getPrimerApellido());
                binding.txtUsuario.setText("@" + perfil.getNombreUsuario());
                binding.txtNivelEducativo.setText(perfil.getNivelEducativo());
                binding.txtCarrera.setText(perfil.getNombreInstitucion());
                // Carga de imagen: Glide/Picasso si tienes fotoPerfil (opcional)
            }
        });

        binding.btnEditarPerfil.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_perfil_to_editarPerfilFragment)
        );

        // Cargar publicaciones (a futuro desde ViewModel/API)
        List<Documento> publicacionesUsuario = obtenerPublicacionesDelUsuario();

        ResultadoAdapter adapter = new ResultadoAdapter(requireContext(), publicacionesUsuario, true, documento -> {
            Bundle bundle = new Bundle();
            bundle.putString("titulo", documento.getTitulo());
            bundle.putString("subtitulo", documento.getSubtitulo());
            bundle.putString("detalles", documento.getDetalles());
            bundle.putInt("imagenId", documento.getImagenId());

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
                    .navigate(R.id.action_navigation_perfil_to_navigation_verArchivo, bundle);
        });

        RecyclerView recyclerView = binding.rvPublicaciones;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private List<Documento> obtenerPublicacionesDelUsuario() {
        // TODO: Reemplazar por publicaciones reales desde API o ViewModel
        List<Documento> lista = new ArrayList<>();
        lista.add(new Documento("Matemáticas I", "Guía para parcial", "1.2k Vistas · 23 Páginas · 1h ago", R.drawable.ic_archivo));
        lista.add(new Documento("Historia Universal", "Resumen completo", "600 Vistas · 40 Páginas · 2h ago", R.drawable.ic_archivo));
        lista.add(new Documento("Física Moderna", "Fórmulas clave para examen", "890 Vistas · 15 Páginas · 30min ago", R.drawable.ic_archivo));
        return lista;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

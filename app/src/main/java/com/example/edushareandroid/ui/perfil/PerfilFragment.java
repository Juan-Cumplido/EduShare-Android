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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Navegación al botón "Editar Perfil"
        binding.btnEditarPerfil.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_perfil_to_editarPerfilFragment)
        );

        // Configuración del RecyclerView de publicaciones
        RecyclerView recyclerView = binding.recyclerViewPublicaciones;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Simular publicaciones del usuario (reemplaza esto con tus datos reales)
        List<Documento> publicacionesUsuario = obtenerPublicacionesDelUsuario();

        ResultadoAdapter adapter = new ResultadoAdapter(requireContext(), publicacionesUsuario, true, documento -> {
            // Puedes seguir navegando a la vista de archivo
            Bundle bundle = new Bundle();
            bundle.putString("titulo", documento.getTitulo());
            bundle.putString("subtitulo", documento.getSubtitulo());
            bundle.putString("detalles", documento.getDetalles());
            bundle.putInt("imagenId", documento.getImagenId());

            Navigation.findNavController(requireView()).navigate(R.id.action_navigation_perfil_to_navigation_verArchivo, bundle);
        });
        boolean logueado = SesionUsuario.isUsuarioLogueado(requireContext());

        if (!logueado) {
            binding.tvIniciaSesion.setVisibility(View.VISIBLE);
            binding.recyclerViewPublicaciones.setVisibility(View.GONE);
            binding.btnEditarPerfil.setVisibility(View.GONE);
            binding.btnCerrarSesion.setVisibility(View.GONE);
            binding.bottomButtonsGroup.setVisibility(View.GONE);
            binding.tvNombre.setVisibility(View.GONE);
            binding.tvUsuario.setVisibility(View.GONE);
            binding.tvNivelEducativo.setVisibility(View.GONE);
            binding.tvCarrera.setVisibility(View.GONE);
            binding.tvPublicaciones.setVisibility(View.GONE);
            binding.infoTitle.setVisibility(View.GONE);
            binding.btnSubirPublicacion.setVisibility(View.GONE);
            return root;
        }


        recyclerView.setAdapter(adapter);

        return root;
    }

    private List<Documento> obtenerPublicacionesDelUsuario() {
        // TODO: Reemplaza esto con datos reales de la base o del ViewModel
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

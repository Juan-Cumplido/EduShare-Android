package com.example.edushareandroid.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentHomeBinding;
import com.example.edushareandroid.model.adapter.ResultadoAdapter;
import com.example.edushareandroid.model.base_de_datos.Documento;
import com.example.edushareandroid.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ResultadoAdapter adapter;
    private List<Documento> documentos;
    private List<Documento> documentosFiltrados;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Acción del botón de login
        binding.btnIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // Inicializar RecyclerView
        binding.rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear lista completa
        documentos = new ArrayList<>();
        documentos.add(new Documento("Matemáticas I", "Guía para parcial", "1.2k Vistas · 23 Páginas · 1h ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Historia Universal", "Resumen completo", "600 Vistas · 40 Páginas · 2h ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Física Moderna", "Fórmulas clave para examen", "890 Vistas · 15 Páginas · 30min ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Lengua y Literatura", "Ensayo sobre Borges", "2.3k Vistas · 10 Páginas · 3h ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Biología Celular", "Apuntes y esquemas", "720 Vistas · 32 Páginas · 4h ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Química Orgánica", "Resumen con reacciones", "950 Vistas · 28 Páginas · 1d ago", R.drawable.ic_archivo));
        documentos.add(new Documento("Filosofía", "Resumen de Kant y Nietzsche", "500 Vistas · 12 Páginas · 5h ago", R.drawable.ic_archivo));

        // Inicializar lista filtrada con copia completa
        documentosFiltrados = new ArrayList<>(documentos);

        // Configurar adapter
        adapter = new ResultadoAdapter(requireContext(), documentosFiltrados, documento -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("documentoSeleccionado", documento);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_home_to_navigation_verArchivo, bundle);
        });

        binding.rvResultados.setAdapter(adapter);

        // Configurar lógica de búsqueda
        binding.seavBuscarDocumento.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarDocumentos(query);
                binding.seavBuscarDocumento.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarDocumentos(newText);
                return true;
            }
        });
        root.setOnTouchListener((v, event) -> {
            binding.seavBuscarDocumento.clearFocus();
            return false;
        });
        return root;
    }

    private void filtrarDocumentos(String texto) {
        documentosFiltrados.clear();
        for (Documento doc : documentos) {
            if (doc.getTitulo().toLowerCase().contains(texto.toLowerCase())
                    || doc.getSubtitulo().toLowerCase().contains(texto.toLowerCase())) {
                documentosFiltrados.add(doc);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

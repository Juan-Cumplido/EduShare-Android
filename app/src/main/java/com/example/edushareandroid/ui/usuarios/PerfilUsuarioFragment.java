package com.example.edushareandroid.ui.usuarios;

import android.annotation.SuppressLint;
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
import com.example.edushareandroid.model.bd.Documento;
import com.example.edushareandroid.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;


public class PerfilUsuarioFragment extends Fragment {
    private FragmentPerfilusuarioBinding binding;
    private ResultadoAdapter adapter;
    private List<Documento> documentosFiltrados;

    private List<Documento> documentos;
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentPerfilusuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Inicializar RecyclerView
        binding.rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));

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
            navController.navigate(R.id.action_perfilUsuarioFragment_to_navigation_verArchivo, bundle);
        });

        binding.rvPublicaciones.setAdapter(adapter);
        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre", "Nombre no disponible");
            String institucion = args.getString("institucion", "Institución no disponible");

            // Mostrar en los TextView
            binding.txtNombre.setText("Nombre: " + nombre);
            binding.txtUsuario.setText("Usuario: " + institucion);

            // Puedes actualizar estos otros también si más adelante agregas datos reales
            binding.txtNivel.setText("Nivel educativo: (por definir)");
            binding.txtCarrera.setText("Carrera: (por definir)");
        }



        return root;
    }
}

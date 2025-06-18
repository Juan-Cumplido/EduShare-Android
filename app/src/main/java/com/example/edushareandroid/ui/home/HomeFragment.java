package com.example.edushareandroid.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentHomeBinding;
import com.example.edushareandroid.model.adapter.ResultadoAdapter;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Documento;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.login.LoginActivity;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.publicaciones.PublicacionesAdapter;
import com.example.edushareandroid.ui.publicaciones.PublicacionesViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PublicacionesViewModel publicacionesViewModel;
    private HomeViewModel homeViewModel;
    private PublicacionesAdapter adapter;
    private List<DocumentoResponse> publicacionesCompletas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.btnIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar ViewModels
        publicacionesViewModel = new ViewModelProvider(this).get(PublicacionesViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar búsqueda
        setupSearch();

        // Configurar spinners
        setupSpinners();

        // Cargar datos iniciales
        cargarDatosIniciales();
    }

    private void setupRecyclerView() {
        adapter = new PublicacionesAdapter(new ArrayList<>(), new FileServiceClient(), new PublicacionesAdapter.OnItemClickListener() {
            @Override
            public void onVerMasClick(DocumentoResponse documento) {
                HomeFragmentDirections.ActionNavigationHomeToNavigationVerArchivo action =
                        HomeFragmentDirections.actionNavigationHomeToNavigationVerArchivo(documento);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }

            @Override
            public void onOpcionesClick(DocumentoResponse documento) {
                mostrarMenuOpciones(documento);
            }

            @Override
            public void onEliminarClick(DocumentoResponse documento) {
                confirmarEliminacion(documento);
            }
        },
            requireContext()
        );

        binding.rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvResultados.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.seavBuscarDocumento.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarPublicaciones(query);
                binding.seavBuscarDocumento.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarPublicaciones(newText);
                return true;
            }
        });
    }

    private void setupSpinners() {
        // Configurar spinner de categorías
        ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<Categoria>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreCategoria()); // Asume que existe getNombreCategoria()
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreCategoria());
                return view;
            }
        };
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(categoriaAdapter);
        binding.spinnerCategoria.setPrompt(getString(R.string.selecciona_categoria));

        // Configurar spinner de ramas
        ArrayAdapter<Rama> ramaAdapter = new ArrayAdapter<Rama>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreRama()); // Asume que existe getNombreRama()
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreRama());
                return view;
            }
        };
        ramaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRama.setAdapter(ramaAdapter);
        binding.spinnerRama.setPrompt(getString(R.string.selecciona_rama));

        // Configurar spinner de materias
        ArrayAdapter<Materia> materiaAdapter = new ArrayAdapter<Materia>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreMateria()); // Asume que existe getNombreMateria()
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreMateria());
                return view;
            }
        };
        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMateria.setAdapter(materiaAdapter);
        binding.spinnerMateria.setPrompt(getString(R.string.selecciona_materia));
        // Listeners para los spinners
        binding.spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categoria categoria = (Categoria) parent.getItemAtPosition(position);
                publicacionesViewModel.cargarPublicacionesPorCategoria(categoria.getIdCategoria());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerRama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Rama rama = (Rama) parent.getItemAtPosition(position);
                homeViewModel.cargarMateriasPorRama(rama.getIdRama());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerMateria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Materia materia = (Materia) parent.getItemAtPosition(position);
                // Aquí puedes implementar la carga por materia si lo necesitas
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void cargarDatosIniciales() {
        // Cargar datos para los spinners
        homeViewModel.cargarCategorias();
        homeViewModel.cargarRamas();

        // Observar cambios en categorías
        homeViewModel.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            ArrayAdapter<Categoria> adapter = (ArrayAdapter<Categoria>) binding.spinnerCategoria.getAdapter();
            adapter.clear();
            adapter.addAll(categorias);
            adapter.notifyDataSetChanged();
        });

        // Observar cambios en ramas
        homeViewModel.getRamas().observe(getViewLifecycleOwner(), ramas -> {
            ArrayAdapter<Rama> adapter = (ArrayAdapter<Rama>) binding.spinnerRama.getAdapter();
            adapter.clear();
            adapter.addAll(ramas);
            adapter.notifyDataSetChanged();
        });

        // Observar cambios en materias
        homeViewModel.getMaterias().observe(getViewLifecycleOwner(), materias -> {
            ArrayAdapter<Materia> adapter = (ArrayAdapter<Materia>) binding.spinnerMateria.getAdapter();
            adapter.clear();
            adapter.addAll(materias);
            adapter.notifyDataSetChanged();
        });
        // Observar cambios en las publicaciones
        publicacionesViewModel.getPublicacionesLiveData().observe(getViewLifecycleOwner(), publicaciones -> {
            if (publicaciones != null && !publicaciones.isEmpty()) {
                publicacionesCompletas = new ArrayList<>(publicaciones);
                adapter.actualizarLista(publicaciones);
                binding.rvResultados.setVisibility(View.VISIBLE);
                binding.txtSinResultados.setVisibility(View.GONE);
            } else {
                adapter.actualizarLista(new ArrayList<>());
                binding.rvResultados.setVisibility(View.GONE);
                binding.txtSinResultados.setVisibility(View.VISIBLE);
            }
        });

        // Observar estado de carga
        publicacionesViewModel.getCargandoLiveData().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando != null && cargando) {
                //binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                //binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Cargar todas las publicaciones inicialmente
        publicacionesViewModel.cargarTodasLasPublicaciones();
    }

    private void filtrarPublicaciones(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            // Si no hay texto, mostrar todas las publicaciones
            adapter.actualizarLista(publicacionesCompletas);
        } else {
            // Filtrar por texto
            List<DocumentoResponse> publicacionesFiltradas = new ArrayList<>();
            String textoLower = texto.toLowerCase();

            for (DocumentoResponse publicacion : publicacionesCompletas) {
                if (publicacion.getTitulo().toLowerCase().contains(textoLower) ||
                        publicacion.getResuContenido().toLowerCase().contains(textoLower) ||
                        publicacion.getNombreCompleto().toLowerCase().contains(textoLower)) {
                    publicacionesFiltradas.add(publicacion);
                }
            }

            adapter.actualizarLista(publicacionesFiltradas);

            if (publicacionesFiltradas.isEmpty()) {
                binding.txtSinResultados.setVisibility(View.VISIBLE);
                binding.txtSinResultados.setText("No se encontraron publicaciones para: " + texto);
            } else {
                binding.txtSinResultados.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarMenuOpciones(DocumentoResponse documento) {
        // Implementa el menú de opciones aquí
    }

    private void confirmarEliminacion(DocumentoResponse documento) {
        // Implementa la confirmación de eliminación aquí
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
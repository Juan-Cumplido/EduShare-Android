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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentHomeBinding;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.login.LoginActivity;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.ui.home.HomePublicacionesAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private HomePublicacionesAdapter adapter;
    private List<DocumentoResponse> publicacionesCompletas = new ArrayList<>();
    private List<DocumentoResponse> publicacionesFiltradas = new ArrayList<>();

    private Categoria categoriaSeleccionada = null;
    private Rama ramaSeleccionada = null;
    private Materia materiaSeleccionada = null;

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

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupSpinners();
        setupObservers();
        cargarDatosIniciales();
    }

    private void setupRecyclerView() {
        adapter = new HomePublicacionesAdapter(new ArrayList<>(), new FileServiceClient(), new HomePublicacionesAdapter.OnHomeItemClickListener() {
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

            @Override
            public void onCompartirClick(DocumentoResponse documento) {
                compartirPublicacion(documento);
            }

            @Override
            public void onFavoritoClick(DocumentoResponse documento) {
                toggleFavorito(documento);
            }
        }, requireContext());

        binding.rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvResultados.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.seavBuscarDocumento.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarPublicacionesPorTexto(query);
                binding.seavBuscarDocumento.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarPublicacionesPorTexto(newText);
                return true;
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<Categoria>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreCategoria());
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

        ArrayAdapter<Rama> ramaAdapter = new ArrayAdapter<Rama>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreRama());
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

        ArrayAdapter<Materia> materiaAdapter = new ArrayAdapter<Materia>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getNombreMateria());
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

        setupSpinnerListeners();
    }

    private void setupSpinnerListeners() {
        binding.spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = (Categoria) parent.getItemAtPosition(position);
                aplicarFiltrosSeleccionados();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoriaSeleccionada = null;
                aplicarFiltrosSeleccionados();
            }
        });

        binding.spinnerRama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ramaSeleccionada = (Rama) parent.getItemAtPosition(position);
                homeViewModel.cargarMateriasPorRama(ramaSeleccionada.getIdRama());
                aplicarFiltrosSeleccionados();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ramaSeleccionada = null;
                // Limpiar materias
                ArrayAdapter<Materia> adapter = (ArrayAdapter<Materia>) binding.spinnerMateria.getAdapter();
                adapter.clear();
                adapter.notifyDataSetChanged();
                aplicarFiltrosSeleccionados();
            }
        });

        binding.spinnerMateria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                materiaSeleccionada = (Materia) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                materiaSeleccionada = null;
            }
        });
    }

    private void setupObservers() {
        homeViewModel.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            if (categorias != null) {
                ArrayAdapter<Categoria> adapter = (ArrayAdapter<Categoria>) binding.spinnerCategoria.getAdapter();
                adapter.clear();
                adapter.addAll(categorias);
                adapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getRamas().observe(getViewLifecycleOwner(), ramas -> {
            if (ramas != null) {
                ArrayAdapter<Rama> adapter = (ArrayAdapter<Rama>) binding.spinnerRama.getAdapter();
                adapter.clear();
                adapter.addAll(ramas);
                adapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getMaterias().observe(getViewLifecycleOwner(), materias -> {
            if (materias != null) {
                ArrayAdapter<Materia> adapter = (ArrayAdapter<Materia>) binding.spinnerMateria.getAdapter();
                adapter.clear();
                adapter.addAll(materias);
                adapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getPublicaciones().observe(getViewLifecycleOwner(), publicaciones -> {
            if (publicaciones != null) {
                publicacionesCompletas = new ArrayList<>(publicaciones);
                publicacionesFiltradas = new ArrayList<>(publicaciones);

                String textoActual = binding.seavBuscarDocumento.getQuery().toString();
                if (!textoActual.isEmpty()) {
                    filtrarPublicacionesPorTexto(textoActual);
                } else {
                    actualizarVistaPublicaciones(publicaciones);
                }
            } else {
                publicacionesCompletas.clear();
                publicacionesFiltradas.clear();
                actualizarVistaPublicaciones(new ArrayList<>());
            }
        });

        homeViewModel.getCargandoPublicaciones().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando != null) {
            }
        });

        homeViewModel.getMensajeError().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getMensajeExito().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
            }
        });

        homeViewModel.getFiltrosActivos().observe(getViewLifecycleOwner(), filtrosActivos -> {
        });
    }

    private void cargarDatosIniciales() {
        homeViewModel.cargarCategorias();
        homeViewModel.cargarRamas();

    }

    private void aplicarFiltrosSeleccionados() {
        Integer idCategoria = categoriaSeleccionada != null ? categoriaSeleccionada.getIdCategoria() : null;
        Integer idRama = ramaSeleccionada != null ? ramaSeleccionada.getIdRama() : null;

        if (idCategoria != null || idRama != null) {
            homeViewModel.aplicarFiltros(idCategoria, idRama);
        } else {
            homeViewModel.cargarTodasLasPublicaciones();
        }
    }

    private void filtrarPublicacionesPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            actualizarVistaPublicaciones(publicacionesCompletas);
        } else {
            List<DocumentoResponse> publicacionesFiltradas = new ArrayList<>();
            String textoLower = texto.toLowerCase().trim();

            for (DocumentoResponse publicacion : publicacionesCompletas) {
                if (publicacion.getTitulo().toLowerCase().contains(textoLower) ||
                        publicacion.getResuContenido().toLowerCase().contains(textoLower) ||
                        publicacion.getNombreCompleto().toLowerCase().contains(textoLower)) {
                    publicacionesFiltradas.add(publicacion);
                }
            }

            actualizarVistaPublicaciones(publicacionesFiltradas);
        }
    }

    private void actualizarVistaPublicaciones(List<DocumentoResponse> publicaciones) {
        if (publicaciones != null && !publicaciones.isEmpty()) {
            adapter.actualizarLista(publicaciones);
            binding.rvResultados.setVisibility(View.VISIBLE);
            binding.txtSinResultados.setVisibility(View.GONE);
        } else {
            adapter.actualizarLista(new ArrayList<>());
            binding.rvResultados.setVisibility(View.GONE);
            binding.txtSinResultados.setVisibility(View.VISIBLE);

            String textoActual = binding.seavBuscarDocumento.getQuery().toString();
            if (!textoActual.isEmpty()) {
                binding.txtSinResultados.setText("No se encontraron publicaciones para: " + textoActual);
            } else {
                binding.txtSinResultados.setText("No hay publicaciones disponibles");
            }
        }
    }

    public void limpiarFiltros() {
        binding.spinnerCategoria.setSelection(AdapterView.INVALID_POSITION);
        binding.spinnerRama.setSelection(AdapterView.INVALID_POSITION);
        binding.spinnerMateria.setSelection(AdapterView.INVALID_POSITION);

        binding.seavBuscarDocumento.setQuery("", false);

        homeViewModel.limpiarFiltros();

        categoriaSeleccionada = null;
        ramaSeleccionada = null;
        materiaSeleccionada = null;
    }

    public void refrescarPublicaciones() {
        homeViewModel.refrescarPublicaciones();
    }

    private void compartirPublicacion(DocumentoResponse documento) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, documento.getTitulo());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Revisa este documento: " + documento.getTitulo() + "\n" + documento.getResuContenido());
        startActivity(Intent.createChooser(shareIntent, "Compartir documento"));
    }

    private void toggleFavorito(DocumentoResponse documento) {
        Toast.makeText(getContext(), "Funcionalidad de favoritos próximamente", Toast.LENGTH_SHORT).show();
    }

    private void mostrarMenuOpciones(DocumentoResponse documento) {
    }

    private void confirmarEliminacion(DocumentoResponse documento) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.cleanup();
        }
        binding = null;
    }
}
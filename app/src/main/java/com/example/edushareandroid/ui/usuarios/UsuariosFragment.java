package com.example.edushareandroid.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentUsuariosBinding;
import com.example.edushareandroid.model.home.adapter.UsuarioAdapter;
import com.example.edushareandroid.model.home.bd.Usuario;
import com.example.edushareandroid.model.home.bd.UsuarioConInstitucion;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;
    private UsuarioAdapter adapter;
    private List<UsuarioConInstitucion> listaCompleta;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        boolean usuarioAutenticado = SesionUsuario.isUsuarioLogueado(requireContext());

        if (usuarioAutenticado) {
            // Mostrar elementos
            binding.textNoSesion.setVisibility(View.GONE);
            binding.searchView.setVisibility(View.VISIBLE);
            binding.recyclerViewResults.setVisibility(View.VISIBLE);

            configurarRecyclerView();
            cargarUsuarios();

            binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filtrar(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filtrar(newText);
                    return true;
                }
            });

        } else {
            // Ocultar elementos y mostrar mensaje
            binding.textNoSesion.setVisibility(View.VISIBLE);
            binding.searchView.setVisibility(View.GONE);
            binding.recyclerViewResults.setVisibility(View.GONE);
        }

        return view;
    }

    private void configurarRecyclerView() {
        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UsuarioAdapter(new ArrayList<>(), usuario -> {
            Bundle bundle = new Bundle();
            bundle.putInt("usuarioId", usuario.getUsuario().getIdUsuario());
            bundle.putString("nombre", usuario.getNombreCompleto());
            bundle.putString("institucion", usuario.getNombreInstitucion());

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_usuarios_to_perfilUsuarioFragment, bundle);
        });

        binding.recyclerViewResults.setAdapter(adapter);
    }

    private void cargarUsuarios() {
        listaCompleta = obtenerUsuarios();
        adapter.actualizarLista(listaCompleta);
    }

    private void filtrar(String texto) {
        List<UsuarioConInstitucion> filtrados = new ArrayList<>();
        for (UsuarioConInstitucion u : listaCompleta) {
            if (u.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(u);
            }
        }
        adapter.actualizarLista(filtrados);
    }

    private List<UsuarioConInstitucion> obtenerUsuarios() {
        List<UsuarioConInstitucion> lista = new ArrayList<>();

        Usuario u1 = new Usuario(1, "Ana", "García", "Lopez", R.drawable.avatar_1, 1, 1);
        lista.add(new UsuarioConInstitucion(u1, "Universidad Nacional"));

        Usuario u2 = new Usuario(2, "Luis", "Pérez", "Martínez", R.drawable.avatar_2, 2, 2);
        lista.add(new UsuarioConInstitucion(u2, "Instituto Tecnológico"));

        return lista;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

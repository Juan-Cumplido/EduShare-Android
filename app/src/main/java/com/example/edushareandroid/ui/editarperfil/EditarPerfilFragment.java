package com.example.edushareandroid.ui.editarperfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentEditarperfilBinding;
import com.example.edushareandroid.model.adapter.FotoAdapter;
import com.example.edushareandroid.ui.recuperarcontrasenia.RecoverypasswordActivity;

public class EditarPerfilFragment extends Fragment {
    private FragmentEditarperfilBinding binding;
    private boolean mostrandoSelector = false;

    private final int[] imagenesDisponibles = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        EditarPerfilViewModel editarPerfilViewModel =
                new ViewModelProvider(this).get(EditarPerfilViewModel.class);

        binding = FragmentEditarperfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.rvFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        FotoAdapter adapter = new FotoAdapter(imagenesDisponibles, resId -> {
            binding.imgFotoPerfil.setImageResource(resId);
            binding.rvFotos.setVisibility(View.GONE);
            mostrandoSelector = false;
        });
        binding.rvFotos.setAdapter(adapter);
        binding.btnSeleccionarFoto.setOnClickListener(v -> {
            mostrandoSelector = !mostrandoSelector;
            binding.rvFotos.setVisibility(mostrandoSelector ? View.VISIBLE : View.GONE);
        });

        // Botón para cambiar contraseña
        binding.btnCambiarContraseA.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RecoverypasswordActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

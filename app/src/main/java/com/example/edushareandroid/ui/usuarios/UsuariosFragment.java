package com.example.edushareandroid.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.edushareandroid.databinding.FragmentUsuariosBinding;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UsuariosViewModel usuariosViewModel =
                new ViewModelProvider(this).get(UsuariosViewModel.class);

        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUsuarios;
        usuariosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
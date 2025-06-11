package com.example.edushareandroid.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.databinding.FragmentNotificationsBinding;
import com.example.edushareandroid.model.adapter.NotificacionAdapter;
import com.example.edushareandroid.model.bd.Notificacion;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        boolean usuarioAutenticado = SesionUsuario.isUsuarioLogueado(requireContext());

        if (usuarioAutenticado) {
            binding.rvNotificaciones.setVisibility(View.VISIBLE);
            binding.txtNotificaciones.setVisibility(View.GONE);

            // Simular datos
            List<Notificacion> lista = obtenerNotificacionesEjemplo();
            NotificacionAdapter adapter = new NotificacionAdapter(lista);
            binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvNotificaciones.setAdapter(adapter);
        } else {
            binding.rvNotificaciones.setVisibility(View.GONE);
            binding.txtNotificaciones.setVisibility(View.VISIBLE);
            binding.txtNotificaciones.setText("Debes iniciar sesión para ver tus notificaciones.");
        }

        return root;
    }

    private List<Notificacion> obtenerNotificacionesEjemplo() {
        List<Notificacion> notificaciones = new ArrayList<>();
        notificaciones.add(new Notificacion("Nuevo seguidor", "Juan ahora te sigue", "Hace 2 minutos"));
        notificaciones.add(new Notificacion("Comentario", "Ana comentó tu publicación", "Hace 1 hora"));
        notificaciones.add(new Notificacion("Actualización", "Tu documento fue aprobado", "Ayer"));
        return notificaciones;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

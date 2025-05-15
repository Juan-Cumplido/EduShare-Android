package com.example.edushareandroid.ui.chat;

import android.os.Bundle;

import com.example.edushareandroid.model.home.adapter.ChatMessageAdapter;
import com.example.edushareandroid.model.home.bd.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.databinding.FragmentVistachatBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VistaChatFragment extends Fragment {

    private FragmentVistachatBinding binding;
    private ChatMessageAdapter adapter;
    private List<Message> mensajes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVistachatBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            String titulo = getArguments().getString("titulo");
            String descripcion = getArguments().getString("descripcion");
            String fecha = getArguments().getString("fecha");
            String hora = getArguments().getString("hora");
            String usuario = getArguments().getString("usuario");

            binding.tvTituloDetalle.setText(titulo);
            binding.tvDescripcionDetalle.setText(descripcion);
            binding.tvFechaHoraDetalle.setText(fecha + " " + hora);
            binding.tvUsuarioDetalle.setText(usuario);
        }

        setupChat();

        return binding.getRoot();
    }

    private void setupChat() {
        // Mensajes predefinidos
        mensajes.clear(); // Opcional, por si se llama varias veces

        // Mensajes predefinidos con variedad de longitud, tono y tipo
        mensajes.add(new Message("1", "Pedro", "¡Hola! ¿Cómo estás hoy? 😊", "2025-05-14 12:00", false));
        mensajes.add(new Message("2", "Yo", "Bien, gracias. Un poco ocupado con la universidad. ¿Y tú?", "2025-05-14 12:01", true));
        mensajes.add(new Message("3", "Pedro", "También algo atareado, pero avanzando con el proyecto de redes.", "2025-05-14 12:02", false));
        mensajes.add(new Message("4", "Yo", "¡Qué bueno! Si necesitas ayuda con la parte del informe, avísame.", "2025-05-14 12:03", true));
        mensajes.add(new Message("5", "Pedro", "Gracias 🙌. Justo estaba pensando en eso, ¿te parece si revisamos juntos mañana?", "2025-05-14 12:04", false));
        mensajes.add(new Message("6", "Yo", "Sí, sin problema. ¿Tipo 10 am?", "2025-05-14 12:05", true));
        mensajes.add(new Message("7", "Pedro", "Perfecto, agendado. ¡Nos vemos mañana!", "2025-05-14 12:06", false));
        mensajes.add(new Message("8", "Yo", "Nos vemos 👋", "2025-05-14 12:07", true));

        adapter = new ChatMessageAdapter(mensajes);
        binding.recyclerMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerMensajes.setAdapter(adapter);
        binding.recyclerMensajes.scrollToPosition(mensajes.size() - 1);

        binding.btnEnviarMensaje.setOnClickListener(v -> {
            String nuevoMensaje = binding.etMensaje.getText().toString().trim();
            if (!nuevoMensaje.isEmpty()) {
                // Aquí generas los valores requeridos para el constructor
                String id = String.valueOf(System.currentTimeMillis()); // o UUID.randomUUID().toString()
                String autor = "Yo"; // o el nombre del usuario actual si lo tienes
                String fecha = obtenerFechaActual(); // función que devuelve "2025-05-14 12:07"
                boolean esPropio = true;

                mensajes.add(new Message(id, autor, nuevoMensaje, fecha, esPropio));
                adapter.notifyItemInserted(mensajes.size() - 1);
                binding.recyclerMensajes.scrollToPosition(mensajes.size() - 1);
                binding.etMensaje.setText("");
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

}

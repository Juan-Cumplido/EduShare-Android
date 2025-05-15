package com.example.edushareandroid.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentChatBinding;
import com.example.edushareandroid.model.home.adapter.ChatAdapter;
import com.example.edushareandroid.model.home.bd.AgendaChat;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private final List<AgendaChat> chatList = new ArrayList<>();
    private ChatAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        configurarRecyclerView();
        cargarDatosSimulados(); // solo se llama una vez

        ProgramarChatViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProgramarChatViewModel.class);
        viewModel.getChatProgramado().observe(getViewLifecycleOwner(), nuevoChat -> {
            if (nuevoChat != null) {
                chatList.add(nuevoChat);
                adapter.notifyItemInserted(chatList.size() - 1);
            }
        });

        binding.btnProgramarChat.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_chat_to_programarChatFragment);
        });

        return root;
    }

    private void configurarRecyclerView() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        adapter = new ChatAdapter(chatList, navController);
        binding.recyclerViewAgendaChats.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAgendaChats.setAdapter(adapter);
    }


    private void cargarDatosSimulados() {
        chatList.clear(); // limpia para evitar duplicados
        chatList.add(new AgendaChat("1", "Reunión de clase", "Revisión de tareas y dudas", "15/05/2025", "17:00", "Juan"));
        chatList.add(new AgendaChat("2", "Estudio de biología", "Capítulo 3 del libro", "16/05/2025", "10:30", "María"));
        chatList.add(new AgendaChat("3", "Grupo de lectura", "Discusión sobre ensayo", "17/05/2025", "19:45", "Pedro"));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

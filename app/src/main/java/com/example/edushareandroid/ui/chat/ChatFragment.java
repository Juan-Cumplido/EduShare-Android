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
import com.example.edushareandroid.model.adapter.ChatAdapter;
import com.example.edushareandroid.model.bd.AgendaChat;
import com.example.edushareandroid.ui.programarchatfragment.ProgramarChatViewModel;
import com.example.edushareandroid.utils.SesionUsuario;

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

        boolean usuarioAutenticado = SesionUsuario.isUsuarioLogueado(requireContext());

        if (usuarioAutenticado) {
            binding.txtNoSesion.setVisibility(View.GONE);
            binding.txtChat.setVisibility(View.VISIBLE);
            binding.rvAgendaChats.setVisibility(View.VISIBLE);
            binding.btnProgramarChat.setVisibility(View.VISIBLE);

            configurarRecyclerView();
            cargarDatosSimulados();

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

        } else {
            binding.txtNoSesion.setVisibility(View.VISIBLE);
            binding.txtChat.setVisibility(View.GONE);
            binding.rvAgendaChats.setVisibility(View.GONE);
            binding.btnProgramarChat.setVisibility(View.GONE);
        }

        return root;
    }

    private void configurarRecyclerView() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        adapter = new ChatAdapter(chatList, navController);
        binding.rvAgendaChats.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAgendaChats.setAdapter(adapter);
    }

    private void cargarDatosSimulados() {
        chatList.clear();
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

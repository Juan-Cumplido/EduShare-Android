package com.example.edushareandroid.ui.programarchatfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.ui.chat.AgendaChat;

import java.util.UUID;

public class ProgramarChatViewModel extends ViewModel {

    private final MutableLiveData<AgendaChat> chatProgramado = new MutableLiveData<>();

    public void programarChat(String titulo, String descripcion, String fecha, String hora, String usuario) {
        String id = UUID.randomUUID().toString();

        AgendaChat nuevoChat = new AgendaChat(id, titulo, descripcion, fecha, hora, usuario);
        chatProgramado.setValue(nuevoChat);
    }

    public LiveData<AgendaChat> getChatProgramado() {
        return chatProgramado;
    }
}

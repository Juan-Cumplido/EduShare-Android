package com.example.edushareandroid.ui.verarchivo.comentarios;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ComentarioDiffCallback extends DiffUtil.Callback {
    private final List<Comentario> oldList;
    private final List<Comentario> newList;

    public ComentarioDiffCallback(List<Comentario> oldList, List<Comentario> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getIdComentario() ==
                newList.get(newItemPosition).getIdComentario();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
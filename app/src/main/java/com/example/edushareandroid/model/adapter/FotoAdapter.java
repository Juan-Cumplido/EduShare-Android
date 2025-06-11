package com.example.edushareandroid.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {

    private final int[] imagenes;
    private final OnImageSelectedListener listener;

    public interface OnImageSelectedListener {
        void onImageSelected(int resId);
    }

    public FotoAdapter(int[] imagenes, OnImageSelectedListener listener) {
        this.imagenes = imagenes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        int resId = imagenes[position];
        holder.imageView.setImageResource(resId);
        holder.imageView.setOnClickListener(v -> listener.onImageSelected(resId));
    }

    @Override
    public int getItemCount() {
        return imagenes.length;
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_foto);
        }
    }
}

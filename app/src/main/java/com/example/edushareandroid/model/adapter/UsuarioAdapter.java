package com.example.edushareandroid.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.bd.Usuario;
import com.example.edushareandroid.model.bd.UsuarioConInstitucion;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<UsuarioConInstitucion> listaUsuarios;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UsuarioConInstitucion usuario);
    }

    public UsuarioAdapter(List<UsuarioConInstitucion> listaUsuarios, OnItemClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioConInstitucion usuarioConInstitucion = listaUsuarios.get(position);
        Usuario usuario = usuarioConInstitucion.getUsuario();

        holder.tvNombreCompleto.setText(usuarioConInstitucion.getNombreCompleto());
        holder.tvInstitucion.setText(usuarioConInstitucion.getNombreInstitucion());

        // Imagen si tienes librería como Glide o Picasso
        // Glide.with(holder.itemView.getContext()).load(usuario.getFotoRuta()).into(holder.imgFotoPerfil);

        holder.buttonVerMas.setOnClickListener(v -> listener.onItemClick(usuarioConInstitucion));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void actualizarLista(List<UsuarioConInstitucion> nuevaLista) {
        this.listaUsuarios = nuevaLista;
        notifyDataSetChanged();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCompleto, tvInstitucion;
        ImageView imgFotoPerfil;
        ImageButton buttonVerMas;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tvNombreCompleto);
            tvInstitucion = itemView.findViewById(R.id.tvInstitucion);
            imgFotoPerfil = itemView.findViewById(R.id.imgFotoPerfil);
            buttonVerMas = itemView.findViewById(R.id.buttonVerMas);
        }
    }
}

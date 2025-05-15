    package com.example.edushareandroid.model.home.adapter;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageButton;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.edushareandroid.R;
    import com.example.edushareandroid.model.home.bd.Comentario;

    import java.util.List;

    public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {
        private List<Comentario> lista;
        private OnComentarioDeleteListener deleteListener;

        public interface OnComentarioDeleteListener {
            void onComentarioDelete(int position);
        }

        public ComentarioAdapter(List<Comentario> lista, OnComentarioDeleteListener deleteListener) {
            this.lista = lista;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View vista = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comentario, parent, false);
            return new ComentarioViewHolder(vista);
        }

        @Override
        public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
            Comentario comentario = lista.get(position);
            holder.tvNombreUsuario.setText(comentario.getNombreUsuario());
            holder.tvFechaComentario.setText(comentario.getFecha());
            holder.tvTextoComentario.setText(comentario.getTexto());

            if ("Tú".equals(comentario.getNombreUsuario())) {
                holder.btnEliminarComentario.setVisibility(View.VISIBLE);
                holder.btnEliminarComentario.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onComentarioDelete(holder.getAdapterPosition());
                    }
                });
            } else {
                holder.btnEliminarComentario.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        static class ComentarioViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreUsuario, tvFechaComentario, tvTextoComentario;
            ImageButton btnEliminarComentario;

            public ComentarioViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
                tvFechaComentario = itemView.findViewById(R.id.tvFechaComentario);
                tvTextoComentario = itemView.findViewById(R.id.tvTextoComentario);
                btnEliminarComentario = itemView.findViewById(R.id.btnEliminarComentario); // este botón se agregará en el siguiente paso
            }
        }
    }

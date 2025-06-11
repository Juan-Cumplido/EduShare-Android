package com.example.edushareandroid.ui.verarchivo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentVerarchivoBinding;
import com.example.edushareandroid.model.adapter.ComentarioAdapter;
import com.example.edushareandroid.model.bd.Comentario;
import com.example.edushareandroid.model.bd.Documento;
import com.example.edushareandroid.utils.SesionUsuario;

import java.util.ArrayList;
import java.util.List;

public class VerArchivoFragment extends Fragment {
    private FragmentVerarchivoBinding binding;
    private List<Comentario> comentarios = new ArrayList<>();
    private ComentarioAdapter adapter;
    private boolean liked = false;
    private int likeCount = 0;
    private boolean estaLogueado;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVerarchivoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Verificar si el usuario está logueado
        estaLogueado = SesionUsuario.isUsuarioLogueado(requireContext());

        // Mostrar u ocultar advertencia
        binding.txtAdvertencia.setVisibility(estaLogueado ? View.GONE : View.VISIBLE);

        // Deshabilitar botón de descarga si no está logueado
        binding.btnDescargarDocumento.setEnabled(estaLogueado);

        // Ocultar elementos de comentarios si no está logueado
        if (!estaLogueado) {
            binding.clNuevoComentario.setVisibility(View.GONE);
        }
        // Obtener datos del documento
        Documento documento = null;
        Bundle args = getArguments();
        if (args != null) {
            documento = (Documento) args.getSerializable("documentoSeleccionado");
            if (documento != null) {
                binding.txtTituloDocumento.setText(documento.getTitulo());
                binding.txtSubtitulo.setText(documento.getSubtitulo());
                binding.txtDetallesExtra.setText(documento.getDetalles());
            }
        }

        // Comentarios de prueba
        comentarios.add(new Comentario("Ana", "Hace 2 horas", "Excelente documento, gracias por compartir."));
        comentarios.add(new Comentario("Carlos", "Hace 45 minutos", "Muy útil para la clase."));
        comentarios.add(new Comentario("Luisa", "Hace 10 minutos", "¿Dónde puedo encontrar más de este tema?"));

        // Adapter de comentarios con opción de eliminar solo si está logueado
        adapter = new ComentarioAdapter(comentarios, position -> {
            if (estaLogueado) {
                eliminarComentario(position);
            } else {
                Toast.makeText(getContext(), "Inicia sesión para eliminar comentarios", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComentarios.setAdapter(adapter);

        // URL de ejemplo
        String urlDocumento = "https://www.diputados.gob.mx/LeyesBiblio/pdf/CPEUM.pdf";

        // Abrir documento
        binding.btnAbrirDocumento.setOnClickListener(v -> abrirDocumentoExterno(urlDocumento));

        // Descargar documento
        binding.btnDescargarDocumento.setOnClickListener(v -> descargarConDownloadManager(urlDocumento));

        // Manejo de comentarios
        binding.btnEnviarComentario.setOnClickListener(v -> {
            if (estaLogueado) {
                agregarComentario();
            } else {
                Toast.makeText(getContext(), "Inicia sesión para comentar", Toast.LENGTH_SHORT).show();
            }
        });

        // Manejo de likes
        binding.btnLike.setOnClickListener(v -> {
            if (estaLogueado) {
                toggleLike();
            } else {
                Toast.makeText(getContext(), "Inicia sesión para dar like", Toast.LENGTH_SHORT).show();
            }
        });

        actualizarLikeUI();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void abrirDocumentoExterno(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No se pudo abrir el documento", Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarConDownloadManager(String urlDocumento) {
        try {
            Uri uri = Uri.parse(urlDocumento);
            String nombreArchivo = uri.getLastPathSegment();
            if (nombreArchivo == null || nombreArchivo.isEmpty()) {
                nombreArchivo = "archivo_descargado.pdf";
            }

            String extension = MimeTypeMap.getFileExtensionFromUrl(urlDocumento);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Descargando documento");
            request.setDescription(nombreArchivo);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo);

            if (mimeType != null) {
                request.setMimeType(mimeType);
            }

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            Toast.makeText(getContext(), "Descarga iniciada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al iniciar descarga", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarComentario() {
        String texto = binding.edtNuevoComentario.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(getContext(), "Escribe un comentario", Toast.LENGTH_SHORT).show();
            return;
        }

        Comentario nuevo = new Comentario("Tú", "Ahora", texto);
        comentarios.add(nuevo);
        adapter.notifyItemInserted(comentarios.size() - 1);
        binding.edtNuevoComentario.setText("");
        binding.rvComentarios.scrollToPosition(comentarios.size() - 1);
    }

    private void toggleLike() {
        liked = !liked;
        likeCount += liked ? 1 : -1;
        actualizarLikeUI();
    }

    private void actualizarLikeUI() {
        binding.txtContadorMeGusta.setText(String.valueOf(likeCount));
        int drawableRes = liked ? R.drawable.ic_like_filled : R.drawable.ic_like_outline;
        binding.btnLike.setBackground(ContextCompat.getDrawable(requireContext(), drawableRes));
    }

    private void eliminarComentario(int position) {
        comentarios.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(getContext(), "Comentario eliminado", Toast.LENGTH_SHORT).show();
    }
}

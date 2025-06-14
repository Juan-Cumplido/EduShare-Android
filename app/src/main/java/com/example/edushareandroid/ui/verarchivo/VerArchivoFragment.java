package com.example.edushareandroid.ui.verarchivo;

import static android.telephony.PhoneNumberUtils.formatNumber;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentVerarchivoBinding;
import com.example.edushareandroid.model.base_de_datos.Login.UsuarioData;
import com.example.edushareandroid.model.base_de_datos.comentarios.Comentario;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.ui.perfil.DocumentoResponse;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.SesionUsuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class VerArchivoFragment extends Fragment {
    private FragmentVerarchivoBinding binding;
    private final List<Comentario> comentarios = Collections.synchronizedList(new ArrayList<>());
    private ComentarioAdapter adapter;
    private boolean liked = false;
    private int likeCount = 0;
    private boolean estaLogueado;
    private FileServiceClient fileServiceClient;
    private VerArchivoViewModel viewModel;
    private File archivoTemporalPdf = null;
    private static final int REQUEST_CODE_CREATE_DOCUMENT = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVerarchivoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(VerArchivoViewModel.class);
        estaLogueado = SesionUsuario.isUsuarioLogueado(requireContext());

        inicializarUI();
        configurarDocumento();
        configurarComentarios();
        configurarListeners();

        actualizarLikeUI();
        return binding.getRoot();
    }

    private void inicializarUI() {
        binding.txtAdvertencia.setVisibility(estaLogueado ? View.GONE : View.VISIBLE);
        binding.btnDescargarDocumento.setEnabled(estaLogueado);
        binding.clNuevoComentario.setVisibility(estaLogueado ? View.VISIBLE : View.GONE);
    }

    private void configurarComentarios() {
        DocumentoResponse documento = VerArchivoFragmentArgs.fromBundle(getArguments()).getDocumento();
        UsuarioData usuario = SesionUsuario.obtenerDatosUsuario(requireContext());
        int idUsuarioLogueado = usuario != null ? usuario.getIdUsuario() : -1;
        Log.d("ComentarioAdapter", "ID logueado: " + idUsuarioLogueado);
        if (documento == null) return;
        int idPublicacion = documento.getIdPublicacion();
        FileServiceClient fileServiceClient = new FileServiceClient();
        adapter = new ComentarioAdapter(requireContext(), new ArrayList<>(),idUsuarioLogueado, posicion -> {
            Comentario comentario = adapter.getComentarios().get(posicion);
            viewModel.eliminarComentario(requireContext(), comentario.getIdComentario())
                    .observe(getViewLifecycleOwner(), respuesta -> {
                        if (!respuesta.isError()) {
                            Toast.makeText(getContext(), "Comentario eliminado", Toast.LENGTH_SHORT).show();
                            viewModel.cargarComentarios(idPublicacion);
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar comentario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }, fileServiceClient);
        binding.rvComentarios.setAdapter(adapter);

        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComentarios.setAdapter(adapter);

        viewModel.getComentarios().observe(getViewLifecycleOwner(), nuevosComentarios -> {
            if (nuevosComentarios != null) {
                comentarios.clear();
                comentarios.addAll(nuevosComentarios);
                adapter.setComentarios(new ArrayList<>(comentarios));
                binding.rvComentarios.scrollToPosition(comentarios.size() - 1); // scroll al final
            }
        });


        viewModel.cargarComentarios(idPublicacion);
    }

    private void configurarListeners() {
        DocumentoResponse documento = VerArchivoFragmentArgs.fromBundle(getArguments()).getDocumento();
        if (documento == null) return;

        binding.btnAbrirDocumento.setOnClickListener(v -> {
            if (archivoTemporalPdf != null && archivoTemporalPdf.exists()) {
                abrirArchivoLocal(archivoTemporalPdf);
            } else {
                Toast.makeText(getContext(), "Documento aún no está listo", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDescargarDocumento.setOnClickListener(v -> {
            if (archivoTemporalPdf != null && archivoTemporalPdf.exists()) {
                crearSelectorParaGuardarArchivo(archivoTemporalPdf.getName());
            } else {
                Toast.makeText(getContext(), "Documento aún no está disponible para descargar", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnEnviarComentario.setOnClickListener(v -> {
            if (!estaLogueado) {
                Toast.makeText(getContext(), "Inicia sesión para comentar", Toast.LENGTH_SHORT).show();
                return;
            }

            String texto = binding.edtNuevoComentario.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(getContext(), "Escribe un comentario", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.edtNuevoComentario.setText("");

            viewModel.crearComentario(requireContext(), texto, documento.getIdPublicacion())
                    .observe(getViewLifecycleOwner(), respuesta -> {
                        if (respuesta == null || !respuesta.isSuccess()) {
                            Toast.makeText(getContext(), "Error al publicar comentario", Toast.LENGTH_SHORT).show();
                        } else {
                            // Recargar comentarios
                            viewModel.cargarComentarios(documento.getIdPublicacion());
                        }
                    });
        });

        binding.btnLike.setOnClickListener(v -> {
            if (estaLogueado) {
                toggleLike();
            } else {
                Toast.makeText(getContext(), "Inicia sesión para dar like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarComentarios(int idPublicacion) {
        viewModel.getComentarios().observe(getViewLifecycleOwner(), nuevosComentarios -> {
            if (nuevosComentarios != null) {
                comentarios.clear();
                comentarios.addAll(nuevosComentarios);
                adapter.setComentarios(new ArrayList<>(comentarios));
            }
        });

        viewModel.cargarComentarios(idPublicacion);
    }
    private void configurarBotonLike(DocumentoResponse documento) {
        boolean isLiked = false; // Deberías obtener este valor de tu backend o base de datos local
        binding.btnLike.setImageResource(isLiked ? R.drawable.ic_like_filled : R.drawable.ic_like_outline);
        if (isLiked) {
            binding.btnLike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.rojo));
        }
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
    private void configurarDocumento() {
        DocumentoResponse documento = VerArchivoFragmentArgs.fromBundle(getArguments()).getDocumento();
        if (documento == null) return;

        binding.txtTituloDocumento.setText(documento.getTitulo());
        binding.txtSubtitulo.setText(documento.getResuContenido());
        binding.txtAutor.setText(String.format("Publicado por: %s", documento.getNombreCompleto()));
        binding.txtVisualizaciones.setText(formatNumber(documento.getNumeroVisualizaciones()) + " vistas");
        binding.txtDescargas.setText(formatNumber(documento.getNumeroDescargas()) + " descargas");
        binding.txtContadorMeGusta.setText(formatNumber(documento.getNumeroLiker()));
        binding.txtFecha.setText(formatTimeAgo(documento.getFecha()));
        binding.txtNivelEducativo.setText(documento.getNivelEducativo());
        binding.txtEstado.setText(documento.getEstado());
        setEstadoColor(documento.getEstado());
        descargarYMostrarImagen(documento.getRuta());
        descargarDocumentoTemporal(documento.getRuta());

        if (documento.getNumeroVisualizaciones() > 100 && !estaLogueado) {
            binding.txtAdvertencia.setVisibility(View.VISIBLE);
        }
    }
    private String formatNumber(int number) {
        if (number < 1000) return String.valueOf(number);
        if (number < 1000000) return String.format(Locale.getDefault(), "%.1fK", number / 1000.0);
        return String.format(Locale.getDefault(), "%.1fM", number / 1000000.0);
    }

    private String formatTimeAgo(String fecha) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date past = format.parse(fecha);
            Date now = new Date();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) return "hace unos segundos";
            if (minutes < 60) return minutes + " min atrás";
            if (hours < 24) return hours + " h atrás";
            return days + " días atrás";
        } catch (Exception e) {
            return fecha;
        }
    }

    private void descargarDocumentoTemporal(String rutaRemota) {
        fileServiceClient = new FileServiceClient();
        fileServiceClient.downloadPdf(rutaRemota, new FileServiceClient.DownloadCallback() {
            @Override
            public void onSuccess(byte[] fileData, String filename) {
                try {
                    File tempFile = new File(requireContext().getCacheDir(), filename);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(fileData);
                    fos.close();

                    archivoTemporalPdf = tempFile;

                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Documento cargado", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error al guardar temporalmente el documento", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error al descargar el documento", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setEstadoColor(String estado) {
        switch (estado.toLowerCase()) {
            case "publicado": binding.txtEstado.setTextColor(R.color.blue_light); break;
            case "pendiente": binding.txtEstado.setTextColor(R.color.purple_200); break;
            case "rechazado": binding.txtEstado.setTextColor(R.color.teal_200); break;
            default: binding.txtEstado.setTextColor(R.color.white);
        }
    }


    private void descargarYMostrarImagen(String coverImagePath) {
        if (coverImagePath == null || coverImagePath.isEmpty()) {
            binding.imgDocumento.setImageResource(R.drawable.ic_archivo);
            return;
        }
        fileServiceClient = new FileServiceClient();
        fileServiceClient.downloadCover(coverImagePath, new FileServiceClient.DownloadCallback() {
            @Override
            public void onSuccess(byte[] fileData, String filename) {
                requireActivity().runOnUiThread(() -> {
                    Bitmap coverBitmap = ImageUtil.binaryToBitmap(fileData);
                    binding.imgDocumento.setImageBitmap(coverBitmap);
                    binding.imgDocumento.setScaleType(ImageView.ScaleType.CENTER_CROP);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    binding.imgDocumento.setImageResource(R.drawable.ic_archivo);
                    Toast.makeText(requireContext(), "No se pudo cargar la portada", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void crearSelectorParaGuardarArchivo(String suggestedFileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);
        startActivityForResult(intent, REQUEST_CODE_CREATE_DOCUMENT);
    }

    private void abrirArchivoLocal(File file) {
        try {
            Uri uri = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file
            );

            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType != null ? mimeType : "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Abrir con"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "No se pudo abrir el documento", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_DOCUMENT && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uriDestino = data.getData();
                guardarArchivoEn(uriDestino);
            }
        }
    }

    private void guardarArchivoEn(Uri destinoUri) {
        try {
            if (archivoTemporalPdf == null || !archivoTemporalPdf.exists()) return;

            InputStream in = new FileInputStream(archivoTemporalPdf);
            OutputStream out = requireContext().getContentResolver().openOutputStream(destinoUri);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();

            Toast.makeText(getContext(), "Archivo guardado exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}